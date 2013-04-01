package net.lethargiclion.tradingpost;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.lethargiclion.tradingpost.QueuedItemDelivery.DeliveryResult;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

/**
 * A {@link ConfigurationSerializable} class which stores TradingPost data.<br>
 * An instance of TradeStorage is responsible for maintaining the data structures
 * which hold the trading data, and for returning the correct trade when called upon.
 * @author TerrorBite
 *
 */
public class TradeStorage implements ConfigurationSerializable {
	
	// Data structures
	private int currentId;
	private Map<Integer, GenericTrade> trades;
	private Collection<QueuedItemDelivery> deliveries;
	
	// Constructor
	public TradeStorage() {
		trades = new HashMap<Integer, GenericTrade>();
		currentId = 0;
	}
	
	@SuppressWarnings("unchecked")
	public TradeStorage(Map<String, Object> serialData) {
		currentId = serialData.containsKey("currentId")?
				(Integer)serialData.get("currentId"):0;
		

		// Initialize our list of trades.
		
		Collection<GenericTrade> tradelist = null;
		trades = new HashMap<Integer, GenericTrade>();
		
		try {
			// Attempt to cast incoming to a collection of GenericTrade
			// The deserialization of the GenericTrade objects in the list should have
			// been done for us by the YAML parser.
			tradelist = (Collection<GenericTrade>)serialData.get("trades");
		} catch(ClassCastException ex) {
			TradingPost.log.log(Level.SEVERE,
					"Unable to deserialize: Invalid trade data.", ex);
		}
		// If that didn't go well, leave list empty
		if(tradelist != null) {
			Iterator<GenericTrade> i = tradelist.iterator();

			while(i.hasNext()) {
				GenericTrade next = i.next();
				trades.put(next.getId(), next);
			}
		}
		
		// Initialize our list of pending deliveries.
		deliveries = null;
		
		try {
			// Attempt to cast incoming data to a collection of GenericTrade
			// The deserialization of the QueuedItemDelivery objects in the list
			// should have been done for us by the YAML parser.
			deliveries = (Collection<QueuedItemDelivery>)serialData.get("deliveries");
		} catch(ClassCastException ex) {
			TradingPost.log.log(Level.SEVERE,
					"Unable to deserialize: Invalid delivery data.", ex);
		}
		// If that didn't go well, make an empty list
		if(deliveries == null) deliveries = new ArrayList<QueuedItemDelivery>();
	}
	
	/**
	 * Retrieves the next ID value that should be used for a Trade or Bid.
	 * @return
	 */
	int getNextId() {
		return currentId++;
	}
	
	GenericTrade getTrade(Integer id) throws TradeNotFoundException {
		if(trades.containsKey(id)) {
			return trades.get(id);
		}
		throw new TradeNotFoundException(String.format("A trade with id %d was not found.", id));
	}
	
	/**
	 * Retrieves a List of trades that this player owns.
	 * @param p The OfflinePlayer to search for.
	 * @return A List of a player's trades.
	 */
	List<GenericTrade> getPlayerTrades(OfflinePlayer p) {
		List<GenericTrade> playerTrades = new ArrayList<GenericTrade>();
		for(GenericTrade tr: trades.values()) {
			if(tr.getOwner().equals(p)) {
				playerTrades.add(tr);
			}
		}
		return playerTrades;
	}
	
	List<GenericTrade> getTradesByPage(int page, int amountPerPage) {
		if(page < 0 || amountPerPage <= 0) {
			throw new IllegalArgumentException();
		}
		
		List<GenericTrade> reverseTrades = new ArrayList<GenericTrade>();
		
		reverseTrades.addAll(trades.values());
		
		Collections.reverse(reverseTrades);
		
		int from = page*amountPerPage;
		int to = from + amountPerPage;
		
		if(from >= reverseTrades.size()) return new ArrayList<GenericTrade>();
		if(to > reverseTrades.size()) to = reverseTrades.size();
		
		return reverseTrades.subList(from, to);
	}
	
	void addTrade(GenericTrade tr) {
		this.trades.put(tr.getId(), tr);
	}
	
	GenericTrade removeTrade(Integer id) throws TradeNotFoundException {
		if(trades.containsKey(id)) {
			GenericTrade removed = trades.remove(id);
			
			// If this is a GenericBid then we also need to remove its ID
			// from the list of bids on its parent GenericOffer.
			if(removed instanceof GenericBid) {
				GenericBid b = (GenericBid)removed;
				if(trades.containsKey(b.getParentId()) && trades.get(b.getParentId()) instanceof GenericOffer) {
					GenericOffer o = (GenericOffer)trades.get(b.getParentId());
					// We can't use getBids() as that returns a read-only copy.
					// Instead we use the protected access level to access the field itself.
					o.bids.remove(b.getId());
				}
			}
			
			return removed;
		}
		throw new TradeNotFoundException(String.format("A trade with id %d was not found.", id));
	}
	
