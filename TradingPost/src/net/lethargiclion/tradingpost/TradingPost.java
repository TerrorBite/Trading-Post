/**
 * @author TerrorBite
 */
package net.lethargiclion.tradingpost;

import java.util.logging.Logger;

import net.lethargiclion.tradingpost.CommandProcessor.TPCommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main class for the TradingPost Bukkit plugin.
 * @author TerrorBite
 *
 */
public class TradingPost extends JavaPlugin {
	
	Logger log = Logger.getLogger("Minecraft");
	
	CommandProcessor processor = new CommandProcessor(log);
	
	public void onEnable() {
		log.info("TradingPost has been enabled.");
	}
	 
	public void onDisable() {
		log.info("TradingPost is now disabled. Have a nice day!.");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		
		if(cmd.getName().toLowerCase() == "tp") {
			TPCommand subcmd;
			try {
				subcmd = processor.getCommand(args[0]);
			} catch (CommandNotFoundException e) {
				return false;
			}
			
			return processor.runCommand(sender, subcmd, args);
		}
		else if(cmd.getName().toLowerCase() == "tpadmin") {
			//TODO: Add admin command processor
			return false;
		}
		
		return false;
	}

}
