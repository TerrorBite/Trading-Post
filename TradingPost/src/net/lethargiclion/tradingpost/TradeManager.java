package net.lethargiclion.tradingpost;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

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
	 * Holds the pending item deliveries.
	 */
	Collection<PendingItemDelivery> pendingDeliveries;
	
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
			// Load our Storage class out of the config object
			storage = (TradeStorage)tradeStorageConfig.get("storage");
		} catch (ClassCastException e) {
			// Config didn't give us the correct class
			TradingPost.log.log(Level.SEVERE, String.format("[TradingPost] TradeManager: Got an invalid class, expecting %s", TradeStorage.class.getName()), e);
		}
		if(storage == null) {
			storage = new TradeStorage();
			TradingPost.log.warning("[TradingPost] TradeManager: Failed to load from storage.yml, initialized new TradeStorage!");
		}
		
		else TradingPost.log.info("[TradingPost] TradeManager: Successfully restored trading data.");
		
		// Now load the data into our own data structures
		trades = new LinkedHashMap<Integer, TradeBase>();
		if(storage.trades == null) {
			TradingPost.log.severe("[TradingPost] TradeManager: Null trades list in TradeStorage!");
		} else {
			Iterator<TradeBase> i = storage.trades.iterator();
			while(i.hasNext()) {
				TradeBase t = i.next();
				trades.put(t.getId(), t);
			}
		}
		pendingDeliveries = storage.deliveries;
		this.currentId = storage.currentId;
	}
	
	public void serialize() {
		if(storage == null) throw new IllegalStateException("Can't serialize with a null storage object!");
		storage.setValues(currentId, trades.values(), pendingDeliveries);
		if(tradeStorageConfig == null) loadStorage();
		tradeStorageConfig.set("storage", storage);
		saveStorage();
		TradingPost.log.info("[TradingPost] TradeManager: Successfully saved trading data.");
	}
	
	private void loadStorage() {
		if(plugin == null) throw new IllegalStateException("loadStorage() was called before the TradeManager was initialized!");
		if(tradeStorageFile == null) {
			tradeStorageFile = new File(plugin.getDataFolder(), "storage.yml");
		}
		try {
			tradeStorageConfig = YamlConfiguration.loadConfiguration(tradeStorageFile);
		} catch (Exception e) {
			TradingPost.log.log(Level.WARNING, "Internal error occurred while loading storage.yml, falling back to defaults", e);
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
		        TradingPost.log.log(Level.SEVERE, "Could not persist storage to " + tradeStorageFile, ex);
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
	
	/**
	 * Delivers items to a player. The items will be delivered immediately if
	 * the player is online.
	 * @param p The player to deliver to.
	 * @param items The items to deliver.
	 */
	public void deliverItems(OfflinePlayer p, List<ItemStack> items) {
		deliverItems(p, items, false);
	}
	
	/**
	 * Delivers items to a player. The items will be delivered immediately if
	 * the player is online, unless forceDelay is set to {@code true}.
	 * @param p The player to deliver to.
	 * @param items The items to deliver.
	 * @param forceDelay If true, will force the items to be delivered later.
	 */
	public void deliverItems(OfflinePlayer p, List<ItemStack> items, boolean forceDelay) {
		if(p.isOnline() && !forceDelay) {
			// Try and deliver the items now
			Map<Integer, ItemStack> undelivered = p.getPlayer().getInventory().addItem(items.toArray(new ItemStack[items.size()]));
			
			// If some items can't fit, save them for later
			if(!undelivered.isEmpty()) {
				p.getPlayer().sendMessage("Some items wouldn't fit in your inventory. They've been saved for later.");
				pendingDeliveries.add(new PendingItemDelivery(p, undelivered.values().toArray(new ItemStack[undelivered.values().size()])));
			}
		}
		else {
			// Deliver the items later
			pendingDeliveries.add(new PendingItemDelivery(p, items));
		}
	}
	
	/**
	 * Attempts to deliver all pending items to the given player.
	 * @param p The player to deliver to.
	 */
	public void deliverPending(OfflinePlayer p) {
		Iterator<PendingItemDelivery> i = pendingDeliveries.iterator();
		while(i.hasNext()) {
			PendingItemDelivery delivery = i.next();
			if(delivery.getTarget().equals(p)) {
				// Attempt delivery of items.
				if(delivery.deliver()) {
					// Remove pending delivery if it was successful.
					i.remove();
				}
			}
		}
	}

	public void newBid(ItemBid i) {
		// TODO: This should add a bid TO a trade, not just on its own.
		// This is currently just for debugging.
		this.trades.put(i.getId(), i);
		
	}

}
