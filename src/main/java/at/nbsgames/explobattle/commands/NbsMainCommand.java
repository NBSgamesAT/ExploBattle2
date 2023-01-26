package at.nbsgames.explobattle.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

abstract class NbsMainCommand {

    protected String name;
    protected String description;
    protected String permission;
    protected boolean playerOnly;


    public NbsMainCommand(String name, String description){
        this.name = name;
        this.description = description;
    }
    public NbsMainCommand(String name, String description, String permission){
        this.name = name;
        this.description = description;
        this.permission = permission;
    }

    public String getName() {
        return name;
    }

    public NbsMainCommand setPlayerOnly(boolean playerOnly){
        this.playerOnly = playerOnly;
        return this;
    }

    abstract protected boolean handleCommand(CommandSender commandSender, Command command, String label, ArrayList<String> args);
    abstract protected List<String> handleTabCompletion(CommandSender commandSender, Command command, String label, ArrayList<String> args);

}
