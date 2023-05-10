package at.nbsgames.explobattle.arenas;

import at.nbsgames.explobattle.Main;
import at.nbsgames.explobattle.MapConfig;
import at.nbsgames.explobattle.enums.EnumBattleSchedulesStages;
import at.nbsgames.explobattle.enums.EnumConfigStrings;
import at.nbsgames.explobattle.exceptions.ExploBattleExceptions;
import at.nbsgames.explobattle.utility.BukkitTaskWrapper;
import at.nbsgames.explobattle.utility.ExploBattlePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Arena {

    public static final Component EXPLOBATTLE_TAG = Component.text("[", NamedTextColor.BLACK)
            .append(Component.text("ExploBattle", TextColor.color(0x0031EE)))
            .append(Component.text("]", NamedTextColor.BLACK));
    public static final Component SIGN_JOIN = Component.text("JOIN").color(NamedTextColor.DARK_GREEN);

    private BukkitTaskWrapper task;
    final private ArenaManager manager;

    private Sign sign;
    private boolean enabled = false;
    final private String name;
    private List<Location> spawnPoints;

    private LinkedHashMap<UUID, ExploBattlePlayer> currentPlayers;
    private LinkedHashMap<UUID, ExploBattlePlayer> spectators;
    private EnumBattleSchedulesStages schedulesStage;
    private int minPlayers;

    Arena(String name, ArenaManager manager){
        this.name = name;
        currentPlayers = new LinkedHashMap<>();
        spectators = new LinkedHashMap<>();
        schedulesStage = EnumBattleSchedulesStages.WAITING_FOR_PLAYERS;
        this.manager = manager;
    }

    boolean loadArena(Main main){

        Location sign = main.getMapConfig().getBlockLocation(MapConfig.ARENA_PREFIX_WITH_POINT + this.name + ".sign");
        List<String> locationStrings = main.getMapConfig().getStringList(MapConfig.ARENA_PREFIX_WITH_POINT + this.name + ".spawns");

        if(sign != null && sign.getBlock().getState() instanceof Sign){
            this.sign = (Sign) sign.getBlock().getState();
        }
        if(locationStrings != null){
            this.spawnPoints = locationStrings.stream().map(MapConfig::getEntityLocationByLocationString).toList();
        }
        this.enabled = main.getMapConfig().getBoolean(MapConfig.ARENA_PREFIX_WITH_POINT + this.name + ".enabled");
        if(main.getMapConfig().exists(MapConfig.ARENA_PREFIX_WITH_POINT + this.name + ".minPlayers")){
            this.minPlayers = main.getMapConfig().getInt(MapConfig.ARENA_PREFIX_WITH_POINT + this.name + ".minPlayers");
        }
        else{
            this.minPlayers = 2;
        }

        // If the enabled doesn't exist, it's just going to be false..
        if(!this.checkIfConfigurationIsComplete()){
            this.schedulesStage = EnumBattleSchedulesStages.SPECIAL_PRE_CONFIG;
            if(this.sign != null) this.writeSign();
            return false;
        }
        else{
            this.schedulesStage = EnumBattleSchedulesStages.WAITING_FOR_PLAYERS;
            this.writeSign();
            return true;
        }
    }

    public LinkedHashMap<UUID, ExploBattlePlayer> getCurrentPlayers() {
        return currentPlayers;
    }
    public List<Location> getSpawnPoints() {
        return spawnPoints;
    }
    public Sign getSign() {
        return sign;
    }

    public boolean checkIfConfigurationIsComplete(){
        if(sign == null) return false;
        if(spawnPoints == null || spawnPoints.size() < 2) return false;
        return true;
    }
    void addPlayer(Player player) throws ExploBattleExceptions {
        if(currentPlayers.containsKey(player.getUniqueId()) || spectators.containsKey(player.getUniqueId()))
            throw new ExploBattleExceptions(EnumConfigStrings.PLAYER_JOIN_FAILURE.translate());
        if(currentPlayers.size() == spawnPoints.size())
            throw new ExploBattleExceptions(EnumConfigStrings.PLAYER_JOIN_ARENA_FULL.translate());
        if(schedulesStage != EnumBattleSchedulesStages.WAIT_COUNTDOWN && schedulesStage != EnumBattleSchedulesStages.WAITING_FOR_PLAYERS)
            throw new ExploBattleExceptions(EnumConfigStrings.PLAYER_JOIN_MATCH_ONGOING.translate());

        currentPlayers.put(player.getUniqueId(), new ExploBattlePlayer(player));
        Component message = EnumConfigStrings.PLAYER_JOINED.translateWithPlayer(player.getName());
        currentPlayers.values().forEach((exp) -> exp.getBukkitPlayer().sendMessage(message));
        if(currentPlayers.size() < spawnPoints.size() && currentPlayers.size() >= minPlayers && schedulesStage == EnumBattleSchedulesStages.WAITING_FOR_PLAYERS){
            this.startCountdown();
        }
        else if(currentPlayers.size() == spawnPoints.size() && (schedulesStage == EnumBattleSchedulesStages.WAITING_FOR_PLAYERS || schedulesStage == EnumBattleSchedulesStages.WAIT_COUNTDOWN)){
            if(schedulesStage == EnumBattleSchedulesStages.WAIT_COUNTDOWN){
                if(task != null) task.cancel();
                task = null;
            }
            this.startMatch();
        }

        this.writeSign();
    }

    private void startCountdown(){
        this.schedulesStage = EnumBattleSchedulesStages.WAIT_COUNTDOWN;

        AtomicInteger i = new AtomicInteger(45);
        task = new BukkitTaskWrapper(name + "_WAITING", Bukkit.getScheduler().runTaskTimer(getMain(), () -> {
            if(i.get() == 0){
                task.cancel();
                task = null;
                this.startMatch();
                return;
            }
            Component message = EnumConfigStrings.WAIT_GAME_STARTS_IN.translateWithSeconds(i.getAndDecrement());
            for(ExploBattlePlayer player : currentPlayers.values()){
                player.getBukkitPlayer().sendActionBar(message);
            }
        }, 0, 20));
    }

    void startMatch(){
        if(this.currentPlayers.size() < 2) return;
        if(schedulesStage != EnumBattleSchedulesStages.WAIT_COUNTDOWN && schedulesStage != EnumBattleSchedulesStages.WAITING_FOR_PLAYERS) return;

        schedulesStage = EnumBattleSchedulesStages.FIGHT_COUNTDOWN;

        Random r = new Random();
        ArrayList<Location> locs = new ArrayList<>(this.spawnPoints);
        for(ExploBattlePlayer p : currentPlayers.values()){
            int posIndex = r.nextInt(locs.size());
            p.saveGameMode();
            p.getBukkitPlayer().teleport(locs.get(posIndex));
            p.saveAndClearInventory();
            p.getBukkitPlayer().setGameMode(GameMode.ADVENTURE);
            locs.remove(posIndex);
        }

        AtomicInteger i = new AtomicInteger(10);
        task = new BukkitTaskWrapper(name + "_STARTING_FIGHT", Bukkit.getScheduler().runTaskTimer(getMain(), () -> {
            if(i.get() <= 0){
                task.cancel();
                task = null;
                handleMatch();

                Component text = EnumConfigStrings.BATTLE_STARTED.translate();
                Title title = Title.title(text, Component.empty(), Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(3), Duration.ofSeconds(1)));
                currentPlayers.values().forEach(p -> p.getBukkitPlayer().showTitle(title));
                return;
            }
            if(i.get() == 10 || i.get() <= 5){
                Component text = EnumConfigStrings.BATTLE_START_IN.translateWithSeconds(i.get());
                Title title = Title.title(text, Component.empty(), Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(2), Duration.ofSeconds(0)));
                currentPlayers.values().forEach(p -> p.getBukkitPlayer().showTitle(title));
            }
            i.getAndDecrement();
        }, 0, 20));

        this.writeSign();
    }

    public void handleMatch(){
        schedulesStage = EnumBattleSchedulesStages.FIGHTING;

        currentPlayers.values().forEach(exp -> {
            exp.equipPlayer();
            exp.getBukkitPlayer().setHealth(20);
            exp.getBukkitPlayer().setFoodLevel(20);
            exp.getBukkitPlayer().setSaturation(3f);
        });

        task = new BukkitTaskWrapper(name + "_FIGHTING", Bukkit.getScheduler().runTaskTimer(getMain(), () -> {
            for (ExploBattlePlayer value : currentPlayers.values()) {
                Player p = value.getBukkitPlayer();
                for (int i = 0; i < p.getInventory().getContents().length; ++i) {
                    ItemStack content = p.getInventory().getContents()[i];

                    if(content == null || content.getType() != Material.GUNPOWDER) continue;

                    if(content.getAmount() <= 1){
                        if(content.getItemMeta() == null) continue;
                        if(content.getItemMeta().displayName().equals(EnumConfigStrings.ITEMS_ULTRA_GRENADE_COOLDOWN.translate())){
                            content = Main.makeItemWithName(Material.SNOWBALL, EnumConfigStrings.ITEMS_ULTRA_GRENADE_NAME.translate());
                        }
                        else if(content.getItemMeta().displayName().equals(EnumConfigStrings.ITEMS_BAZOOKA_COOLDOWN.translate())){
                            content = Main.makeItemWithName(Material.NETHERITE_AXE, EnumConfigStrings.ITEMS_BAZOOKA_NAME.translate());
                        }
                        else if(content.getItemMeta().displayName().equals(EnumConfigStrings.ITEMS_COBWEB_COOLDOWN.translate())){
                            content = Main.makeItemWithName(Material.COBWEB, EnumConfigStrings.ITEMS_COBWEB_NAME.translate());
                        }
                        p.getInventory().setItem(i, content);
                    }
                    else{
                        content.setAmount(content.getAmount() - 1);

                    }
                }
            }
        }, 0, 20));
    }

    void playerDies(Player player){
        spectators.put(player.getUniqueId(), currentPlayers.get(player.getUniqueId()));
        currentPlayers.remove(player.getUniqueId());
        player.setGameMode(GameMode.SPECTATOR);

        Component component = EnumConfigStrings.PLAYER_DIED.translateWithPlayer(player.getName());
        currentPlayers.values().forEach(p -> p.getBukkitPlayer().sendMessage(component));
        spectators.values().forEach(p -> p.getBukkitPlayer().sendMessage(component));

        if(currentPlayers.size() < 2){
            this.endBattle();
        }
    }
    void playerLeaves(Player player, boolean left){
        ExploBattlePlayer exp = currentPlayers.remove(player.getUniqueId());
        if(exp == null) exp = spectators.remove(player.getUniqueId());

        if(exp == null) return;

        Component component = left ? EnumConfigStrings.PLAYER_LEFT.translateWithPlayer(player.getName()) : EnumConfigStrings.PLAYER_DISCONNECTED.translateWithPlayer(player.getName());
        currentPlayers.values().forEach(p -> p.getBukkitPlayer().sendMessage(component));
        spectators.values().forEach(p -> p.getBukkitPlayer().sendMessage(component));

        manager.removePlayerFromList(exp.getBukkitPlayer());

        if(schedulesStage == EnumBattleSchedulesStages.WAITING_FOR_PLAYERS){
            this.writeSign();
            return;
        }
        if(schedulesStage == EnumBattleSchedulesStages.WAIT_COUNTDOWN && currentPlayers.size() < minPlayers){
            if(task != null) task.cancel();
            task = null;
            schedulesStage = EnumBattleSchedulesStages.WAITING_FOR_PLAYERS;


            Component comp = EnumConfigStrings.BATTLE_TOO_FEW_PLAYERS.translate();
            currentPlayers.values().forEach(p -> p.getBukkitPlayer().sendMessage(comp));
            this.writeSign();
            return;
        }

        Location loc = MapConfig.getEntityLocationByLocationString(getMain().getConfig().getString("general.spawn"));
        this.bringPlayerBack(exp, loc);

        if(currentPlayers.size() < 2){
            this.endBattle();
        }
        this.writeSign();
    }

    private void endBattle(){
        if(schedulesStage == EnumBattleSchedulesStages.POST_FIGHT) return;
        schedulesStage = EnumBattleSchedulesStages.POST_FIGHT;

        if(task != null) task.cancel();
        task = null;

        if(currentPlayers.size() == 1){
            Component message = EnumConfigStrings.PLAYER_WON.translateWithPlayer(currentPlayers.values().stream().toList().get(0).getBukkitPlayer().getName());
            Title title = Title.title(message, Component.empty(), Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1)));
            currentPlayers.values().forEach(p -> p.getBukkitPlayer().showTitle(title));
            spectators.values().forEach(p -> p.getBukkitPlayer().showTitle(title));
        }

        task = new BukkitTaskWrapper(name + "_POST_FIGHT", Bukkit.getScheduler().runTaskLater(getMain(), () -> {
            Location loc = MapConfig.getEntityLocationByLocationString(getMain().getConfig().getString("general.spawn"));

            currentPlayers.values().forEach(p -> this.bringPlayerBack(p, loc));
            spectators.values().forEach(p -> this.bringPlayerBack(p, loc));

            restartArena();
        }, 100));
    }

    private void restartArena(){
        currentPlayers = new LinkedHashMap<>();
        spectators = new LinkedHashMap<>();
        if(task != null) task.cancel();
        task = null;
        schedulesStage = EnumBattleSchedulesStages.WAITING_FOR_PLAYERS;


        this.writeSign();
    }

    private void bringPlayerBack(ExploBattlePlayer p, Location loc){
        p.restoreOldInv();
        p.getBukkitPlayer().teleport(loc);
        p.restoreOldGameMode();
        manager.removePlayerFromList(p.getBukkitPlayer());
    }

    EnumBattleSchedulesStages getSchedulesStage(){
        return this.schedulesStage;
    }

    private static Main getMain(){
        return JavaPlugin.getPlugin(Main.class);
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
        else if(schedulesStage == EnumBattleSchedulesStages.WAIT_COUNTDOWN || schedulesStage == EnumBattleSchedulesStages.WAITING_FOR_PLAYERS){
            if(currentPlayers.size() == spawnPoints.size()){
                text = "FULL";
                color = NamedTextColor.RED;
            }
            else{
                text = "JOIN";
                color = NamedTextColor.DARK_GREEN;
            }
            showPlayerCount = true;
        }
        else{
            text = "IN PROGRESS";
            color = NamedTextColor.RED;
            showPlayerCount = true;
        }
        sign.setEditable(false);
        sign.line(0, EXPLOBATTLE_TAG);
        sign.line(1, Component.text(text, color));
        sign.line(2, Component.text(name, NamedTextColor.BLACK));
        sign.line(3, Component.text(showPlayerCount ? currentPlayers.size() + " / " + spawnPoints.size() : "-----", NamedTextColor.BLACK));
        sign.update();
    }

    void deleteSign(){
        sign.setEditable(false);
        sign.line(0, EXPLOBATTLE_TAG);
        sign.line(1, Component.text("DELETED", NamedTextColor.DARK_RED));
        sign.line(2, Component.text(name, NamedTextColor.BLACK));
        sign.line(3, Component.text("-----", NamedTextColor.BLACK));
        sign.update();
    }

    public String getName(){
        return this.name;
    }

}
