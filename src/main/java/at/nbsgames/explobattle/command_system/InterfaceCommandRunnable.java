package at.nbsgames.explobattle.command_system;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface InterfaceCommandRunnable {
    boolean run(CommandSender sender, Command command, String label, Object[] args);

}
