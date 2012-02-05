package net.lethargiclion.tradingpost;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a bid on a sale.
 * @author TerrorBite
 *
 */
public class ItemBid extends TradeBase {
	
	private Date timestamp; // Time this bid was made
	private ItemBidStatus status; // Current status of this bid
	
	/**
	 * Constructs a new ItemBid.
	 * This particular constructor is designed to be used when the user actually makes a bid.
	 * @param owner The user making the bid.
	 * @param items The items that they are bidding.
	 */
	public ItemBid(OfflinePlayer owner, List<ItemStack> items) {
		this(owner, items, TradingPost.getManager().getNextId(), new Date(), ItemBidStatus.open);
	}
	
	/**
	 * Constructs a new ItemBid.
	 * This particular constructor is designed to be used for recreating a Bukkit-serialized ItemBid instance.
	 * @param owner The user making the bid.
	 * @param items The items that they are bidding.
	 * @param id The ID of this bid.
	 * @param timestamp The time the bid was made.
	 * @param status The current status of this bid.
	 */
	public ItemBid(OfflinePlayer owner, List<ItemStack> items, int id, Date timestamp, ItemBidStatus status) {
		this.id = id;
		this.owner = owner;
		this.items = items; // Not a deep copy! What happens if the caller later edits their List?
		this.timestamp = timestamp;
		this.status = status;
	}
	
	/**
	 * Recreates an ItemBid from its serialized Map form.
	 * @param serial The serialized object, using Bukkit's ConfigurationSerializable format.
	 */
	public static ItemBid deserialize(Map<String, Object> serial) {
		int id = (Integer) serial.get("id");
		OfflinePlayer owner = org.bukkit.Bukkit.getServer().getOfflinePlayer((String)serial.get("owner"));
		Date timestamp = (Date)serial.get("timestamp");
		ItemBidStatus status = ItemBidStatus.valueOf((String)serial.get("status"));
		
		//Deserialize list of items
		List<ItemStack> items = new ArrayList<ItemStack>();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> itemstacks = (ArrayList<Map<String,Object>>)serial.get("items");
		Iterator<Map<String, Object>> i = itemstacks.iterator();
		while(i.hasNext()) {
			items.add(ItemStack.deserialize(i.next()));
		}
		
		return new ItemBid(owner, items, id, timestamp, status);
	}
	
	@Override
	public Map<String, Object> serialize() {
		
		Map<String, Object> serial = new LinkedHashMap<String, Object>();
		
		serial.put("id", id);
		serial.put("owner", owner.getName());
		serial.put("timestamp", timestamp);
		serial.put("status", status.name());
		
		// Serialize items
		List<Map<String, Object>> itemstacks = new ArrayList<Map<String,Object>>();
		Iterator<ItemStack> i = items.iterator();
		while(i.hasNext()) {
			itemstacks.add(i.next().serialize());
		}
		serial.put("items", itemstacks);
		return serial;
	}
	
}
