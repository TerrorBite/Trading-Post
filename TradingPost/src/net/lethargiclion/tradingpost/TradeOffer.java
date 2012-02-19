package net.lethargiclion.tradingpost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a trade where a user is offering items in return for bids.
 * @author TerrorBite
 *
 */
public class TradeOffer extends TradeBase {
	
	List<Integer> bids;
	int acceptedBidId;
	
	public TradeOffer(int id, OfflinePlayer p, List<ItemStack> items) {
		this(id, p, items, new Date(),
				TradeStatus.open, new ArrayList<Integer>());
	}
	
	public TradeOffer(int id, OfflinePlayer p, List<ItemStack> items,
			Date timestamp, TradeStatus status, List<Integer> bids) {
		this.owner = p;
		this.items = items;
		this.id = id;
		this.timestamp = timestamp;
		this.status = status;
		this.bids = bids;
	}
	
	public List<Integer> getBids() {
		return Collections.unmodifiableList(bids);
	}
	
	public void addBid(ItemBid bid) {
		bids.add(bid.getId());
	}
	
	public void acceptBid(int bidId) {		
		status = TradeStatus.completed;
		// Accept this bid
		this.acceptedBidId = bidId;
	}
	
	public void markWithdrawn() {
		// Silently ignore attempts to withdraw a bid that is not open.
		if(status != TradeStatus.open) return;
		status = TradeStatus.withdrawn;
	}

	@SuppressWarnings("unchecked")
	public static TradeOffer deserialize(Map<String, Object> serial) {
		// TODO: this is incomplete.
		OfflinePlayer owner = Bukkit.getOfflinePlayer((String)serial.get("owner"));
		
		int id = (Integer)serial.get("id");
		
		//Deserialize list of items.
		List<ItemStack> items = new ArrayList<ItemStack>();
		/*
		 * Actually the following is now done for us by the parser.
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> itemstacks = (ArrayList<Map<String,Object>>)serial.get("items");
		Iterator<Map<String, Object>> i = itemstacks.iterator();
		while(i.hasNext()) {
			items.add(ItemStack.deserialize(i.next()));
		}
		*/
		items = (List<ItemStack>)serial.get("items");
		
		return new TradeOffer(id, owner, items);
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> serial = new LinkedHashMap<String, Object>();
		serial.put("owner",	this.owner.getName());
		serial.put("id", this.id);
		serial.put("status", this.status.name());
		serial.put("bids", this.bids);
		
		// Serialize items
		List<Map<String, Object>> itemstacks = new ArrayList<Map<String,Object>>();
		Iterator<ItemStack> i = items.iterator();
		while(i.hasNext()) {
			ItemStack s = i.next();
			Map<String, Object> stack = new LinkedHashMap<String, Object>();
			stack.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY,
					ItemStack.class.getName());
			stack.putAll(s.serialize());
			itemstacks.add(stack);
		}
		serial.put("items", itemstacks);
		
		return serial;
	}
	
	@Override
	public int hashCode() {
		Iterator<Integer> i = bids.iterator();
		int bidhash = 0;
		int n = bids.size();
		while(i.hasNext()) {
			bidhash += (int)(Math.pow(i.next().intValue()*31, --n));
		}
		return super.hashCode() ^ bidhash;
	}

}
