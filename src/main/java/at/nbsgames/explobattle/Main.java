package at.nbsgames.explobattle;

import at.nbsgames.explobattle.arenas.ArenaManager;
import at.nbsgames.explobattle.command_system.CommandHandler;
import at.nbsgames.explobattle.command_system.NbsGroupCommand;
import at.nbsgames.explobattle.enums.EnumConfigStrings;
import at.nbsgames.explobattle.events.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main extends JavaPlugin{

    private MapConfig mapConfig;

    private ArenaManager arenaMan;
    public void onEnable(){
        try {
            mapConfig = new MapConfig("arenas", this);
        } catch (IOException e) {
            this.getLogger().severe("Failed to create/load arenas.yml in plugins/ExploBattle2. Disabling ExploBattle2!");
            this.getLogger().severe("Posting Java's stacktrace in case it helps to debug the problem. Please check your file permissions and make sure you have enough disk space left.");
            e.printStackTrace();
            this.getPluginLoader().disablePlugin(this);
        }
        this.arenaMan = new ArenaManager(this);
        this.createConfig();
        this.loadConfig();
        this.loadCommand();
        this.loadEvents();
        this.loadArenas();
        this.getLogger().info("Plugin ExploBattle2 Loaded successfully.");
    }
    private CommandHandler handler = new CommandHandler(this);

    private void loadCommand(){
        NbsGroupCommand cmd = CommandExploBattle.getCommand(this);
        handler.registerCommand(cmd);
        handler.registerCommandAsAlias(cmd, "eb");
    }
    private void loadEvents(){
        this.getServer().getPluginManager().registerEvents(new RightClickEvent(this), this);
        this.getServer().getPluginManager().registerEvents(new ExplosionEvent(this), this);
        this.getServer().getPluginManager().registerEvents(new ProjectileHitSomethingEvent(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerMove(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerDeathEvent(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerDisconnectEvent(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerThrowsSomethingEvent(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerInventoryChangeEvent(this), this);
    }

    private void loadArenas(){
        List<String> mapNames = this.mapConfig.getListOfKeys(MapConfig.ARENA_PREFIX);
        if(mapNames == null) return;
        for(String name : mapNames){
            if(!this.arenaMan.loadArena(name)){
                this.getLogger().info("Arena \"" + name + "\" could not be loaded please check the sign and if the arena has 2 spawns.");
            }
        }
    }

    public ArenaManager getArenaManager(){
        return this.arenaMan;
    }

    public void onDisable(){

    }
    private void createConfig(){
        this.reloadConfig();
        List<String> list = new ArrayList<>();
        list.add("This is the config for Explo Battle. Just Change the lines if you want to change it");
        list.add("Message here use the simple mini message format (DOC: https://docs.advntr.dev/minimessage/format.html#). It does not require end tags.");
        this.getConfig().options().setHeader(list);

        for(EnumConfigStrings configString : EnumConfigStrings.values()){
            this.getConfig().addDefault(configString.getConfig(), configString.getText());
        }
        this.getLogger().info("ALL TEXTS LOADED!");
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
    }

    private void loadConfig(){
        for(EnumConfigStrings configString : EnumConfigStrings.values()){
            String configureText = this.getConfig().getString(configString.getConfig());
            configString.setText(configureText);
        }
    }

    public MapConfig getMapConfig() {
        return mapConfig;
    }

    public static ItemStack makeItemWithName(Material mat, Component name){
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(name);
        item.setItemMeta(meta);
        return item;
    }
}
