package at.nbsgames.explobattle.events;

import at.nbsgames.explobattle.arenas.Arena;
import at.nbsgames.explobattle.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PlayerDeathEvent implements Listener {

    public Main main;
    public PlayerDeathEvent(Main main){
        this.main = main;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(org.bukkit.event.entity.PlayerDeathEvent e){
        boolean info = main.getArenaManager().playerDied(e.getPlayer());
        e.setCancelled(info);
    }


}
