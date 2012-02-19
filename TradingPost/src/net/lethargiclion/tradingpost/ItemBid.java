package net.lethargiclion.tradingpost;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a bid on a sale.
 * @author TerrorBite
 *
 */
public class ItemBid extends TradeBase implements Cloneable {
	
	int parentId;
	
	/**
	 * Constructs a new ItemBid.
	 * This particular constructor is designed to be used when the user actually makes a bid.
	 * @param id The id that this bid should be given.
	 * @param owner The user making the bid.
	 * @param items The items that they are bidding.
	 * @param parent The ID of the trade being bid upon.
	 */
	public ItemBid(int id, OfflinePlayer owner, List<ItemStack> items, int parent) {
		this(owner, items, id, new Date(), TradeStatus.open, parent);
	}
	
	/**
	 * Constructs a new ItemBid.
	 * This particular constructor is designed to be used for recreating a Bukkit-serialized ItemBid instance.
	 * It is public so that it can be accessed during unit-testing.
	 * @param owner The user making the bid.
	 * @param items The items that they are bidding.
	 * @param id The ID of this bid.
	 * @param timestamp The time the bid was made.
	 * @param status The current status of this bid.
	 * @param parent The ID of the trade being bid upon.
	 */
	public ItemBid(OfflinePlayer owner, List<ItemStack> items, int id, Date timestamp, TradeStatus status, int parent) {
		this.id = id;
		this.owner = owner;
		this.items = items; // Not a deep copy! What happens if the caller later edits their List?
		this.timestamp = timestamp;
		this.status = status;
		this.parentId = parent;
	}
	
	public int getParentId() {
		return parentId;
	}
	
	public void markAccepted() {
		// Silently ignore attempts to accept a bid that is not open.
		if(status != TradeStatus.open) return;
		status = TradeStatus.accepted;
	}
	
	public void markRejected() {
		// Silently ignore attempts to reject a bid that is not open.
		if(status != TradeStatus.open) return;
		status = TradeStatus.rejected;
	}
	
	/**
	 * Recreates an ItemBid from its serialized Map form.
	 * @param serial The serialized object, using Bukkit's ConfigurationSerializable format.
	 */
	@SuppressWarnings("unchecked")
	public static ItemBid deserialize(Map<String, Object> serial) {
		int id = (Integer) serial.get("id");
		OfflinePlayer owner = org.bukkit.Bukkit.getServer().getOfflinePlayer((String)serial.get("owner"));
		Date timestamp = (Date)serial.get("timestamp");
		TradeStatus status = TradeStatus.valueOf((String)serial.get("status"));
		
		//Deserialize list of items
		// This is now done for us by the parser.
		List<ItemStack> items = new ArrayList<ItemStack>();
		/*
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> itemstacks = (ArrayList<Map<String,Object>>)serial.get("items");
		Iterator<Map<String, Object>> i = itemstacks.iterator();
		while(i.hasNext()) {
			items.add(ItemStack.deserialize(i.next()));
		}*/
		items = (List<ItemStack>)serial.get("items");
		
		int parent = (Integer)serial.get("parent");
		
		return new ItemBid(owner, items, id, timestamp, status, parent);
	}
	
	@Override
	public Map<String, Object> serialize() {
		
		Map<String, Object> serial = new LinkedHashMap<String, Object>();
		
		serial.put("id", id);
		serial.put("owner", owner.getName());
		serial.put("timestamp", timestamp);
		serial.put("status", status.name());
		serial.put("parent", parentId);
		
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
		return super.hashCode() + (this.parentId << 6);
	}
	
}
