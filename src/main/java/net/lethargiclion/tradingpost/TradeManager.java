package net.lethargiclion.tradingpost;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.lethargiclion.tradingpost.QueuedItemDelivery.DeliveryResult;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

/**
 * The TradeManager is responsible for keeping track of trades in progress.
 * It takes care of altering the status of trades and delivering items to players.
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
	
	/**
	 * Stores the Trading Post data onto disk for later retrieval.
	 */
	public void serialize() {
		if(storage == null) throw new IllegalStateException("Can't serialize with a null storage object!");
		if(tradeStorageConfig == null) loadStorage();
		tradeStorageConfig.set("storage", storage);
		saveStorage();
		TradingPost.log.info("[TradingPost] TradeManager: Successfully saved trading data.");
	}
	
	/**
	 * (Internal) Loads persistance data from disk into a {@link FileConfiguration} object.
	 */
	private void loadStorage() {
		if(tradeStorageFile == null) {
			tradeStorageFile = new File(plugin.getDataFolder(), "storage.yml");
			if(tradeStorageFile.exists()) {
				try {
					tradeStorageConfig = YamlConfiguration.loadConfiguration(tradeStorageFile);
				} catch (Exception e) {
					TradingPost.log.log(Level.WARNING, "Internal error occurred while loading storage.yml, falling back to defaults", e);
				    // Get default persistence file from the jar.
				    InputStream defaultStorageStream = plugin.getResource("storage.yml");
				    if (defaultStorageStream != null) {
				    	// Set default values (e.g. empty storage) if no storage exists yet.
				    	tradeStorageConfig = YamlConfiguration.loadConfiguration(defaultStorageStream);
				    } else {
				    	tradeStorageConfig = new YamlConfiguration();
				    }
				}
			}
			else {
				TradingPost.log.info("[TradingPost] No storage file exists, creating a new one.");
				// Get default persistence file from the jar.
			    InputStream defaultStorageStream = plugin.getResource("storage.yml");
			    if (defaultStorageStream != null) {
			    	// Set default values (e.g. empty storage) if no storage exists yet.
			    	tradeStorageConfig = YamlConfiguration.loadConfiguration(defaultStorageStream);
			    } else {
			    	tradeStorageConfig = new YamlConfiguration();
			    }
			}
		}
	}
	
	/**
	 * (Internal) Saves the {@link FileConfiguration} to disk.<br>
	 * Also makes a backup of the previous file.
	 */
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
	 * Creates a new TradeOffer with the given player selling the given items.<br>
	 * <br>
	 * This is a convenience function as it does not allow the flexibility
	 * to add custom implementations of GenericOffer.
	 * @param p The player creating the TradeOffer.
	 * @param items The items they are offering for trade.
	 * @return the ID of the newly created trade.
	 */
	public int makeOffer(OfflinePlayer p, Collection<ItemStack> items) {
		GenericOffer trade = new TradeOffer(storage.getNextId(), p, items);
		storage.addTrade(trade);
		return trade.getId();
	}
	
	public int makeBid(OfflinePlayer p, Collection<ItemStack> items, GenericOffer offer) {
		GenericBid bid = new ItemBid(storage.getNextId(), p, items, offer.getId());
		offer.addBid(bid);
		storage.addTrade(bid);
		return bid.getId();
	}
	
	/**
	 * Accepts a bid on a trade offer, including performing the exchange and delivery of
	 * items, and rejecting all other (non-accepted) bids on the offer.
	 * @param p The player who is accepting the bid.
	 * @param bidId The bid that they are accepting.
	 * @return true if successful.
	 * @throws TradeNotFoundException if there is no {@link ItemBid} with that ID.
	 */
	public boolean acceptBid(OfflinePlayer p, GenericBid winningBid) throws TradeNotFoundException, java.lang.SecurityException {
		if(winningBid.getStatus() != TradeStatus.open) {
			throw new IllegalStateException("Only bids in an open state can be accepted.");
		}
		GenericOffer offer = getOffer(winningBid.getParentId());
		if(!offer.owner.getName().equals(p.getName())) {
			throw new java.lang.SecurityException("This user does not own the trade this bid was made on.");
		}
		
		// Mark winning bid as accepted
		winningBid.markAccepted();
		offer.markAccepted(winningBid.getId());
		
		// Exchange items
		exchangeItems(offer, winningBid);
		
		// Reject all other bids
		for(int i: offer.getBids()) {
			if(i == winningBid.getId()) continue;
			rejectBid(getBid(i));
		}
		return true;
	}
	
	/**
	 * (Internal) Rejects a bid, returning the items held to their owner.
	 * @param p The player who is rejecting a bid.
	 * @param rejectedBid The bid that is being rejected.
	 */
	private void rejectBid(GenericBid rejectedBid) {
		// Change bid status to rejected
		rejectedBid.markRejected();
		
		// Return items to owner
		returnItems(rejectedBid);
	}
	
	/**
	 * Withdraws a trade, returning the items held to their owner.
	 * @param p The player withdrawing a trade.
	 * @param tradeId The ID of the trade they are withdrawing.
	 * @return True if successful. False if trade has already been withdrawn.
	 */
	public boolean withdrawTrade(OfflinePlayer p, GenericTrade trade) {
		if(!trade.getOwner().getName().equals(p.getName())) {
			throw new SecurityException("This user does not own the trade.");
		}
		if(trade.getStatus() == TradeStatus.withdrawn) return false;
		trade.markWithdrawn();
		returnItems(trade);
		
		// If this is an offer, then we need to reject all bids
		// so that the items go back to their owners.
		if(trade instanceof GenericOffer) {
			GenericOffer offer = (GenericOffer)trade;
			// Reject all bids on this offer
			for(int i: offer.getBids()) {
				try {
					rejectBid(getBid(i));
				} catch (TradeNotFoundException e) {
					continue;
				}
			}
		}
		return true;
	}
	
	/**
	 * Returns a bid by the given ID.
	 * @param bidId The ID of the bid to return.
	 * @return The requested ItemBid, or null if there is no ItemBid with that ID.
	 * @throws TradeNotFoundException 
	 */
	public GenericBid getBid(int bidId) throws TradeNotFoundException {
		GenericTrade t = storage.getTrade(bidId);
		if(t instanceof GenericBid) {
			return (GenericBid)t;
		}
		throw new TradeNotFoundException("The given trade ID is not the ID of a bid.");
	}
	
	/**
	 * Returns a bid by the given ID.
	 * @param bidId The ID of the bid to return.
	 * @return The requested ItemBid, or null if there is no ItemBid with that ID.
	 * @throws TradeNotFoundException 
	 */
	public GenericOffer getOffer(int offerId) throws TradeNotFoundException {
		GenericTrade t = storage.getTrade(offerId);
		if(t instanceof GenericOffer) {
			return (GenericOffer)t;
		}
		throw new TradeNotFoundException("The given trade ID is not the ID of an offer.");
	}
	
	/**
	 * Delivers items to a player. The items will be delivered immediately if
	 * the player is online.
	 * @param p The player to deliver these items to.
	 * @param collection The items to be delivered.
	 * @return {@code true} if the items were delivered immediately,
	 * or {@code false} if the items were queued instead.
	 */
	public boolean deliverItems(OfflinePlayer p, Collection<ItemStack> collection) {
		return deliverItems(p, collection, false);
	}
	
	/**
	 * (Internal) Delivers the items held by this trade back to their original owner.
	 * @param trade The trade to process.
	 */
	private void returnItems(GenericTrade trade) {
		deliverItems(trade.getOwner(), trade.getItems());
	}
	
	/**
	 * (Internal) Performs the work of delivering items to their new owners.
	 * @param trade The trade to process.
	 */
	@SuppressWarnings("unused")
	private void exchangeItems(GenericOffer trade) {
		GenericBid winningBid = null;
		try {
			winningBid = getBid(trade.getAcceptedBidId());
		} catch (TradeNotFoundException e) {
			return;
		}
		exchangeItems(trade, winningBid);
	}
	
	/**
	 * (Internal) Delivers the items from each trade to the other trade's owner.
	 * @param first One of the trades.
	 * @param second The other trade.
	 */
	private void exchangeItems(GenericTrade first, GenericTrade second) {
		deliverItems(first.getOwner(), second.getItems());
		deliverItems(second.getOwner(), first.getItems());
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
			p.getPlayer().sendMessage("[TradingPost] You have received items!"); //TODO: Make this message more informative
			
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
	 * Convenience method that calls the {@code deliverQueued()} method
	 * of this TradeManager's {@link TradeStorage} instance.<br>
	 * <br>Attempts to deliver all pending items to the given player.
	 * @see TradeStorage
	 * @param p The {@link OfflinePlayer} whose queued items should be delivered.
	 * @return A {@link DeliveryResult} describing the result of the delivery attempt.
	 */
	public DeliveryResult deliverQueued(OfflinePlayer p) {
		return storage.deliverQueued(p);
	}
	
	public List<GenericTrade> getPlayerTrades(OfflinePlayer p) {
		return storage.getPlayerTrades(p);
	}
	
	public List<GenericTrade> getPage(int page) {
		return storage.getTradesByPage(page-1, 6);
	}
	
    private class ItemDeliveryTask implements Runnable {
        Player p;
        
        public ItemDeliveryTask(Player player) {
            // TODO Auto-generated constructor stub
            this.p = player;
        }

        @Override
        public void run() {
            DeliveryResult result = storage.deliverQueued(p);
            switch(result) {
            case SUCCESS:
                p.sendMessage("[TradingPost] You have just recieved items from a trade!");
                break;
            case NOT_ENOUGH_SPACE:
                p.sendMessage("[TradingPost] You have received items from a trade, but your inventory is full.");
                p.sendMessage("Clear some inventory space, then type /tr deliver to receive the rest of your items.");
                break;
            default: // NO_ITEMS, or PLAYER_OFFLINE
                break; // do nothing
            }
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        // Don't bother checking if this event is cancelled. Technically this is a chat event.
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new ItemDeliveryTask(e.getPlayer()), 2000);
    }

}
