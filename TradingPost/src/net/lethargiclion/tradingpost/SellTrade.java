package net.lethargiclion.tradingpost;

import java.util.ArrayList;
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
public class SellTrade extends TradeBase {
	
	List<Integer> bids;
	TradeStatus status;
	int acceptedBidId;
	
	public SellTrade(OfflinePlayer p, List<ItemStack> items) {
		this(p, items, TradingPost.getManager().getNextId(), new Date(),
				TradeStatus.open, new ArrayList<Integer>());
	}
	
	public SellTrade(OfflinePlayer p, List<ItemStack> items, int id,
			Date timestamp, TradeStatus status, List<Integer> bids) {
		this.owner = p;
		this.items = items;
		this.id = id;
		this.timestamp = timestamp;
		this.status = status;
		this.bids = bids;
	}
	
	public List<Integer> getBids() {
		return bids;
	}
	
	public void makeBid(ItemBid bid) {
		bids.add(bid.getId());
	}
	
	public void acceptBid(ItemBid bid) throws TradeNotFoundException {
		int bidId = bid.getId();
		if(!bids.contains(bidId)) {
			throw new TradeNotFoundException(String.format("Trade with ID %d does not have a bid with ID %d", id, bidId));
		}
		
		status = TradeStatus.completed;
		// Accept this bid
		bid.accept(this.owner);
		// Deliver this trade's items to their new owner
		TradingPost.getManager().deliverItems(bid.getOwner(), items);
		
		// Reject all other bids
		Iterator<Integer> i = bids.iterator();
		while(i.hasNext()) {
			int nextBidId = i.next();
			if(nextBidId == bidId) continue;
			TradingPost.getManager().getBid(nextBidId).reject();	
		}
	}
	
	public void withdraw() {
		status = TradeStatus.withdrawn;
		
		// Reject all bids
		Iterator<Integer> i = bids.iterator();
		while(i.hasNext()) {
			TradingPost.getManager().getBid(i.next()).reject();
		}
		
	}

	@SuppressWarnings("unchecked")
	public static SellTrade deserialize(Map<String, Object> serial) {
		// TODO: this is incomplete.
		OfflinePlayer owner = Bukkit.getOfflinePlayer((String)serial.get("owner"));
		
		//Deserialize list of items. Actually this is now done for us by the parser.
		List<ItemStack> items = new ArrayList<ItemStack>();
		/*
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> itemstacks = (ArrayList<Map<String,Object>>)serial.get("items");
		Iterator<Map<String, Object>> i = itemstacks.iterator();
		while(i.hasNext()) {
			items.add(ItemStack.deserialize(i.next()));
		}
		*/
		items = (List<ItemStack>)serial.get("items");
		
		return new SellTrade(owner, items);
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

}
