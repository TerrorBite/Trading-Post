package net.lethargiclion.tradingpost;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

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
	
	public int currentId;
	public Collection<TradeBase> trades;
	public Collection<PendingItemDelivery> deliveries;
	
	public TradeStorage() {
		trades = new ArrayList<TradeBase>();
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
		trades = null;
		
		try {
			// Attempt to cast incoming to a collection of TradeBase
			// The deserialization of the TradeBase objects in the list should have
			// been done for us by the YAML parser.
			trades = (Collection<TradeBase>)serialData.get("trades");
		} catch(ClassCastException ex) {
			TradingPost.log.log(Level.SEVERE,
					"Unable to deserialize: Invalid trade data.", ex);
		}
		// If that didn't go well, make an empty list
		if(trades == null) trades = new ArrayList<TradeBase>();
		TradingPost.log.info(String.format("Instance %d has %d new trades", myinstance, trades.size()));
		
		// Initialize our list of pending deliveries.
		deliveries = null;
		
		try {
			// Attempt to cast incoming data to a collection of TradeBase
			// The deserialization of the PendingItemDelivery objects in the list
			// should have been done for us by the YAML parser.
			deliveries = (Collection<PendingItemDelivery>)serialData.get("trades");
		} catch(ClassCastException ex) {
			TradingPost.log.log(Level.SEVERE,
					"Unable to deserialize: Invalid delivery data.", ex);
		}
		// If that didn't go well, make an empty list
		if(deliveries == null) deliveries = new ArrayList<PendingItemDelivery>();
		TradingPost.log.info(String.format("Instance %d has %d new trades", myinstance, trades.size()));
	}
	
	public void setValues(int currentId, Collection<TradeBase> trades, Collection<PendingItemDelivery> deliveries) {
		this.currentId = currentId;
		this.trades = trades;
		this.deliveries = deliveries;
	}
	
	@Override
	public Map<String, Object> serialize() {
		
		Map<String, Object> serial = new LinkedHashMap<String, Object>();
		
		// Save current ID
		serial.put("currentId", currentId);
		
		// Serialize each trade into a list of maps
		List<Map<String, Object>> subsection = new ArrayList<Map<String, Object>>();
		{ // New scope for iterator
			Iterator<TradeBase> i = trades.iterator();
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
			Iterator<PendingItemDelivery> i = deliveries.iterator();
			while(i.hasNext()) {
				PendingItemDelivery t = i.next();
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
