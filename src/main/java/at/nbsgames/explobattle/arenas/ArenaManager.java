package at.nbsgames.explobattle.arenas;

import at.nbsgames.explobattle.Main;
import at.nbsgames.explobattle.enums.EnumBattleSchedulesStages;
import at.nbsgames.explobattle.enums.EnumConfigStrings;
import at.nbsgames.explobattle.exceptions.ExploBattleExceptions;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.UUID;

public class ArenaManager {

    final LinkedHashMap<String, Arena> arenas = new LinkedHashMap<>();
    final LinkedHashMap<UUID, Arena> playerUuidToArena = new LinkedHashMap<>();

    final Main main;
    public ArenaManager(Main main) {
        this.main = main;
    }

    public boolean loadArena(String arenaName){
        Arena arena = this.arenas.get(arenaName);
        if(arena == null){
            arena = new Arena(arenaName, this);
            this.arenas.put(arenaName, arena);
        }

        return arena.loadArena(this.main);
    }

    public boolean isInActiveMatch(Player player){
        Arena arena = playerUuidToArena.get(player.getUniqueId());
        if(arena == null) return false;

        if(arena.getSchedulesStage() == EnumBattleSchedulesStages.WAIT_COUNTDOWN ||
                arena.getSchedulesStage() == EnumBattleSchedulesStages.SPECIAL_PRE_CONFIG ||
                arena.getSchedulesStage() == EnumBattleSchedulesStages.WAITING_FOR_PLAYERS){
            return false;
        }
        return true;
    }
    public boolean isInAnyMatch(Player player){
        Arena arena = playerUuidToArena.get(player.getUniqueId());
        if(arena == null) return false;
        return true;
    }

    public void addPlayer(Player player, String arenaName) throws ExploBattleExceptions {
        if(this.playerUuidToArena.containsKey(player.getUniqueId())){
            throw new ExploBattleExceptions(EnumConfigStrings.PLAYER_ALREADY_JOINED.translateWithArena(this.playerUuidToArena.get(player.getUniqueId()).getName()));
        }
        Arena arena = this.arenas.get(arenaName);
        if(arena == null){
            throw new ExploBattleExceptions(EnumConfigStrings.PLAYER_JOIN_FAILURE.translate());
        }

        arena.addPlayer(player);
        playerUuidToArena.put(player.getUniqueId(), arena);
    }

    public boolean playerDied(Player p){
        Arena arena = playerUuidToArena.get(p.getUniqueId());
        if(arena == null) return false;
        if(arena.getSchedulesStage() != EnumBattleSchedulesStages.FIGHTING && arena.getSchedulesStage() != EnumBattleSchedulesStages.POST_FIGHT && arena.getSchedulesStage() != EnumBattleSchedulesStages.FIGHT_COUNTDOWN) return false;

        arena.playerDies(p);
        return true;
    }
    public void playerLeaves(Player p) throws ExploBattleExceptions {
        Arena arena = playerUuidToArena.remove(p.getUniqueId());
        if(arena == null) throw new ExploBattleExceptions(EnumConfigStrings.PLAYER_LEAVE_NOT_INGAME.translate());

        arena.playerLeaves(p, true);
    }
    public void playerDisconnect(Player p) {
        Arena arena = playerUuidToArena.remove(p.getUniqueId());
        if(arena == null) return;

        arena.playerLeaves(p, false);
    }

    public int getSpawnPointsOfArena(String arenaName){
        Arena arena = this.arenas.get(arenaName);
        if(arena == null) return -1;
        return arena.getSpawnPoints().size();
    }

    void removePlayerFromList(Player p){
        playerUuidToArena.remove(p.getUniqueId());
    }

    @Nullable
    public EnumBattleSchedulesStages getBattleSchedules(Player p){
        Arena arena = playerUuidToArena.get(p.getUniqueId());
        if(arena == null) return null;

        return arena.getSchedulesStage();
    }

    public void removeArena(String name){
        Arena arena = arenas.remove(name);
        if(arena == null) return;

        arena.deleteSign();
    }

}
