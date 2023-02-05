package at.nbsgames.explobattle.events;

import at.nbsgames.explobattle.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;

public class ExplosionEvent implements Listener {

    private Main main;
    public ExplosionEvent(Main main){
        this.main = main;
    }

    public static ArrayList<Integer> tnt = new ArrayList<>();
    public static ArrayList<Integer> bazooka = new ArrayList<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExplosion(EntityExplodeEvent event){
        if(tnt.remove((Integer) event.getEntity().getEntityId())){
            event.blockList().clear(); // Clearing the blocks
        }
        if(bazooka.remove((Integer) event.getEntity().getEntityId())){
            event.blockList().clear(); // Clearing the blocks
            event.getEntity().remove();
        }
    }
}
