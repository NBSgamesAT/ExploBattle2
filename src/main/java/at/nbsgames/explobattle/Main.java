package at.nbsgames.explobattle;

import at.nbsgames.explobattle.command_system.CommandHandler;
import at.nbsgames.explobattle.command_system.NbsGroupCommand;
import at.nbsgames.explobattle.enums.EnumConfigStrings;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin{

    private MapConfig mapConfig;
    private SignManager signManager;
    public void onEnable(){
        try {
            mapConfig = new MapConfig("arenas", this);
        } catch (IOException e) {
            this.getLogger().severe("[ExploBattle2] Failed to create/load arenas.yml in plugins/ExploBattle2. Disabling ExploBattle2!");
            this.getLogger().severe("[ExploBattle2] Posting Java's stacktrace in case it helps to debug the problem. Please check your file permissions and make sure you have enough disk space left.");
            e.printStackTrace();
            this.getPluginLoader().disablePlugin(this);
        }
        this.signManager = new SignManager();
        this.loadConfig();
        this.loadCommand();
        this.loadArenas();
        this.getLogger().info("[ExploBattle2]Plugin ExploBattle2 Loaded successfully.");
    }
    private CommandHandler handler = new CommandHandler(this);

    private void loadCommand(){
        NbsGroupCommand cmd = CommandExploBattle.getCommand(this);
        handler.registerCommand(cmd);
        handler.registerCommandAsAlias(cmd, "eb");
    }

    private void loadArenas(){
        List<String> mapNames = this.mapConfig.getListOfKeys(CommandExploBattle.ARENA_PREFIX);
        if(mapNames == null) return;
        for(String name : mapNames){
            if(!Arena.loadArena(this, name)){
                this.getLogger().info("[ExploBattle2] Arena \"" + name + "\" could not be loaded please check the sign and if the arena has 2 spawns.");
            }
        }
    }

    public void onDisable(){

    }
    private void loadConfig(){
        this.reloadConfig();
        List<String> list = new ArrayList<>();
        list.add("This is the config for Explo Battle. Just Change the lines if you want to change them god them.");
        this.getConfig().options().setHeader(list);
        this.getConfig().addDefault(EnumConfigStrings.TEXT_PLAYER_JOINED.toString(), ChatColor.GREEN + "+ (PLAYER) has joined the game!");
        this.getConfig().addDefault(EnumConfigStrings.TEXT_PLAYER_LEAVE.toString(), ChatColor.RED + "- (PLAYER) has left the game!");
        this.getConfig().addDefault(EnumConfigStrings.TEXT_PLAYER_JOINED.toString(), ChatColor.RED + "- (PLAYER) disconnected and was removed from the game!");
        this.getConfig().addDefault(EnumConfigStrings.TEXT_PLAYER_DIED.toString(), ChatColor.YELLOW + "+ (PLAYER) died!!");
        //this.getConfig().addDefault(EnumConfigStrings.TEXT_PLAYER_JOINED.toString(), ChatColor.GREEN + "+ (PLAYER) has joined the game!");
        //this.getConfig().addDefault(EnumConfigStrings.TEXT_PLAYER_JOINED.toString(), ChatColor.GREEN + "+ (PLAYER) has joined the game!");
        //this.getConfig().addDefault(EnumConfigStrings.TEXT_PLAYER_JOINED.toString(), ChatColor.GREEN + "+ (PLAYER) has joined the game!");
        this.saveConfig();
    }

    public MapConfig getMapConfig() {
        return mapConfig;
    }

    public SignManager getSignManager() {
        return signManager;
    }
}
