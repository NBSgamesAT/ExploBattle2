package at.nbsgames.explobattle.events;

import at.nbsgames.explobattle.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDisconnectEvent implements Listener {

    public Main main;
    public PlayerDisconnectEvent(Main main){
        this.main = main;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDisconnect(PlayerQuitEvent e){
        main.getArenaManager().playerDisconnect(e.getPlayer());
    }

}
