package at.nbsgames.explobattle.events;

import at.nbsgames.explobattle.Main;
import at.nbsgames.explobattle.enums.EnumConfigStrings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerThrowsSomethingEvent implements Listener {

    private Main main;
    public PlayerThrowsSomethingEvent(Main main){
        this.main = main;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerThrowsSomething(ProjectileLaunchEvent e){

        if(!(e.getEntity().getShooter() instanceof Player)) return;

        Player p = (Player) e.getEntity().getShooter();
        ItemStack item = p.getInventory().getItemInMainHand();
        if(item.getItemMeta() != null && item.getItemMeta().displayName() != null && item.getItemMeta().displayName().equals(EnumConfigStrings.ITEMS_ULTRA_GRENADE_NAME.translate())){
            ExplosionEvent.ultraBazooka.add(e.getEntity().getEntityId());
            if(main.getArenaManager().isInActiveMatch(p)){
                int mainHandSlot = p.getInventory().getHeldItemSlot();

                // Look this needs be delayed cuz otherwise this thing ain't working
                Bukkit.getScheduler().runTaskLater(main, () -> {
                    p.getInventory().setItem(mainHandSlot, Main.makeItemWithName(Material.GUNPOWDER, EnumConfigStrings.ITEMS_ULTRA_GRENADE_COOLDOWN.translate()).asQuantity(8));
                }, 5);
            }
        }
    }

}
