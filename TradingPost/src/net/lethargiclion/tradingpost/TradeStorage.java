package net.lethargiclion.tradingpost;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

/**
 * An automatically serializable class designed to store TradingPost data.
 * @author TerrorBite
 *
 */
public class TradeStorage implements ConfigurationSerializable {
	
	static int instances = 0;
	int myinstance = 0;
	
	private int currentId;
	private Map<Integer, TradeBase> trades;
	private Set<QueuedItemDelivery> deliveries;
	
	public TradeStorage() {
		trades = new HashMap<Integer, TradeBase>();
		currentId = 0;
	}
	
	@SuppressWarnings("unchecked")
	public TradeStorage(Map<String, Object> serialData) {
		// DEBUG: Count how many instances of us are created.
		myinstance = ++instances;
		// DEBUG: logging
		TradingPost.log.info(String.format("[DEBUG] TradeStorage instance %d", myinstance));
		currentId = serialData.containsKey("currentId")?
				(Integer)serialData.get("currentId"):0;
		
		// DEBUG: logging
		TradingPost.log.info(String.format("[DEBUG] Instance %d got currentId=%d", myinstance, currentId));

		// Initialize our list of trades.
		
		Collection<TradeBase> tradelist = null;
		trades = new HashMap<Integer, TradeBase>();
		
		try {
			// Attempt to cast incoming to a collection of TradeBase
			// The deserialization of the TradeBase objects in the list should have
			// been done for us by the YAML parser.
			tradelist = (Collection<TradeBase>)serialData.get("trades");
		} catch(ClassCastException ex) {
			TradingPost.log.log(Level.SEVERE,
					"Unable to deserialize: Invalid trade data.", ex);
		}
		// If that didn't go well, make an empty list
		if(trades == null) tradelist = new ArrayList<TradeBase>();
		TradingPost.log.info(String.format("Instance %d has %d new trades", myinstance, trades.size()));
		
		Iterator<TradeBase> i = tradelist.iterator();
		while(i.hasNext()) {
			TradeBase next = i.next();
			trades.put(next.getId(), next);
		}
		
		// Initialize our list of pending deliveries.
		deliveries = null;
		
		try {
			// Attempt to cast incoming data to a collection of TradeBase
			// The deserialization of the QueuedItemDelivery objects in the list
			// should have been done for us by the YAML parser.
			deliveries = (Set<QueuedItemDelivery>)serialData.get("deliveries");
		} catch(ClassCastException ex) {
			TradingPost.log.log(Level.SEVERE,
					"Unable to deserialize: Invalid delivery data.", ex);
		}
		// If that didn't go well, make an empty list
		if(deliveries == null) deliveries = new HashSet<QueuedItemDelivery>();
		TradingPost.log.info(String.format("Instance %d has %d new deliveries", myinstance, deliveries.size()));
	}
	
	/**
	 * Retrieves the next ID value that should be used for a Trade or Bid.
	 * @return
	 */
	int getNextId() {
		return currentId++;
	}
	
	TradeBase getTrade(Integer id) throws TradeNotFoundException {
		if(trades.containsKey(id)) {
			return trades.get(id);
		}
		throw new TradeNotFoundException(String.format("Trade with id %d was not found.", id));
	}
	
	void addTrade(TradeBase tr) {
		this.trades.put(tr.getId(), tr);
	}
	
	TradeBase removeTrade(Integer id) throws TradeNotFoundException {
		if(trades.containsKey(id)) {
			return trades.remove(id);
		}
		throw new TradeNotFoundException(String.format("Trade with id %d was not found.", id));
	}
	
	Collection<QueuedItemDelivery> getDeliveries() {
		return Collections.unmodifiableSet(deliveries);
	}
	
	Iterator<QueuedItemDelivery> getDeliveryIterator() {
		return deliveries.iterator();
	}
	
	void addDelivery(QueuedItemDelivery d) {
		deliveries.add(d);
	}
	
	void removeDelivery(QueuedItemDelivery d) {
		deliveries.remove(d);
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
	
	@Override
	public Map<String, Object> serialize() {
		
		Map<String, Object> serial = new LinkedHashMap<String, Object>();
		
		// Save current ID
		serial.put("currentId", currentId);
		
		// Serialize each trade into a list of maps
		List<Map<String, Object>> subsection = new ArrayList<Map<String, Object>>();
		{ // New scope for iterator
			Iterator<TradeBase> i = trades.values().iterator();
			while(i.hasNext()) {
				TradeBase t = i.next();
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
