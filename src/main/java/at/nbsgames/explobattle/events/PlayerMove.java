package at.nbsgames.explobattle.events;

import at.nbsgames.explobattle.Main;
import at.nbsgames.explobattle.enums.EnumBattleSchedulesStages;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMove implements Listener {

    final private Main main;
    public PlayerMove(Main main){
        this.main = main;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event){
        EnumBattleSchedulesStages stage = main.getArenaManager().getBattleSchedules(event.getPlayer());
        if(stage == null) return;
        if(stage == EnumBattleSchedulesStages.FIGHT_COUNTDOWN){
            if(event.getFrom().getX() != event.getTo().getX() ||
                    event.getFrom().getZ() != event.getTo().getZ()){
                event.setCancelled(true);
            }
        }

    }

}
