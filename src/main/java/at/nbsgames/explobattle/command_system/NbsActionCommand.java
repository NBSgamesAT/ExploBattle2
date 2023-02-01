package at.nbsgames.explobattle.command_system;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NbsActionCommand extends NbsMainCommand {
    private NbsArguments[] args;
    private InterfaceCommandRunnable runnable;

    public NbsActionCommand(String name, String description, InterfaceCommandRunnable runnable) {
        super(name, description);
        this.runnable = runnable;
    }

    protected String permission = null;
    protected boolean playerOnly = false;
    public NbsActionCommand(String name, String description, String permission, InterfaceCommandRunnable runnable) {
        super(name, description);
        this.permission = permission;
        this.runnable = runnable;
    }

    public NbsActionCommand setArguments(NbsArguments... args) {
        this.args = args;
        return this;
    }

    //@Override
    public NbsActionCommand setPlayerOnly(boolean playerOnly){
        this.playerOnly = playerOnly;
        return this;
    }

    private boolean shouldHandle(CommandSender commandSender, ArrayList<String> args, ArrayList<NbsMainCommand> list){

        if(this.permission != null && !commandSender.hasPermission(this.permission)){
            if(list != null) super.errMessages.missingPermission(commandSender, list);
            return false;
        }
        if(this.playerOnly && !(commandSender instanceof Player)) {
            if(list != null) super.errMessages.senderIsNotPlayer(commandSender, list);
            return false;
        }
        if(this.args != null && this.args.length > args.size()){
            if(list != null) super.errMessages.tooFewArguments(commandSender, list, this.args);
            return false;
        }
        return true;
    }
    @Override
    protected boolean handleCommand(CommandSender commandSender, Command command, String label, ArrayList<String> args, ArrayList<NbsMainCommand> list) {
        list.add(this);

        if(!shouldHandle(commandSender, args, list)){
            return true;
        }

        if(this.args == null)
            return runnable.run(commandSender, command, label, null);

        Object[] argsToParse = new Object[this.args.length];
        try{
            for(int i = 0; i < this.args.length; i++){
                argsToParse[i] = this.args[i].getWorker().objectify(args.get(i), this.args[i]);
            }
        }
        catch (FailedToObjectifyException e){
            commandSender.sendMessage(e.getCustomMessage());
            return false;
        }

        return runnable.run(commandSender, command, label, argsToParse);
    }

    protected List<String> handleTabCompletion(CommandSender commandSender, Command command, String label, ArrayList<String> args){
        if(!shouldHandle(commandSender, args, null) || this.args == null ){
            return null;
        }

        if(args.size() > this.args.length){
            return null;
        }
        NbsArguments argToComplete = this.args[args.size() - 1];
        String inputSoFar = args.get(args.size() - 1);

        return argToComplete.getWorker().autocompletionList(inputSoFar, argToComplete);
    }
}