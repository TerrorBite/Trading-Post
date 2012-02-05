package net.lethargiclion.tradingpost;

import java.util.ArrayList;
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

	static {
		ConfigurationSerialization.registerClass(TradeStorage.class);
	}
	
	public int currentId;
	public List<TradeBase> trades;
	
	@SuppressWarnings("unchecked")
	public TradeStorage(Map<String, Object> serialData) {
		currentId = (Integer)serialData.get("currentId");
		
		trades = new ArrayList<TradeBase>();
		
		List<Map<String, Object>> tradeMap;
		try {
			tradeMap = (List<Map<String, Object>>)serialData.get("trades");
		} catch(ClassCastException ex) {
			tradeMap = new ArrayList<Map<String, Object>>();
			TradingPost.getManager().getPlugin().log.log(Level.SEVERE,
					"Unable to deserialize: Invalid data.", ex);
		}
		Iterator<Map<String, Object>> i = tradeMap.iterator();
		
		while(i.hasNext()) {
			trades.add(TradeBase.deserialize(serialData));
		}
	}
	
	public void setValues(int currentId, List<TradeBase> trades) {
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
