package net.lethargiclion.tradingpost;

import java.util.logging.Logger;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TradingPostCommandProcessor {
	
	Logger log;
	
	public TradingPostCommandProcessor(Logger log) {
		this.log = log;
	}
	
	/**
	 * Contains a list of the commands that this command processor supports.
	 * @author TerrorBite
	 *
	 */
	public enum TPCommand {
		help,
		commands
	}
	
	/**
	 * Converts a command name in string form to a TPCommand enum value.
	 * This makes it possible to use switch statements on commands.
	 * @param command A string containing the name of the command to look up.
	 * @return A {@link TPCommand} value for the given command.
	 * @throws CommandNotFoundException if the command does not exist.
	 */
	public TPCommand getCommand(String command) throws CommandNotFoundException {
		TPCommand cmd;
		try { cmd = TPCommand.valueOf(command); }
		catch(IllegalArgumentException e) {
			throw new CommandNotFoundException("No such command");
		}
		return cmd;
	}
	
	/**
	 * Runs a TradingPost user command.
	 * @param sender The {@link CommandSender} the command was issued from.
	 *               Will reply with a warning message if it is not a Player.
	 * @param cmd The command to run, as returned by {@link getCommand}().
	 * @param args The command arguments as provided by Bukkit's {@link JavaPlugin.onCommand}() method.
	 * @return A boolean value suitable to be returned by onCommand().
	 */
	public boolean runCommand(CommandSender sender, TPCommand cmd, String[] args) {
		
		String[] cmdargs = java.util.Arrays.copyOfRange(args, 1, args.length);
		
		if(sender.getClass() != Player.class) {
			sender.sendMessage("You can only run TradingPost commands as a player.");
			return false;
		}
		
		Player p = (Player)sender;
		
		switch(cmd) {
		case help:
			return cmdHelp(p, cmdargs);
		default:
			return false;
		}
		
	}
	
	private boolean cmdHelp(Player p, String[] args) {
		if(args.length == 0) {
			p.sendMessage("For help on a specific command, type /tp help command");
			return true;
		}
		try {
			getCommand(args[0]);
		} catch (CommandNotFoundException e) {
			p.sendMessage("Sorry, that TradingPost command doesn't exist. Try /tr commands");
			return true;
		}
		return false;
	}

}
