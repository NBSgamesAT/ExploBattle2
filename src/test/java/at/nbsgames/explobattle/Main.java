package at.nbsgames.explobattle;

import at.nbsgames.explobattle.enums.EnumConfigStrings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Main extends JavaPlugin{

    public void onEnable(){
        this.loadConfig();
        System.out.println("Plugin ExploBattle Loaded Successfully");
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

}
