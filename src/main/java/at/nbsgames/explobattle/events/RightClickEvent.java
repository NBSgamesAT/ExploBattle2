package at.nbsgames.explobattle.events;

import at.nbsgames.explobattle.arenas.Arena;
import at.nbsgames.explobattle.Main;
import at.nbsgames.explobattle.enums.EnumConfigStrings;
import at.nbsgames.explobattle.enums.EnumPermissions;
import at.nbsgames.explobattle.exceptions.ExploBattleExceptions;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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
        if(!main.getArenaManager().isInActiveMatch(event.getPlayer()) && !event.getPlayer().hasPermission(EnumPermissions.USE_WEAPONS_OUTSIDE_ARENA.toString())) return;

        ItemStack stack = event.getPlayer().getInventory().getItemInMainHand();
        if(stack.getType() == Material.NETHERITE_HOE && stack.getItemMeta().displayName() != null && stack.getItemMeta().displayName().equals(EnumConfigStrings.ITEMS_GUN_NAME.translate())){
            Arrow arrow = event.getPlayer().launchProjectile(Arrow.class);
            Vector vec = arrow.getVelocity();
            vec.multiply(1.25);
            arrow.setVelocity(vec);
            arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            ProjectileHitSomethingEvent.gun.add(arrow.getEntityId());
        }
        else if(stack.getType() == Material.TNT && stack.getItemMeta().displayName() != null && stack.getItemMeta().displayName().equals(EnumConfigStrings.ITEMS_GRENADE_NAME.translate())){
            TNTPrimed primed = event.getPlayer().getWorld().spawn(event.getPlayer().getEyeLocation(), TNTPrimed.class);
            Vector vec = event.getPlayer().getLocation().getDirection().normalize();
            vec.multiply(2);
            primed.setVelocity(vec);
            ExplosionEvent.tnt.add(primed.getEntityId());
        }
        else if(stack.getType() == Material.NETHERITE_AXE && stack.getItemMeta().displayName() != null && stack.getItemMeta().displayName().equals(EnumConfigStrings.ITEMS_BAZOOKA_NAME.translate())){
            Arrow arrow = event.getPlayer().launchProjectile(Arrow.class);
            Vector vec = arrow.getVelocity();
            vec.multiply(0.75);
            arrow.setVelocity(vec);
            arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            ExplosionEvent.bazooka.add(arrow.getEntityId());
            if(main.getArenaManager().isInActiveMatch(event.getPlayer()))
                event.getPlayer().getInventory().setItemInMainHand(Main.makeItemWithName(Material.GUNPOWDER, EnumConfigStrings.ITEMS_BAZOOKA_COOLDOWN.translate()).asQuantity(5));
        }

    }


    private boolean onRightClickSign(PlayerInteractEvent event){

        Block block = event.getClickedBlock();

        if(!(block.getState() instanceof Sign)) return false;

        Sign sign = (Sign) block.getState();
        if(!sign.line(0).equals(Arena.EXPLOBATTLE_TAG)){
            return false;
        }

        if(!sign.line(1).equals(Arena.SIGN_JOIN)) return false;

        String mapName = PlainTextComponentSerializer.plainText().serialize(sign.line(2));

        try{
            this.main.getArenaManager().addPlayer(event.getPlayer(), mapName);
        } catch (ExploBattleExceptions e) {
            event.getPlayer().sendMessage(e.getComponent());
        }

        return true;
    }

}
