package at.nbsgames.explobattle.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class CommandHandler implements CommandExecutor, TabCompleter {

    private JavaPlugin plugin;
    private LinkedList<NbsMainCommand> topLevelCommands = new LinkedList<>();
    public CommandHandler(JavaPlugin plugin){
        this.plugin = plugin;
        //this.plugin.getServer().getPluginManager().registerEvent();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        ArrayList<String> argsToParse = Arrays.stream(args).collect(Collectors.toCollection(ArrayList::new));
        String commandName = command.getName();
        Optional<NbsMainCommand> possible = topLevelCommands.stream().filter(cmd -> cmd.getName().equals(commandName)).findFirst();
        if(possible.isEmpty()){
            commandSender.sendPlainMessage("command named " + commandName  + " not found");
            return false;
        }

        return possible.get().handleCommand(commandSender, command, label, argsToParse);
    }


    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        ArrayList<String> argsToParse = Arrays.stream(args).collect(Collectors.toCollection(ArrayList::new));
        String commandName = command.getName();
        Optional<NbsMainCommand> possible = topLevelCommands.stream().filter(cmd -> cmd.getName().equals(commandName)).findFirst();
        if(possible.isEmpty()){
            return null;
        }

        return possible.get().handleTabCompletion(commandSender, command, label, argsToParse);
    }

    public void registerCommand(NbsMainCommand command){
        this.topLevelCommands.add(command);
        this.plugin.getCommand(command.getName()).setExecutor(this);
        this.plugin.getCommand(command.getName()).setTabCompleter(this);

    }
}
