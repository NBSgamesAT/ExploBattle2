package at.nbsgames.explobattle.events;

import at.nbsgames.explobattle.Main;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;

public class ProjectileHitSomethingEvent implements Listener {

    private Main main;
    public ProjectileHitSomethingEvent(Main main){
        this.main = main;
    }

    public static ArrayList<Integer> gun = new ArrayList<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExplosion(ProjectileHitEvent event){
        int id = event.getEntity().getEntityId();
        if(gun.remove((Integer) id)){
            event.getEntity().remove();
        }
        else if(ExplosionEvent.bazooka.contains(id)){
            Location loc = event.getEntity().getLocation();
            loc.getWorld().createExplosion(event.getEntity(), 10, false);
        }
    }
}