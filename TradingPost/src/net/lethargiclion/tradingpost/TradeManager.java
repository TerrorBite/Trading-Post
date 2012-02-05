package net.lethargiclion.tradingpost;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
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
	Map<Integer, TradeBase> trades;
	
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
		//storage = new TradeStorage(tradeStorageConfig.getValues(true));
		storage = (TradeStorage)tradeStorageConfig.get("storage");
		
		trades = new LinkedHashMap<Integer, TradeBase>();
		Iterator<TradeBase> i = storage.trades.iterator();
		while(i.hasNext()) {
			TradeBase t = i.next();
			trades.put(t.getId(), t);
		}
		this.currentId = storage.currentId;
	}
	
	public void serialize() {
		if(storage == null) throw new IllegalStateException("Can't serialize if the storage object is null!");
		storage.setValues(currentId, trades.values());
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
