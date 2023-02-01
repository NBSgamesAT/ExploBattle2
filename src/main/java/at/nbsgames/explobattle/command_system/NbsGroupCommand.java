package at.nbsgames.explobattle.command_system;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class NbsGroupCommand extends NbsMainCommand{


    public NbsGroupCommand(String name, String description) {
        super(name, description);
    }


    /*public NbsGroupCommand(String name, String description, String permission) {
        super(name, description, permission);
    }*/
    protected LinkedList<NbsMainCommand> subCommands = new LinkedList<>();


    private boolean shouldHandle(CommandSender commandSender, Command command, String label, ArrayList<String> args){
        /*if(super.permission != null && !commandSender.hasPermission(super.permission)){
            return false;
        }
        if(super.playerOnly && !(commandSender instanceof Player)) {
            return false;
        }*/
        return true;
    }
    private NbsMainCommand getNextCommand(CommandSender commandSender, ArrayList<String> args, ArrayList<NbsMainCommand> list){

        Optional<String> next = args.stream().findFirst();

        if(next.isEmpty()){
            if(list != null) super.errMessages.commandEndedOnGroupCommand(commandSender, list);
            return null;
        }
        if(subCommands.isEmpty()){
            if(list != null) super.errMessages.emptyGroupCommand(commandSender, list);
            return null;
        }
        Optional<NbsMainCommand> nextCommand = subCommands.stream().filter(nbsMainCommand -> nbsMainCommand.getName().equals(next.get())).findFirst();
        if(nextCommand.isEmpty()){
            if(list != null) super.errMessages.commandNotFound(commandSender, list, next.get());
            return null;
        }

        args.remove(0);
        return nextCommand.get();
    }

    @Override
    protected boolean handleCommand(CommandSender commandSender, Command command, String label, ArrayList<String> args, ArrayList<NbsMainCommand> list){
        list.add(this);

        if(!shouldHandle(commandSender, command, label, args)){
            return true;
        }

        NbsMainCommand next = getNextCommand(commandSender, args, list);
        if(next == null){
            return true;
        }
        return next.handleCommand(commandSender, command, label, args, list);
    }

    @Override
    protected List<String> handleTabCompletion(CommandSender commandSender, Command command, String label, ArrayList<String> args){
        if(!shouldHandle(commandSender, command, label, args)){
            return null;
        }
        if(args.size() == 0) {
            return null;
        }

        if(args.size() > 1){
            NbsMainCommand next = getNextCommand(commandSender, args, null);
            if(next == null){
                return null;
            }
            return next.handleTabCompletion(commandSender, command, label, args);
        }

        return subCommands.stream().map(NbsMainCommand::getName).toList();
    }

    @Override
    public void setErrMessages(DefaultMessages messages){
        super.errMessages = messages;
        subCommands.forEach(cmd -> cmd.setErrMessages(messages));
    }

    public NbsGroupCommand addSubCommand(NbsMainCommand command) {
        subCommands.add(command);
        return this;
    }

    /*@Override
    public NbsGroupCommand setPlayerOnly(boolean playerOnly){
        super.playerOnly = playerOnly;
        return this;
    }*/
}
