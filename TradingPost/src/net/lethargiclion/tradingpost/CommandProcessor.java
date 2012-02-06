package net.lethargiclion.tradingpost;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Processes user commands for TradingPost.
 * @author TerrorBite
 *
 */
public class CommandProcessor {
	
	Logger log;
	
	/**
	 * Constructor.
	 * @param log The logger to use for printing console messages.
	 */
	public CommandProcessor(Logger log) {
		this.log = log;
	}
	
	/**
	 * Contains a list of the commands that this command processor supports.
	 * @author TerrorBite
	 *
	 */
	public enum TPCommand {
		help,
		commands,
		deliver,
		debug // TODO: Don't leave debug command in.
		//TODO: Add full range of commands
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
		
		// Remove first argument (which is the command name itself)
		String[] cmdargs = java.util.Arrays.copyOfRange(args, 1, args.length);
		
		if(!(sender instanceof Player)) {
			// Then we were probably called via console or Rcon
			sender.sendMessage("You can only run TradingPost commands as a player.");
			log.warning("Attempted to use TradingPost command from console.");
			return false;
		}
		
		// Typecast the CommandSender
		Player p = (Player)sender;
		
		// Execute the requested command
		switch(cmd) {
		case help:
			return cmdHelp(p, cmdargs);
		case commands:
			return cmdCommands(p);
		case deliver:
			return cmdDeliver(p);
		case debug:
			return cmdDebug(p, cmdargs);
		default:
			p.sendMessage("Sorry, this command has not yet been implemented.");
			log.warning(String.format("%s tried to run unimplemented command %s!", p.getName(), cmd.name()));
			return false;
		}
		
	}
	
	private boolean cmdDeliver(Player p) {
		p.sendMessage("Forcing delivery of pending items.");
		TradingPost.getManager().deliverPending(p);
		return true;
	}

	private boolean cmdDebug(Player p, String[] cmdargs) {
		
		// Temporary debugging stuff.
		if(cmdargs[0].equalsIgnoreCase("bid")) {
		    List<ItemStack> items = new ArrayList<ItemStack>();
			items.add(p.getItemInHand());
			ItemBid i = new ItemBid(p, items);
			TradingPost.getManager().newBid(i);
			p.sendMessage("A debug bid has been created.");
		}
		else if(cmdargs[0].equalsIgnoreCase("deliver")) {
			List<ItemStack> items = new ArrayList<ItemStack>();
			items.add(p.getItemInHand());
			p.setItemInHand(new ItemStack(0));
			TradingPost.getManager().deliverItems(p, items, true);
			p.sendMessage("Your items will be returned to you later.");
		}
		else {
			p.sendMessage(String.format("Unknown debug command \"%s\"", cmdargs[0]));
		}
		return true;
	}

	/**
	 * Provides a list of available TradingPost commands.
	 * @param p The player running the command.
	 * @return True
	 */
	private boolean cmdCommands(Player p) {
		p.sendMessage("Available TradingPost commands are:");
		// Compile and return command list directly from our enum
		p.sendMessage(StringUtils.join(TPCommand.values(), ", "));
		return true;
	}

	/**
	 * Provides help on a 
	 * @param p
	 * @param args
	 * @return
	 */
	private boolean cmdHelp(Player p, String[] args) {
		// No command supplied by the user?
		if(args.length == 0) {
			p.sendMessage("For help on a specific command, type /tr help <command>");
			p.sendMessage("To get a list of commands, type /tr commands");
			return true;
		}
		
		// Find out which command they want help with
		TPCommand cmd;
		try {
			cmd = getCommand(args[0]);
		} catch (CommandNotFoundException e) {
			// We don't know that command
			p.sendMessage("Sorry, that TradingPost command doesn't exist. Try /tr commands");
			return true;
		}
		
		// Prepare to give usage information for the commands
		String usage = "", info = "";
		
		// Provide the correct messages for the requested command
		switch(cmd) {
		case help:
			usage = "help <command>";
			info = "Provides information on how to use TradingPost commands.";
			break;
		case commands:
			usage = "commands";
			info = "Shows a list of available TradingPost commands.";
			break;
		case deliver:
			usage = "deliver";
			info = "Causes TradingPost to attempt redelivery of any waiting items.";
		case debug:
			// TODO: Don't leave this in.
			usage = "debug <feature>";
			info = "Debugging command for developer use.";
		default:
			usage = cmd.name();
			info = "Sorry, there is no help available for this command.";
		}
		
		// Send usage message
		p.sendMessage("Usage: /tr " + usage);
		p.sendMessage(info);
		
		return true;
	}

}
