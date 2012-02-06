package net.lethargiclion.tradingpost;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.OfflinePlayer;
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
	private TradingPost plugin = null;
	
	private TradeStorage storage = null;
	
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
		
		//plugin.log.info("[TradingPost] TradeManager: Storage config loaded successfully.");
		
		//storage = new TradeStorage(tradeStorageConfig.getValues(true));
		try {
			storage = (TradeStorage)tradeStorageConfig.get("storage");
		} catch (ClassCastException e) {
			plugin.log.log(Level.SEVERE, String.format("[TradingPost] TradeManager: Got an invalid class, expecting %s", TradeStorage.class.getName()), e);
		}
		if(storage == null) {
			storage = new TradeStorage();
			plugin.log.warning("[TradingPost] TradeManager: Failed to load from storage.yml, initialized new TradeStorage!");
		}
		
		else plugin.log.info("[TradingPost] TradeManager: Successfully restored trading data.");
		
		// Now load the data into our own data structures
		trades = new LinkedHashMap<Integer, TradeBase>();
		if(storage.trades == null) {
			plugin.log.severe("[TradingPost] TradeManager: Null trades list in TradeStorage!");
		} else {
			Iterator<TradeBase> i = storage.trades.iterator();
			while(i.hasNext()) {
				TradeBase t = i.next();
				trades.put(t.getId(), t);
			}
		}
		this.currentId = storage.currentId;
	}
	
	public void serialize() {
		if(storage == null) throw new IllegalStateException("Can't serialize with a null storage object!");
		storage.setValues(currentId, trades.values());
		if(tradeStorageConfig == null) loadStorage();
		tradeStorageConfig.set("storage", storage);
		saveStorage();
		plugin.log.info("[TradingPost] TradeManager: Successfully saved trading data.");
	}
	
	private void loadStorage() {
		if(plugin == null) throw new IllegalStateException("loadStorage() was called before the TradeManager was initialized!");
		if(tradeStorageFile == null) {
			tradeStorageFile = new File(plugin.getDataFolder(), "storage.yml");
		}
		try {
			tradeStorageConfig = YamlConfiguration.loadConfiguration(tradeStorageFile);
		} catch (Exception e) {
			plugin.log.log(Level.WARNING, "Internal error occurred while loading storage.yml, falling back to defaults", e);
			tradeStorageConfig = new YamlConfiguration();
		}
		
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
	
	public TradeBase getTrade(int tradeId) {
		if(!trades.containsKey(tradeId)) return null;
		return trades.get(tradeId);
	}
	
	public List<TradeBase> getPlayerTrades(OfflinePlayer p) {
		List<TradeBase> playerTrades = new ArrayList<TradeBase>();
		Iterator<TradeBase> i = trades.values().iterator();
		while(i.hasNext()) {
			TradeBase t = i.next();
			if(t.getOwner().equals(p)) {
				playerTrades.add(t);
			}
		}
		return playerTrades;
	}

	
	/**
	 * Convenience method for getting an ItemBid
	 * @param bidId The ID of the bid to return.
	 * @return The requested ItemBid, or null if there is no ItemBid with that ID.
	 */
	public ItemBid getBid(int bidId) {
		TradeBase t = getTrade(bidId);
		if(t instanceof ItemBid) {
			return (ItemBid)t;
		}
		return null;
	}

	public void newBid(ItemBid i) {
		// TODO: This should add a bid TO a trade, not just on its own.
		// This is currently just for debugging.
		this.trades.put(i.getId(), i);
		
	}

}
