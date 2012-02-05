package net.lethargiclion.tradingpost;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Singleton class for managing trades.
 * @author TerrorBite
 *
 */
public enum TradeManager {
	INSTANCE;
	
	/**
	 * Holds the parent TradingPost instance.
	 */
	private TradingPost plugin;
	
	private TradeStorage storage;
	
	/**
	 * Holds all the trades, bids, etc.
	 */
	List<TradeBase> trades;
	
	/**
	 * Holds the last-used ID value, to enable auto-incrementing ID values.
	 */
	private int currentId;
	
	private FileConfiguration tradeStorageConfig = null;
	private File tradeStorageFile = null;
	
	/**
	 * Plugin accessor function. This ensures that classes like ItemBid that only know
	 * about their TradeManager can access the main plugin instance if they need to.
	 * @return The main plugin instance.
	 */
	//NOTE: is this really needed?
	public TradingPost getPlugin() {
		return plugin;
	}
	
	public void initialize(TradingPost plugin) {
		this.plugin = plugin;
		deserialize();
		
	}
	
	public void deserialize() {
		if(tradeStorageConfig == null) loadStorage();
		storage = (TradeStorage)tradeStorageConfig.get("storage");
	}
	
	public void serialize() {
		if(storage == null) throw new IllegalStateException();
		storage.setValues(currentId, trades);
		if(tradeStorageConfig == null) loadStorage();
		tradeStorageConfig.set("storage", storage);
		saveStorage();
	}
	
	private void loadStorage() {
		if(plugin == null) throw new IllegalStateException();
		if(tradeStorageFile == null) {
			tradeStorageFile = new File(plugin.getDataFolder(), "storage.yml");
		}
		tradeStorageConfig = YamlConfiguration.loadConfiguration(tradeStorageFile);
		
	    // Get default persistence file from the jar
	    InputStream defaultStorageStream = plugin.getResource("storage.yml");
	    if (defaultStorageStream != null) {
	    	tradeStorageConfig.setDefaults(YamlConfiguration.loadConfiguration(defaultStorageStream));
	    }
	}
	
	private void saveStorage() {
		if (tradeStorageConfig == null || tradeStorageFile == null) {
		    return;
		    }
		    try {
		        tradeStorageConfig.save(tradeStorageFile);
		    } catch (IOException ex) {
		        plugin.log.log(Level.SEVERE, "Could not persist storage to " + tradeStorageFile, ex);
		    }
	}
	
	/**
	 * Retrieves the next ID value that should be used for a Trade or Bid.
	 * @return
	 */
	public int getNextId() {
		return currentId++;
	}

	public ItemBid getBid(int bidId) {
		return null;
	}

}
