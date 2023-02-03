package at.nbsgames.explobattle;

import at.nbsgames.explobattle.enums.EnumBattleSchedulesStages;
import at.nbsgames.explobattle.enums.EnumConfigStrings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class Arena {

    public static final Component EXPLOBATTLE_TAG = Component.text("[", NamedTextColor.BLACK)
            .append(Component.text("ExploBattle", TextColor.color(0x0031EE)))
            .append(Component.text("]", NamedTextColor.BLACK));
    public static final Component SIGN_JOIN = Component.text("JOIN").color(NamedTextColor.DARK_GREEN);

    private static final LinkedHashMap<String, Arena> arenas = new LinkedHashMap<>();
    private static final LinkedHashMap<UUID, Arena> playerUuidToArena = new LinkedHashMap<>();

    private Sign sign;
    private boolean enabled = false;
    private String name;
    private List<Location> spawnPoints;

    private ArrayList<ExploBattlePlayer> currentPlayers;
    private EnumBattleSchedulesStages schedulesStage;

    public Arena(String name){
        this.name = name;
        currentPlayers = new ArrayList<>();
        schedulesStage = EnumBattleSchedulesStages.WAITING_FOR_PLAYERS;
    }

    public void reset(){
        currentPlayers = new ArrayList<>();
    }

    public ArrayList<ExploBattlePlayer> getCurrentPlayers() {
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
        arena.enabled = main.getMapConfig().getBoolean(CommandExploBattle.ARENA_PREFIX_WITH_POINT +  arenaName + ".enabled");

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

    public static boolean addPlayerToSystem(String mapName, Player player, Main main){
        Arena arena = arenas.get(mapName);
        if(arena == null) return false;

        if(arena.currentPlayers.size() >= arena.spawnPoints.size()){
            arena.writeSign();
            return false;
        }

        if(playerUuidToArena.containsKey(player.getUniqueId())){
            Arena are = playerUuidToArena.get(player.getUniqueId());

            String joinStrg =  main.getConfig().getString(EnumConfigStrings.TEXT_ALREADY_JOINED.toString()).replace("(PLAYER)", player.getName());
            joinStrg = joinStrg.replace("(ARENA)", are.name);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', joinStrg));
            return true;
        }

        arena.currentPlayers.add(new ExploBattlePlayer(player));
        playerUuidToArena.put(player.getUniqueId(), arena);


        arena.writeSign();

        String joinStrg =  main.getConfig().getString(EnumConfigStrings.TEXT_PLAYER_JOINED.toString()).replace("(PLAYER)", player.getName());
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', joinStrg));
        return true;
    }

    public static boolean removePlayerFromSystem(Player player){
        Arena arena = playerUuidToArena.get(player);

        if(arena == null) return false;

        Iterator<ExploBattlePlayer> iter = arena.currentPlayers.iterator();
        while(iter.hasNext()){
            ExploBattlePlayer p = iter.next();
            if(p.getBukkitPlayer() == player){
                p.restoreOldInv();
                iter.remove();
                break;
            }
        }


        arena.writeSign();
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
        sign.line(0, EXPLOBATTLE_TAG);
        sign.line(1, Component.text(text, color));
        sign.line(2, Component.text(name, NamedTextColor.BLACK));
        sign.line(3, Component.text(showPlayerCount ? currentPlayers.size() + " / " + spawnPoints.size() : "-----", NamedTextColor.BLACK));
        sign.update();
    }

}
