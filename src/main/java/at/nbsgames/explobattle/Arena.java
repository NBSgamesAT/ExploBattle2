package at.nbsgames.explobattle;

import at.nbsgames.explobattle.enums.EnumBattleSchedulesStages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Arena {

    private static final LinkedHashMap<String, Arena> arenas = new LinkedHashMap<>();

    private Sign sign;
    private boolean enabled = false;
    private String name;
    private List<Location> spawnPoints;

    private ArrayList<Player> currentPlayers;
    private EnumBattleSchedulesStages schedulesStage;

    public Arena(String name){
        this.name = name;
        currentPlayers = new ArrayList<>();
        schedulesStage = EnumBattleSchedulesStages.WAITING_FOR_PLAYERS;
    }

    public void reset(){
        currentPlayers = new ArrayList<>();
    }

    public ArrayList<Player> getCurrentPlayers() {
        return currentPlayers;
    }
    public List<Location> getSpawnPoints() {
        return spawnPoints;
    }
    public Sign getSign() {
        return sign;
    }

    /*
     * HELPING STATITCS FOR LOADING
     */
    public static boolean loadArena(Main main, String arenaName){
        Arena arena = Arena.arenas.get(arenaName);
        if(arena == null){
            arena = new Arena(arenaName);
        }

        Location sign = main.getMapConfig().getBlockLocation(CommandExploBattle.ARENA_PREFIX_WITH_POINT + arenaName + ".sign");
        List<String> locationStrings = main.getMapConfig().getStringList(CommandExploBattle.ARENA_PREFIX_WITH_POINT + arenaName + ".spawns");

        if(sign != null && sign.getBlock().getState() instanceof Sign){
            arena.sign = (Sign) sign.getBlock().getState();
        }
        if(locationStrings != null){
            arena.spawnPoints = locationStrings.stream().map(MapConfig::getEntityLocationByLocationString).toList();
        }
        arena.enabled = main.getMapConfig().getBoolean(CommandExploBattle.ARENA_PREFIX_WITH_POINT +  arenaName + ".enalbed");

        // If the enabled doesn't exist, it's just going to be false...
        arenas.put(arenaName, arena);
        if(!arena.checkIfConfigurationIsComplete()){
            arena.schedulesStage = EnumBattleSchedulesStages.SPECIAL_PRE_CONFIG;
            if(arena.sign != null) arena.writeSign();
            return false;
        }
        else{
            arena.schedulesStage = EnumBattleSchedulesStages.WAITING_FOR_PLAYERS;
            arena.writeSign();
            return true;
        }
    }

    public boolean checkIfConfigurationIsComplete(){
        if(sign == null) return false;
        if(spawnPoints == null || spawnPoints.size() < 2) return false;
        return true;
    }



    public void writeSign(){
        String text;
        TextColor color;
        boolean showPlayerCount;
        if(schedulesStage == EnumBattleSchedulesStages.SPECIAL_PRE_CONFIG || !enabled){
            text = "DISABLED";
            color = NamedTextColor.DARK_RED;
            showPlayerCount = false;
        }
        else if(schedulesStage == EnumBattleSchedulesStages.STARTING_COUNTDOWN || schedulesStage == EnumBattleSchedulesStages.WAITING_FOR_PLAYERS){
            if(currentPlayers.size() == spawnPoints.size()){
                text = "FULL";
                color = NamedTextColor.RED;
                showPlayerCount = true;
            }
            else{
                text = "JOIN";
                color = NamedTextColor.DARK_GREEN;
                showPlayerCount = true;
            }
        }
        else{
            text = "IN PROGRESS";
            color = NamedTextColor.RED;
            showPlayerCount = false;
        }
        sign.setEditable(false);
        sign.line(0, Component.text("[", NamedTextColor.BLACK)
                .append(Component.text("ExploBattle", TextColor.color(0x0031EE))
                .append(Component.text("]", NamedTextColor.BLACK))));
        sign.line(1, Component.text(text, color));
        sign.line(2, Component.text(name, NamedTextColor.BLACK));
        sign.line(3, Component.text(showPlayerCount ? currentPlayers.size() + " / " + spawnPoints.size() : "-----", NamedTextColor.BLACK));
        sign.update();
    }

}
