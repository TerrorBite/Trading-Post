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
	
	public TradeStorage() {
		trades = new ArrayList<TradeBase>();
		currentId = 0;
	}
	
	@SuppressWarnings("unchecked")
	public TradeStorage(Map<String, Object> serialData) {
		myinstance = ++instances;
		TradingPost.getManager().getPlugin().log.info(String.format("TradeStorage instance %d", myinstance));
		currentId = serialData.containsKey("currentId")?
				(Integer)serialData.get("currentId"):0;
		
		TradingPost.getManager().getPlugin().log.info(String.format("Instance %d got currentId=%d", myinstance, currentId));

		
		trades = new ArrayList<TradeBase>();
		
		Collection<Object> newTrades = null;
		try {
			newTrades = (Collection<Object>)serialData.get("trades");
		} catch(ClassCastException ex) {
			TradingPost.getManager().getPlugin().log.log(Level.SEVERE,
					"Unable to deserialize: Invalid data.", ex);
		}
		if(newTrades == null) newTrades = new ArrayList<Object>();
		TradingPost.getManager().getPlugin().log.info(String.format("Instance %d has %d new trades", myinstance, newTrades.size()));
		Iterator<Object> i = newTrades.iterator();
		
		while(i.hasNext()) {
			Object next = i.next();
			//trades.add(TradeBase.deserialize(serialData));
			if(next instanceof TradeBase) {
				TradingPost.getManager().getPlugin().log.warning("Got an already deserialized object! Cool!");
				trades.add((TradeBase)next);
			}
			else trades.add((TradeBase) ConfigurationSerialization.deserializeObject((Map<String, Object>)next));
		}
	}
	
	public void setValues(int currentId, Collection<TradeBase> trades) {
		this.currentId = currentId;
		this.trades = trades;
	}
	
	@Override
	public Map<String, Object> serialize() {
		
		Map<String, Object> serial = new LinkedHashMap<String, Object>();
		
		serial.put("currentId", currentId);
		
		List<Map<String, Object>> tradeList = new ArrayList<Map<String, Object>>();
		Iterator<TradeBase> i = trades.iterator();
		while(i.hasNext()) {
			TradeBase t = i.next();
			Map<String, Object> trade = new LinkedHashMap<String, Object>();
			trade.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY,
					t.getClass().getName());
			trade.putAll(t.serialize());
			tradeList.add(trade);
		}
		serial.put("trades", tradeList);
		
		return serial;
	}

}
