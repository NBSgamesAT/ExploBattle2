package at.nbsgames.explobattle.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class NbsActionCommand extends NbsMainCommand {
    private NbsArguments[] args;

    public abstract boolean runTime(CommandSender sender, Command command, String label, Object[] args);

    public NbsActionCommand(String name, String description) {
        super(name, description);
    }

    public NbsActionCommand(String name, String description, String permission) {
        super(name, description, permission);
    }

    public void setArguments(NbsArguments... args) {
        this.args = args;
    }

    private boolean shouldHandle(CommandSender commandSender, Command command, String label, ArrayList<String> args){

        if(super.permission != null && !commandSender.hasPermission(super.permission)){
            commandSender.sendPlainMessage("permission issues");
            return false;
        }
        if(super.playerOnly && !(commandSender instanceof Player)) {
            commandSender.sendPlainMessage("player only issues");
            return false;
        }
        if(this.args != null && this.args.length > args.size()){
            commandSender.sendPlainMessage("argumental issues1");
            return false;
        }
        return true;
    }
    @Override
    protected boolean handleCommand(CommandSender commandSender, Command command, String label, ArrayList<String> args) {

        if(!shouldHandle(commandSender, command, label, args)){
            return false;
        }

        if(this.args == null)
            return runTime(commandSender, command, label, null);

        Object[] argsToParse = new Object[this.args.length];
        try{
            for(int i = 0; i < this.args.length; i++){
                argsToParse[i] = this.args[i].getWorker().objectize(args.get(i), this.args[i]);
            }
        }
        catch (IllegalArgumentException e){
            commandSender.sendPlainMessage("argumental issues");
            return false;
        }

        return runTime(commandSender, command, label, argsToParse);
    }

    protected List<String> handleTabCompletion(CommandSender commandSender, Command command, String label, ArrayList<String> args){
        if(!shouldHandle(commandSender, command, label, args) || this.args == null ){
            return null;
        }

        NbsArguments argToComplete = this.args[args.size() - 1];
        String inputSoFar = args.get(args.size() - 1);

        return argToComplete.getWorker().autocompletionList(inputSoFar, argToComplete);
    }
}