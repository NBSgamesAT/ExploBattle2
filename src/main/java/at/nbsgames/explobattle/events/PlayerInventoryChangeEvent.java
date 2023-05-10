package at.nbsgames.explobattle.events;

import at.nbsgames.explobattle.Main;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class PlayerInventoryChangeEvent implements Listener {

    private Main main;
    public PlayerInventoryChangeEvent(Main main){
        this.main = main;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerSwitchItemInHand(PlayerItemHeldEvent event){
        if(main.getArenaManager().isInActiveMatch(event.getPlayer())){
            ItemStack stack = event.getPlayer().getInventory().getItem(event.getNewSlot());

            if(stack == null || stack.getType() != Material.NETHERITE_SWORD){
                event.getPlayer().getInventory().setItemInOffHand(null);
            }
            else{
                event.getPlayer().getInventory().setItemInOffHand(makeShield());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerSwapHands(PlayerSwapHandItemsEvent event){
        if(main.getArenaManager().isInActiveMatch(event.getPlayer())){
            event.setCancelled(true);
        }
    }

    public ItemStack makeShield(){
        ItemStack stack = new ItemStack(Material.SHIELD);
        stack.getItemMeta().setUnbreakable(true);
        stack.getItemMeta().addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        return stack;
    }
}
