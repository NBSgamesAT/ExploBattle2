package at.nbsgames.explobattle.events;

import at.nbsgames.explobattle.Arena;
import at.nbsgames.explobattle.Main;
import at.nbsgames.explobattle.enums.EnumConfigStrings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

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

        if(event.getPlayer().getInventory().getItemInMainHand().getType() != Material.NETHERITE_HOE &&
                event.getPlayer().getInventory().getItemInMainHand().getType() != Material.TNT &&
                event.getPlayer().getInventory().getItemInMainHand().getType() != Material.NETHERITE_AXE) return;

        if

    }

    public static final Component GUN = Component.text("Gun").decorate(TextDecoration.BOLD).color(NamedTextColor.GOLD);
    public static final Component GRENADE = Component.text("Grenade").decorate(TextDecoration.BOLD).color(NamedTextColor.GOLD);
    public static final Component ULTRA_GRENADE = Component.text("Ultra Grenade").decorate(TextDecoration.BOLD).color(NamedTextColor.GOLD);
    public static final Component BAZOOK = Component.text("Bazooka").decorate(TextDecoration.BOLD).color(NamedTextColor.GOLD);


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
