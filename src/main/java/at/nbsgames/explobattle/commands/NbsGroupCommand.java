package at.nbsgames.explobattle.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class NbsGroupCommand extends NbsMainCommand{


    public NbsGroupCommand(String name, String description) {
        super(name, description);
    }


    public NbsGroupCommand(String name, String description, String permission) {
        super(name, description, permission);
    }
    private LinkedList<NbsMainCommand> subCommands = new LinkedList<>();


    private boolean shouldHandle(CommandSender commandSender, Command command, String label, ArrayList<String> args){
        if(super.permission != null && !commandSender.hasPermission(super.permission)){
            return false;
        }
        if(super.playerOnly && !(commandSender instanceof Player)) {
            return false;
        }
        return true;
    }
    private NbsMainCommand getNextCommand(CommandSender commandSender, Command command, String label, ArrayList<String> args){

        Optional<String> next = args.stream().findFirst();

        if(next.isEmpty()){
            return null;
        }
        Optional<NbsMainCommand> nextCommand = subCommands.stream().filter(nbsMainCommand -> nbsMainCommand.getName().equals(next.get())).findFirst();
        if(nextCommand.isEmpty()){
            return null;
        }

        args.remove(0);
        return nextCommand.get();
    }

    @Override
    protected boolean handleCommand(CommandSender commandSender, Command command, String label, ArrayList<String> args){

        if(!shouldHandle(commandSender, command, label, args)){
            return false;
        }

        NbsMainCommand next = getNextCommand(commandSender, command, label, args);
        if(next == null){
            return false;
        }
        return next.handleCommand(commandSender, command, label, args);
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
            NbsMainCommand next = getNextCommand(commandSender, command, label, args);
            if(next == null){
                return null;
            }
            return next.handleTabCompletion(commandSender, command, label, args);
        }

        String input = args.get(0);
        return subCommands.stream().filter(cmd -> cmd.getName().startsWith(input.toLowerCase())).map(NbsMainCommand::getName).toList();
    }

    public NbsGroupCommand addSubCommand(NbsMainCommand command) {
        subCommands.add(command);
        return this;
    }

    @Override
    public NbsGroupCommand setPlayerOnly(boolean playerOnly){
        super.playerOnly = playerOnly;
        return this;
    }
}
