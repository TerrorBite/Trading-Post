package net.lethargiclion.tradingpost;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * An automatically serializable class designed to store TradingPost data.
 * @author TerrorBite
 *
 */
public class TradeStorage implements ConfigurationSerializable {
	
	public int currentId;
	public Collection<TradeBase> trades;
	
	@SuppressWarnings("unchecked")
	public TradeStorage(Map<String, Object> serialData) {
		TradingPost.getManager().getPlugin().log.info(serialData.keySet().toString());
		currentId = serialData.containsKey("currentId")?
				(Integer)serialData.get("currentId"):0;
		
		trades = new ArrayList<TradeBase>();
		
		Collection<Map<String, Object>> tradeMap;
		try {
			tradeMap = (Collection<Map<String, Object>>)serialData.get("trades");
		} catch(ClassCastException ex) {
			tradeMap = new ArrayList<Map<String, Object>>();
			TradingPost.getManager().getPlugin().log.log(Level.SEVERE,
					"Unable to deserialize: Invalid data.", ex);
		} catch(NullPointerException ex) {
			tradeMap = new ArrayList<Map<String, Object>>();
			TradingPost.getManager().getPlugin().log.log(Level.SEVERE,
					"Unable to deserialize: Null pointer.", ex);
		}
		if(tradeMap == null) tradeMap = new ArrayList<Map<String, Object>>();
		Iterator<Map<String, Object>> i = tradeMap.iterator();
		
		while(i.hasNext()) {
			trades.add(TradeBase.deserialize(serialData));
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
		serial.put("trades", trades);
		
		return serial;
	}

}
