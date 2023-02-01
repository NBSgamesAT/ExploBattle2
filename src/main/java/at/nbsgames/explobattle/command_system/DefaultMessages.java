package at.nbsgames.explobattle.command_system;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultMessages {

    public static String createCommandFromList(List<NbsMainCommand> commands, NbsArguments[] args){
        StringBuilder strg = new StringBuilder("/");
        Iterator<NbsMainCommand> iter = commands.iterator();
        while(iter.hasNext()){
            strg.append(iter.next().getName());
            if(iter.hasNext()) strg.append(" ");
        }
        if(args == null) return strg.toString();
        for(NbsArguments arg : args){
            strg.append(" <");
            strg.append(arg.getName());
            strg.append('>');
        }
        return strg.toString();
    }

    public void commandNotFound(CommandSender sender, ArrayList<NbsMainCommand> commands, String lastInput) {
        sender.sendMessage(Component.text("Command \"" + createCommandFromList(commands, null) + " " + lastInput + "\" not found.").color(NamedTextColor.WHITE));
    }

    public void commandEndedOnGroupCommand(CommandSender sender, ArrayList<NbsMainCommand> commands) {
        sender.sendMessage(Component.text("Command \"" + createCommandFromList(commands, null) + "\" is a sub command group. The following exist:\n").color(NamedTextColor.WHITE));

        if(commands.size() == 0){
            sender.sendMessage(Component.text("No sub commands available. This most likely is a bug, and should be reported to the developer").color(NamedTextColor.WHITE));
            return;
        }

        NbsGroupCommand cmd = (NbsGroupCommand) commands.get(commands.size() - 1);
        for(NbsMainCommand sub : cmd.subCommands){
            sender.sendMessage(Component.text(sub.getName() + ": " + sub.getDescription()).color(NamedTextColor.WHITE));
        }
    }

    public void emptyGroupCommand(CommandSender sender, ArrayList<NbsMainCommand> commands) {
        sender.sendMessage(Component.text("Command \"" + createCommandFromList(commands, null) + "\" is a group of sub commands. Yet, it does not have sub commands... Please report that as a bug to the developer").color(NamedTextColor.WHITE));
    }

    public void tooFewArguments(CommandSender sender, ArrayList<NbsMainCommand> commands, NbsArguments[] args) {
        sender.sendMessage(Component.text("Not enough arguments for command \"" + createCommandFromList(commands, null) + "\". Here is a full list:").color(NamedTextColor.WHITE));
        sender.sendMessage(Component.text(createCommandFromList(commands, args) + "\n").color(NamedTextColor.WHITE));
        for(NbsArguments arg : args){
            sender.sendMessage(Component.text(arg.getName() + ": " + arg.getDescription()).color(NamedTextColor.WHITE));
        }
    }

    public void missingPermission(CommandSender sender, ArrayList<NbsMainCommand> commands) {
        sender.sendMessage(Component.text("Sorry, but you do not have permissions to use this command!").color(NamedTextColor.WHITE));
    }

    public void senderIsNotPlayer(CommandSender sender, ArrayList<NbsMainCommand> commands) {
        sender.sendMessage(ChatColor.WHITE + "This command can only be executed as a player");
    }

    public void provideActionCommandHelp(CommandSender sender, ArrayList<NbsMainCommand> commands, NbsArguments[] args) {
        sender.sendMessage(Component.text("Command: \"" + createCommandFromList(commands, args) + "\"\n").color(NamedTextColor.WHITE));
        for(NbsArguments arg : args){
            sender.sendMessage(Component.text(arg.getName() + ": " + arg.getDescription()).color(NamedTextColor.WHITE));
        }
    }

    public void provideGroupCommandHelp(CommandSender sender, ArrayList<NbsMainCommand> commands) {
        sender.sendMessage(ChatColor.WHITE + "Command: \"" + createCommandFromList(commands, null) + "\" has the following available sub commands:\n");

        if(commands.size() == 0){
            sender.sendMessage(ChatColor.WHITE + "No sub commands available. This most likely is a bug, and should be reported to the developer");
            return;
        }

        NbsGroupCommand cmd = (NbsGroupCommand) commands.get(commands.size() - 1);
        for(NbsMainCommand sub : cmd.subCommands){
            sender.sendMessage(ChatColor.WHITE + sub.getName() + ": " + sub.getDescription());
        }
    }
}
