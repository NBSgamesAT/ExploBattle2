package at.nbsgames.explobattle.command_system;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

public class CommandHandler implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final LinkedHashMap<String, NbsMainCommand> topLevelCommands = new LinkedHashMap<>();
    private final DefaultMessages errMessages;
    public CommandHandler(JavaPlugin plugin){
        this.plugin = plugin;
        this.errMessages = new DefaultMessages();
        //this.plugin.getServer().getPluginManager().registerEvent();
    }
    public CommandHandler(JavaPlugin plugin, DefaultMessages errMessages){
        this.plugin = plugin;
        this.errMessages = errMessages;
        //this.plugin.getServer().getPluginManager().registerEvent();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        ArrayList<String> argsToParse = Arrays.stream(args).collect(Collectors.toCollection(ArrayList::new));
        NbsMainCommand cmd = topLevelCommands.get(command.getName());

        if(cmd == null){
            commandSender.sendMessage("Command named " + command.getName()  + " was not found, this message should never appear, please send that to NBSgamesAT");
            return true;
        }
        ArrayList<NbsMainCommand> list = new ArrayList<>();

        return cmd.handleCommand(commandSender, command, label, argsToParse, list);
    }


    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        ArrayList<String> argsToParse = Arrays.stream(args).collect(Collectors.toCollection(ArrayList::new));
        NbsMainCommand cmd = topLevelCommands.get(command.getName());
        if(cmd == null){
            return null;
        }

        return cmd.handleTabCompletion(commandSender, command, label, argsToParse);
    }

    public void registerCommand(NbsMainCommand command){
        command.setErrMessages(this.errMessages);
        this.topLevelCommands.put(command.getName(), command);
        this.plugin.getCommand(command.getName()).setExecutor(this);
        this.plugin.getCommand(command.getName()).setTabCompleter(this);
    }
    public void registerCommandAsAlias(NbsMainCommand command, String alias){
        this.topLevelCommands.put(alias, command);
        this.plugin.getCommand(alias).setExecutor(this);
        this.plugin.getCommand(alias).setTabCompleter(this);
    }
}