	Collection<QueuedItemDelivery> getDeliveries() {
		return Collections.unmodifiableCollection(deliveries);
	}
	
	/**
	 * Attempts to deliver all pending items to the given player.<br>
	 * <br>
	 * This is a method of {@link TradeStorage} instead of {@link TradeManager} because it
	 * requires access to an iterator over the deliveries collection. 
	 * @param p The {@link OfflinePlayer} whose queued items should be delivered.
	 * @returns A {@link DeliveryResult} containing the result of this delivery attempt.
	 */
	public DeliveryResult deliverQueued(OfflinePlayer p) {
		if(!p.isOnline()) {
			return DeliveryResult.PLAYER_OFFLINE;
		}
		DeliveryResult state = DeliveryResult.NO_ITEMS;
		Iterator<QueuedItemDelivery> i = deliveries.iterator();
		// If there are no items to be delivered at all, return appropriate status
		if(!i.hasNext()) return DeliveryResult.NO_ITEMS;
		while(i.hasNext()) {
			QueuedItemDelivery delivery = i.next();
			if(delivery.getTarget().equals(p)) {
				// Attempt delivery of items.
				if(delivery.deliver() == DeliveryResult.SUCCESS) {
				    state = DeliveryResult.SUCCESS;
					// Remove pending delivery if it was successful.
					i.remove();
				}
				else state = DeliveryResult.NOT_ENOUGH_SPACE;
			}
		}
		return state;
	}
	
	/**
     * Returns a status indicating whether items can be delivered to the given player.<br>
     * Does not take into account whether or not the player has adequate inventory space.<br>
     * <br>
     * This is a method of {@link TradeStorage} instead of {@link TradeManager} because it
     * requires access to an iterator over the deliveries collection. 
     * @param p The {@link OfflinePlayer} whose queued items should be delivered.
     * @returns A {@link DeliveryResult} containing the result of this delivery attempt.
     */
	public DeliveryResult canDeliver(OfflinePlayer p) {
	    if(!p.isOnline()) {
            return DeliveryResult.PLAYER_OFFLINE;
        }
        Iterator<QueuedItemDelivery> i = deliveries.iterator();
        while(i.hasNext()) {
            QueuedItemDelivery delivery = i.next();
            if(delivery.getTarget().equals(p)) {
                // Player has items waiting, so return success
                return DeliveryResult.SUCCESS;
            }
        }
        // If there are no items to be delivered for this player, return appropriate status
        return DeliveryResult.NO_ITEMS;
    }
	
	/**
	 * Adds a {@link QueuedItemDelivery} to the list of queued deliveries.
	 * @param d The {@link QueuedItemDelivery} to be added.
	 */
	void addDelivery(QueuedItemDelivery d) {
		deliveries.add(d);
	}
	
	/**
	 * Removes a {@link QueuedItemDelivery} from the list of queued deliveries.<br>
	 * <br>
	 * <b>Note:</b> This is not actually required, and in fact should never be called in normal operation,
	 *  since deliveries are automatically removed from the list when they complete successfully.
	 *  However it is implemented for completeness, debugging purposes and possible future use
	 *  (e.g. allowing admins to clear a pending delivery).
	 * @param d The {@link QueuedItemDelivery} to be removed.
	 */
	void removeDelivery(QueuedItemDelivery d) {
		deliveries.remove(d);
	}
	
	public Map<String, Object> serialize() {
		
		Map<String, Object> serial = new LinkedHashMap<String, Object>();
		
		// Save current ID
		serial.put("currentId", currentId);
		
		// Serialize each trade into a list of maps
		List<Map<String, Object>> subsection = new ArrayList<Map<String, Object>>();
		{ // New scope for iterator
			Iterator<GenericTrade> i = trades.values().iterator();
			while(i.hasNext()) {
				GenericTrade t = i.next();
				Map<String, Object> trade = new LinkedHashMap<String, Object>();
				trade.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY,
						t.getClass().getName());
				trade.putAll(t.serialize());
				subsection.add(trade);
			}
		}
		// Add to map
		serial.put("trades", subsection);
		
		subsection = new ArrayList<Map<String, Object>>();
		{ // New scope for iterator
			Iterator<QueuedItemDelivery> i = deliveries.iterator();
			while(i.hasNext()) {
				QueuedItemDelivery t = i.next();
				Map<String, Object> delivery = new LinkedHashMap<String, Object>();
				delivery.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY,
						t.getClass().getName());
				delivery.putAll(t.serialize());
				subsection.add(delivery);
			}
		}
		// Add to map
		serial.put("deliveries", subsection);
		
		return serial;
	}

}
