package at.nbsgames.explobattle.events;

import at.nbsgames.explobattle.Main;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

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
        else if(ExplosionEvent.bazooka.contains(id) && (event.getHitEntity() == null || (event.getHitEntity().getType() != EntityType.ARROW && event.getHitEntity().getType() != EntityType.PRIMED_TNT && event.getHitEntity().getType() != EntityType.SNOWBALL))){
            ExplosionEvent.bazooka.remove((Integer) id);
            Location loc = event.getEntity().getLocation();
            loc.getWorld().createExplosion(event.getEntity(), 5, false, false);
            event.getEntity().remove();
        }
        else if(ExplosionEvent.ultraBazooka.contains(id) && (event.getHitEntity() == null || (event.getHitEntity().getType() != EntityType.ARROW && event.getHitEntity().getType() != EntityType.PRIMED_TNT && event.getHitEntity().getType() != EntityType.SNOWBALL))){
            ExplosionEvent.ultraBazooka.remove((Integer) id);
            Location loc = event.getEntity().getLocation();

            createTnt(loc, new Vector(0, 0, 0), 5);

            createTnt(loc, new Vector(0.25, 0.125, 0.25), 8);
            createTnt(loc, new Vector(0.25, 0.125, -0.25), 8);
            createTnt(loc, new Vector(-0.25, 0.125, 0.25), 8);
            createTnt(loc, new Vector(-0.25, 0.125, -0.25), 8);

            createTnt(loc, new Vector(0.5, 0.125, 0.5), 11);
            createTnt(loc, new Vector(0.5, 0.125, -0.5), 11);
            createTnt(loc, new Vector(-0.5, 0.125, 0.5), 11);
            createTnt(loc, new Vector(-0.5, 0.125, -0.5), 11);

            createTnt(loc, new Vector(0, 0.125, 0.5), 11);
            createTnt(loc, new Vector(0, 0.125, -0.5), 11);
            createTnt(loc, new Vector(0.5, 0.125, 0), 11);
            createTnt(loc, new Vector(-0.5, 0.125, 0), 11);
        }
        else if(ExplosionEvent.ultraBazooka.contains(id) && event.getHitEntity() != null && (event.getHitEntity().getType() == EntityType.ARROW || event.getHitEntity().getType() == EntityType.PRIMED_TNT || event.getHitEntity().getType() == EntityType.SNOWBALL)){
            event.setCancelled(true);
        }
    }

    private void createTnt(Location loc, Vector vec, int ticksTillExplosion){
        TNTPrimed primed = loc.getWorld().spawn(loc, TNTPrimed.class);
        primed.setFuseTicks(ticksTillExplosion);
        primed.setVelocity(vec);
        ExplosionEvent.tnt.add(primed.getEntityId());
    }

}