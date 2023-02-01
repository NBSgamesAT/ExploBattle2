package at.nbsgames.explobattle.command_system;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

abstract class NbsMainCommand {

    protected String name;
    protected String description;
    protected DefaultMessages errMessages;


    public NbsMainCommand(String name, String description){
        this.name = name;
        this.description = description;
    }
    protected void setErrMessages(DefaultMessages errMessages){
        this.errMessages = errMessages;
    }
    /*public NbsMainCommand(String name, String description, String permission){
        this.name = name;
        this.description = description;
        this.permission = permission;
    }*/

    public String getName() {
        return name;
    }
    public String getDescription(){
        return description;
    }

    /*public NbsMainCommand setPlayerOnly(boolean playerOnly){
        this.playerOnly = playerOnly;
        return this;
    }*/

    abstract protected boolean handleCommand(CommandSender commandSender, Command command, String label, ArrayList<String> args, ArrayList<NbsMainCommand> list);
    abstract protected List<String> handleTabCompletion(CommandSender commandSender, Command command, String label, ArrayList<String> args);

}
