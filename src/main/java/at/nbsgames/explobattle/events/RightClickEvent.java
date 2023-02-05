package at.nbsgames.explobattle.events;

import at.nbsgames.explobattle.Arena;
import at.nbsgames.explobattle.Main;
import at.nbsgames.explobattle.enums.EnumConfigStrings;
import at.nbsgames.explobattle.enums.EnumPermissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class RightClickEvent implements Listener {

    private Main main;
    public RightClickEvent(Main main){
        this.main = main;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRightClick(PlayerInteractEvent event){
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
            if(onRightClickSign(event)) return;
        }

        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(event.getPlayer().getInventory().getItemInMainHand().getType() != Material.NETHERITE_HOE &&
                event.getPlayer().getInventory().getItemInMainHand().getType() != Material.TNT &&
                event.getPlayer().getInventory().getItemInMainHand().getType() != Material.NETHERITE_AXE) return;
        if(!Arena.isInActiveMatch(event.getPlayer()) && !event.getPlayer().hasPermission(EnumPermissions.USE_WEAPONS_OUTSIDE_ARENA.toString())) return;

        ItemStack stack = event.getPlayer().getInventory().getItemInMainHand();
        if(stack.getType() == Material.NETHERITE_HOE && stack.getItemMeta().displayName().equals(GUN)){
            Arrow arrow = event.getPlayer().launchProjectile(Arrow.class);
            Vector vec = arrow.getVelocity();
            vec.multiply(1.25);
            arrow.setVelocity(vec);
            arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            ProjectileHitSomethingEvent.gun.add(arrow.getEntityId());
        }
        else if(stack.getType() == Material.TNT && stack.getItemMeta().displayName().equals(GRENADE)){
            TNTPrimed primed = event.getPlayer().getWorld().spawn(event.getPlayer().getEyeLocation(), TNTPrimed.class);
            Vector vec = event.getPlayer().getLocation().getDirection().normalize();
            vec.multiply(2);
            primed.setVelocity(vec);
            ExplosionEvent.tnt.add(primed.getEntityId());
        }
        else if(stack.getType() == Material.NETHERITE_AXE && stack.getItemMeta().displayName().equals(BAZOOKA)){
            Arrow arrow = event.getPlayer().launchProjectile(Arrow.class);
            Vector vec = arrow.getVelocity();
            vec.multiply(0.75);
            arrow.setVelocity(vec);
            arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            ExplosionEvent.bazooka.add(arrow.getEntityId());
        }

    }

    public static final Component GUN = Component.text("Gun").decorate(TextDecoration.BOLD).color(NamedTextColor.GOLD);
    public static final Component GRENADE = Component.text("Grenade").decorate(TextDecoration.BOLD).color(NamedTextColor.GOLD);
    public static final Component ULTRA_GRENADE = Component.text("Ultra Grenade").decorate(TextDecoration.BOLD).color(NamedTextColor.GOLD);
    public static final Component BAZOOKA = Component.text("Bazooka").decorate(TextDecoration.BOLD).color(NamedTextColor.GOLD);


    private boolean onRightClickSign(PlayerInteractEvent event){

        Block block = event.getClickedBlock();

        if(!(block.getState() instanceof Sign)) return false;

        Sign sign = (Sign) block.getState();
        if(!sign.line(0).equals(Arena.EXPLOBATTLE_TAG)){
            return false;
        }

        if(!sign.line(1).equals(Arena.SIGN_JOIN)) return false;

        String mapName = PlainTextComponentSerializer.plainText().serialize(sign.line(2));

        boolean result = Arena.addPlayerToSystem(mapName, event.getPlayer(), main);

        if(!result){
            String joinStrg =  main.getConfig().getString(EnumConfigStrings.PLAYER_JOIN_FAILURE.toString()).replace("(PLAYER)", event.getPlayer().getName());
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', joinStrg));
        }

        return true;
    }

}
