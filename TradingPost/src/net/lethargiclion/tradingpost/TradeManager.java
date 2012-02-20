package net.lethargiclion.tradingpost;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.lethargiclion.tradingpost.QueuedItemDelivery.DeliveryResult;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Class for managing trades.
 * @author TerrorBite
 *
 */
public class TradeManager implements Listener {
	
	/**
	 * Holds the parent TradingPost instance.
	 */
	private TradingPost plugin = null;
	
	private TradeStorage storage = null;
	
	private FileConfiguration tradeStorageConfig = null;
	private File tradeStorageFile = null;
	
	/**
	 * Constructs a new TradeManager instance.
	 * @param plugin The parent TradingPost plugin.
	 */
	public TradeManager(TradingPost plugin) {
		this(plugin, false);
	}
	
	/**
	 * Constructs a new TradeManager instance.
	 * @param plugin The parent TradingPost plugin.
	 * @param defer Should we defer initialization until later?
	 */
	public TradeManager(TradingPost plugin, boolean defer) {
		this.plugin = plugin;
		if(!defer) deserialize();
	}
	
	/**
	 * Loads the stored Trading Post data from the YAML file on disk.
	 */
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
			// If for some reason our storage class failed to deserialize,
			// create a new blank one.
			storage = new TradeStorage();
			TradingPost.log.warning("[TradingPost] TradeManager: Failed to load from storage.yml, initialized new TradeStorage!");	
		}
		
		else TradingPost.log.info("[TradingPost] TradeManager: Successfully restored trading data.");
		
	}
	
	public void serialize() {
		if(storage == null) throw new IllegalStateException("Can't serialize with a null storage object!");
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
		
	    // Get default persistence file from the jar.
	    InputStream defaultStorageStream = plugin.getResource("storage.yml");
	    if (defaultStorageStream != null) {
	    	// Set default values (e.g. empty storage) if no storage exists yet.
	    	tradeStorageConfig.setDefaults(YamlConfiguration.loadConfiguration(defaultStorageStream));
	    }
	}
	
	private void saveStorage() {
		if (tradeStorageConfig == null || tradeStorageFile == null) {
			// Don't save if there is nothing to save
		    return;
	    }
		
		// Make a backup of the old storage file just in case
		File backupFile = new File(plugin.getDataFolder(), "storage.yml.old");
		try {
			// Create backup file if it doesn't exist (may throw IOException)
			backupFile.createNewFile();
			
			// Open files for reading/writing (may throw FileNotFoundException)
			FileInputStream in = new FileInputStream(tradeStorageFile);
			FileOutputStream out = new FileOutputStream(backupFile);
			
			// Set up buffer
			byte[] data = new byte[4096];
			int bytes = 0;
			
			// Copy data into backup file (may throw IOException)
			while((bytes = in.read(data)) >= 0) {
				out.write(data, 0, bytes);
			}
			
			// Close files (may throw IOException)
			out.close();
			in.close();
		} catch(java.io.FileNotFoundException ex) {
			TradingPost.log.warning("[TradingPost] TradeManager: Failed to back up storage.yml: File not found");
		} catch (IOException e) {
			TradingPost.log.warning("[TradingPost] TradeManager: Failed to back up storage.yml: Read or write error");
		}
		
	    try {
	    	// Attempt to write changed config to disk
	        tradeStorageConfig.save(tradeStorageFile);
	    } catch (IOException ex) {
	        TradingPost.log.log(Level.SEVERE, "Could not persist storage to " + tradeStorageFile, ex);
	    }
	}
	
	/**
	 * Retrieves a trade from the storage.
	 * @param tradeId The ID of the trade to retrieve.
	 * @return The trade with the given ID.
	 * @throws TradeNotFoundException if there is no trade with that ID.
	 */
	public GenericTrade getTrade(int tradeId) throws TradeNotFoundException {
		return storage.getTrade(tradeId);
	}

	/**
	 * Creates a new TradeOffer with the given player selling the given items.
	 * @param p The player creating the TradeOffer.
	 * @param items The items they are offering for trade.
	 * @return the ID of the newly created trade.
	 */
	public int makeTrade(OfflinePlayer p, List<ItemStack> items) {
		TradeOffer trade = new TradeOffer(storage.getNextId(), p, items);
		storage.addTrade(trade);
		return trade.getId();
	}
	
	/**
	 * Accepts a bid.
	 * @param p The player who is accepting the bid.
	 * @param bidId The bid that they are accepting.
	 * @return true if successful.
	 * @throws TradeNotFoundException if there is no {@link ItemBid} with that ID.
	 */
	public boolean acceptBid(OfflinePlayer p, int bidId) throws TradeNotFoundException {
		ItemBid b = getBid(bidId);
		if(b == null) throw new TradeNotFoundException("This trade does not exist, or is not a bid.");
		//TODO: Check if the given player is allowed to accept this bid.
		GenericTrade trade = getTrade(b.getParentId());
		if(trade == null) {
			throw new TradeNotFoundException("This bid's parent does not exist.");
		}
		if(trade.owner != p) {
			// TODO: More meaningful error here
			throw new java.lang.SecurityException("This user does not own the trade this bid was made on.");
		}
		if(!(trade instanceof TradeOffer)) {
			throw new java.lang.ClassCastException("The parent of this bid is not a TradeOffer.");
		}
		TradeOffer st = (TradeOffer)trade;
		deliverItems(p, b.getItems());
		b.markAccepted();
		// Reject all other bids
		Iterator<Integer> i = st.getBids().iterator();
		while(i.hasNext()) {
			int nextBidId = i.next();
			if(nextBidId == bidId) continue;
			ItemBid rejected = getBid(nextBidId);
			rejected.markRejected();
			deliverItems(rejected.getOwner(), rejected.getItems());
			
		}
		return true;
	}
	
	/**
	 * Rejects a bid.
	 * @param p The player who is rejecting a bid.
	 * @param bidId The bid they are rejecting.
	 * @return True if succesful.
	 * @throws TradeNotFoundException if there is no bid by this ID.
	 */
	public boolean rejectBid(OfflinePlayer p, int bidId) throws TradeNotFoundException {
		ItemBid b = getBid(bidId);
		if(b == null) throw new TradeNotFoundException("This trade does not exist, or is not a bid.");
		//TODO: Check if the given player is allowed to reject this bid.
		deliverItems(b.getOwner(), b.getItems());
		b.markRejected();
		return true;
	}
	
	/**
	 * Withdraws a trade. Not yet implemented.
	 * @param p The player withdrawing a trade.
	 * @param tradeId The ID of the trade they are withdrawing.
	 * @return True if successful.
	 * @throws TradeNotFoundException if there is no trade by this ID.
	 */
	public boolean withdrawTrade(OfflinePlayer p, int tradeId) throws TradeNotFoundException {
		return false;
	}
	
	/**
	 * Convenience method for getting an ItemBid
	 * @param bidId The ID of the bid to return.
	 * @return The requested ItemBid, or null if there is no ItemBid with that ID.
	 * @throws TradeNotFoundException 
	 */
	public ItemBid getBid(int bidId) throws TradeNotFoundException {
		GenericTrade t = getTrade(bidId);
		if(t instanceof ItemBid) {
			return (ItemBid)t;
		}
		return null;
	}
	
	/**
	 * Delivers items to a player. The items will be delivered immediately if
	 * the player is online.
	 * @param p The player to deliver to.
	 * @param collection The items to deliver.
	 */
	public void deliverItems(OfflinePlayer p, Collection<ItemStack> collection) {
		deliverItems(p, collection, false);
	}
	
	/**
	 * Delivers items to a player. The items will be delivered immediately if
	 * the player is online, unless forceDelay is set to {@code true}.
	 * @param p The player to deliver to.
	 * @param collection The items to deliver.
	 * @param forceDelay If true, will force the items to be delivered later.
	 * @returns true if items were delivered without being queued.
	 */
	public boolean deliverItems(OfflinePlayer p, Collection<ItemStack> collection, boolean forceDelay) {
		if(p.isOnline() && !forceDelay) {
			// Try and deliver the items now
			Map<Integer, ItemStack> undelivered = p.getPlayer().getInventory().addItem(collection.toArray(new ItemStack[collection.size()]));
			
			// If all items were delivered, return true
			if(undelivered.isEmpty()) {
				return true;
			} // else some items can't fit, save them for later
			storage.addDelivery(new QueuedItemDelivery(p, undelivered.values().toArray(new ItemStack[undelivered.values().size()])));
			
		}
		else {
			// Player is offline, deliver the items later
			storage.addDelivery(new QueuedItemDelivery(p, collection));
		}
		return false;
	}
	
	/**
	 * Attempts to deliver all pending items to the given player.
	 * @param p The player to deliver to.
	 * @returns true if items were delivered.
	 */
	public DeliveryResult deliverQueued(OfflinePlayer p) {
		if(!p.isOnline()) {
			return DeliveryResult.PLAYER_OFFLINE;
		}
		DeliveryResult state = DeliveryResult.SUCCESS;
		Iterator<QueuedItemDelivery> i = storage.getDeliveryIterator();
		// If there are no items to be delivered, return appropriate status
		if(!i.hasNext()) return DeliveryResult.NO_ITEMS;
		while(i.hasNext()) {
			QueuedItemDelivery delivery = i.next();
			if(delivery.getTarget().equals(p)) {
				// Attempt delivery of items.
				if(delivery.deliver() == DeliveryResult.SUCCESS) {
					// Remove pending delivery if it was successful.
					i.remove();
				}
				else state = DeliveryResult.NOT_ENOUGH_SPACE;
			}
		}
		return state;
	}

	@Deprecated
	public void newBid(ItemBid i) {
		// TODO: This should add a bid TO a trade, not just on its own.
		// This is currently just for debugging.
		storage.addTrade(i);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		// Don't bother checking if this event is cancelled. Technically this is a chat event.
		e.getPlayer().sendMessage("You have items waiting for you.");
		this.deliverQueued(e.getPlayer());
	}

}
