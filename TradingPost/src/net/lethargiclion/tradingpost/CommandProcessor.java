package net.lethargiclion.tradingpost;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
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
	TradeManager manager;
	
	/**
	 * Constructor.
	 * @param log The logger to use for printing console messages.
	 */
	public CommandProcessor(Logger log, TradeManager mgr) {
		this.log = log;
		this.manager = mgr;
	}
	
	/**
	 * Contains a list of the commands that this command processor supports.
	 * @author TerrorBite
	 *
	 */
	public enum TPCommand {
		// Enum constants need to be named in capitals
		HELP,
		COMMANDS,
		DELIVER,
		SELL,
		BROWSE,
		CHECK,
		SHOW,
		WITHDRAW,
		DEBUG // TODO: Don't leave debug command in.
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
		try { cmd = TPCommand.valueOf(command.toUpperCase()); }
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
			return true;
		}
		
		// Typecast the CommandSender
		Player p = (Player)sender;
		
		// Execute the requested command
		switch(cmd) {
		case HELP:
			return cmdHelp(p, cmdargs);
		case COMMANDS:
			return cmdCommands(p);
		case DELIVER:
			return cmdDeliver(p);
		case SELL:
			return cmdSell(p);
		case BROWSE:
			return cmdBrowse(p, cmdargs);
		case CHECK:
			return cmdCheck(p, cmdargs);
		case SHOW:
			return cmdShow(p, cmdargs);
		case WITHDRAW:
			return cmdWithdraw(p, cmdargs);
		case DEBUG:
			return cmdDebug(p, cmdargs);
		default:
			p.sendMessage("Sorry, this command has not yet been implemented.");
			log.warning(String.format("%s tried to run unimplemented command %s!", p.getName(), cmd.name()));
			return false;
		}
		
	}
	
	private boolean cmdWithdraw(Player p, String[] cmdargs) {
		int tradeId= 0;
		GenericTrade trade = null;
		if (cmdargs.length != 1) return false;
		try {
			tradeId = Integer.parseInt(cmdargs[0]);
			trade = manager.getTrade(tradeId);
		} catch (NumberFormatException e) {
			p.sendMessage(String.format("\"%s\" is not a valid trade ID!", cmdargs[0]));
			return true;
		} catch (TradeNotFoundException e) {
			p.sendMessage(String.format("There is no trade with ID %d", tradeId));
			return true;
		}
		if (trade.getOwner() != p){
			p.sendMessage("Cannot withdraw a trade you do not own");
			return true;
		}
		if (!manager.withdrawTrade(p, trade)) {
			p.sendMessage(String.format("Trade %d has already been withdrawn!", tradeId));
		}
		return true;
	}

	private boolean cmdCheck(Player p, String[] cmdargs) {
		List<GenericTrade> pTrades = manager.getPlayerTrades(p);
		for(GenericTrade tr: pTrades) {
			// Only display open trades
			if(tr.getStatus() != TradeStatus.open) continue;
			
			displayItem(p, tr);
		}
		return false;
	}
	
	private void displayItem(Player p, GenericTrade tr) {
		// Get the first ItemStack from the trade for later display use
		// (it will only be displayed if it was the only ItemStack)
		ItemStack item = tr.getItems().toArray(new ItemStack[tr.getItems().size()])[0];
		
		// For offers:
		if(tr instanceof GenericOffer) {
			p.sendMessage(String.format(
					"#%d: %s offered %s (%d bids)",
					tr.getId(),
					tr.getOwner()==p?"You":tr.getOwner().getName(),
					(tr.getItems().size() == 1) ? String.format("%d %s", item.getAmount(), item.getType().toString()) : "multiple items",
					((GenericOffer)tr).bidCount()
				));
		}
		
		// For bids:
		else if(tr instanceof GenericBid) {
			String parentOwner = "Unknown";
			try {
				parentOwner = manager.getTrade(((GenericBid)tr).getParentId()).getOwner().getName();
			} catch (TradeNotFoundException e) {
				// What do?
			}
			p.sendMessage(String.format(
					"#%d: %s bid %s on offer %d by %s",
					tr.getId(),
					tr.getOwner()==p?"You":tr.getOwner().getName(),
					(tr.getItems().size() == 1) ? String.format("%d %s", item.getAmount(), item.getType().toString()) : "multiple items",
					((GenericBid)tr).getParentId(),
					parentOwner
				));
		}
	}

	private boolean cmdShow(Player p, String[] cmdargs) {

		List<String> output = new ArrayList<String>();
		if(cmdargs.length == 0) {
			p.sendMessage("You need to give a valid trade ID.");
			return true;
		}
		
		int tradeId;
		try {
			tradeId = Integer.parseInt(cmdargs[0]);
			if(tradeId < 0) {
				p.sendMessage("You need to give a valid trade ID.");
				return true;
			}
			GenericTrade tr = manager.getTrade(tradeId);
			
			output.add(String.format("[#%d] %s %s by %s, %s",
					tr.getId(),
					tr.getStatus().name().toLowerCase(),
					tr instanceof GenericOffer?"offer":"bid",
					tr.getOwner().getName(),
					tr.getTimeString()
			));
			
			if(tr instanceof GenericBid) {
				try {
					GenericTrade parent = manager.getTrade(((GenericBid)tr).getParentId());
					output.add(String.format("On %s offer #%d by %s",
							parent.getStatus().name().toLowerCase(),
							parent.getId(),
							parent.getOwner().getName()
					));
				} catch (TradeNotFoundException e) {
					output.add(String.format("On invalid offer ID #%d", ((GenericBid)tr).getParentId()));
				}
			}
			
			StringBuilder b = new StringBuilder();
			b.append("Items: ");
			Iterator<ItemStack> i = tr.getItems().iterator();
			while(i.hasNext()) {
				ItemStack is = i.next();
				b.append(String.format("%d %s", is.getAmount(), is.getType().toString()));
				if(i.hasNext()) b.append(", ");
			}
			output.add(b.toString());
			
			// TODO: Output trade's comment value
			
			if(tr instanceof GenericOffer) {
				for(Integer bidId: ((GenericOffer)tr).getBids()) {
					try {
						GenericBid bid = manager.getBid(bidId);
						// Get the first ItemStack from the trade for later display use
						// (it will only be displayed if it was the only ItemStack)
						ItemStack item = bid.getItems().toArray(new ItemStack[tr.getItems().size()])[0];
						output.add(String.format("Bid #%d: %s offered %s",
								bidId,
								(bid.getItems().size() == 1) ? String.format("%d %s", item.getAmount(), item.getType().toString()) : "multiple items"
						));
					} catch(TradeNotFoundException ex) {
						output.add(String.format("Bid #%d: invalid", bidId));
					}
				}
			}
			
			
		} catch(NumberFormatException ex) {
			p.sendMessage("You need to give a valid trade ID.");
			return true;
		} catch (TradeNotFoundException e) {
			p.sendMessage("There is no trade by that ID.");
			return true;
		}
		
		return true;
	}

	private boolean cmdBrowse(Player p, String[] cmdargs) {
		List<GenericTrade> tradePage = null;
		int pageNum= 1;
		if (cmdargs.length != 1) return false;
		try {
			pageNum = Integer.parseInt(cmdargs[0]);
		} catch (NumberFormatException e) {
			p.sendMessage(String.format("\"%s\" is not a valid page number!", cmdargs[0]));
			return true;
		}
		
		tradePage = manager.getPage(pageNum);
		for(GenericTrade tr: tradePage) {
			displayItem(p, tr);
		}
		
		return true;
	}

	private boolean cmdSell(Player p) {
		// Get the player's held item stack
		ItemStack items = p.getItemInHand();
		if(items.getType() == Material.AIR) {
			p.sendMessage("Your hand is empty. Nothing to sell - no trade created.");
			return true;
		}
		
		// Set player's hand to empty
		p.setItemInHand(new ItemStack(Material.AIR));
		
		// makeTrade expects a List, so create a single-element ArrayList
		List<ItemStack> itemList = new ArrayList<ItemStack>();
		itemList.add(items);
		
		// Create the trade
		int tradeId = manager.makeOffer(p, itemList);
		
		// Inform the user of success
		p.sendMessage(String.format("Trade number %d has been created.", tradeId));
		p.sendMessage(String.format("You put %d %s up for sale.", items.getAmount(), items.getType().toString()));
		
		return true;
	}

	private boolean cmdDeliver(Player p) {
		switch(manager.deliverQueued(p)) {
		case NO_ITEMS:
			p.sendMessage("There are no items to be delivered.");
			break;
		case SUCCESS:
			p.sendMessage("All waiting items have been delivered to you.");
			break;
		case NOT_ENOUGH_SPACE:
			p.sendMessage("Your inventory has run out of space for new items.");
			p.sendMessage("Clear some inventory slots, and then try again.");
		}
		return true;
	}

	private boolean cmdDebug(Player p, String[] cmdargs) {
		
		// Temporary debugging stuff.
		if(cmdargs[0].equalsIgnoreCase("deliver")) {
			List<ItemStack> items = new ArrayList<ItemStack>();
			items.add(p.getItemInHand());
			p.setItemInHand(new ItemStack(0));
			manager.deliverItems(p, items, true);
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
		p.sendMessage(StringUtils.join(TPCommand.values(), ", ").toLowerCase());
		return true;
	}

	/**
	 * Provides help on a particular command.
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
		case HELP:
			usage = "help <command>";
			info = "Provides information on how to use TradingPost commands.";
			break;
		case COMMANDS:
			usage = "commands";
			info = "Shows a list of available TradingPost commands.";
			break;
		case DELIVER:
			usage = "deliver";
			info = "Attempts to redeliver any waiting items to you.";
			break;
		case DEBUG:
			// TODO: Don't leave this in.
			usage = "debug <feature>";
			info = "Debugging command for developer use.";
			break;
		case SELL:
			usage = "sell";
			info = "Posts the stack of items you are holding as a sale.";
			break;
		case SHOW:
			usage = "show <id>";
			info = "Shows details about a trade or bid.";
			break;
		default:
			usage = cmd.name();
			info = "Sorry, there is no help available for this command yet.";
		}
		
		// Send usage message
		p.sendMessage("Usage: /tr " + usage);
		p.sendMessage(info);
		
		return true;
	}

}
