package at.nbsgames.explobattle.events;

import at.nbsgames.explobattle.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.LinkedList;

public class ExplosionEvent implements Listener {

    private Main main;
    public ExplosionEvent(Main main){
        this.main = main;
    }

    public static LinkedList<Integer> tnt = new LinkedList<>();
    public static LinkedList<Integer> bazooka = new LinkedList<>();
    public static LinkedList<Integer> ultraBazooka = new LinkedList<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExplosion(EntityExplodeEvent event){
        if(tnt.remove((Integer) event.getEntity().getEntityId())){
            event.blockList().clear(); // Clearing the blocks
        }
    }
}
