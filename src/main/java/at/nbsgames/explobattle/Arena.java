package at.nbsgames.explobattle;

import at.nbsgames.explobattle.enums.EnumBattleSchedulesStages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Arena {

    private static LinkedHashMap<String, Arena> arenas = new LinkedHashMap<>();

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
    public static Arena loadMap(Main main, String arenaName){
        Arena arena = new Arena(arenaName);
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
        arena.writeSign();
        return arena;
    }

    public boolean checkIfConfigurationIsComplete(){
        if(sign == null) return false;
        if(spawnPoints == null || spawnPoints.size() < 2) return false;
        return true;
    }



    public void writeSign(){
        sign.setEditable(false);
        sign.line(0, Component.text("[", NamedTextColor.BLACK)
                .append(Component.text("ExploBattle", NamedTextColor.BLUE))
                .append(Component.text("]", NamedTextColor.BLACK)));
        sign.line(1, Component.text("Join", NamedTextColor.DARK_GREEN));
        sign.line(2, Component.text(""));
        sign.line(3, Component.text("[", NamedTextColor.BLACK)
                .append(Component.text("ExploBattle", NamedTextColor.BLUE))
                .append(Component.text("]", NamedTextColor.BLACK)));
        System.out.println(sign.line(1));
        System.out.println(sign.line(2));
        System.out.println(sign.line(3));
    }

}
