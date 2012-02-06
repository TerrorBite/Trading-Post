package net.lethargiclion.tradingpost;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a pending delivery of items to a player.
 * @author TerrorBite
 *
 */
public class PendingItemDelivery implements ConfigurationSerializable {
	
	ItemStack[] items;
	OfflinePlayer target;
	
	public PendingItemDelivery(OfflinePlayer p, ItemStack[] items) {
		target = p;
		this.items = items;
	}
	
	public PendingItemDelivery(OfflinePlayer p, List<ItemStack> items) {
		this(p, items.toArray(new ItemStack[items.size()]));
	}
	
	public PendingItemDelivery(Map<String, Object> serialData) {
		items = (ItemStack[])serialData.get("items");
		target = org.bukkit.Bukkit.getServer().getOfflinePlayer((String)serialData.get("target"));
	}
	
	public OfflinePlayer getTarget() {
		return target;
	}
	
	/**
	 * Attempts to deliver the items to the target player.
	 * If all items were delivered successfully, this method will return true.
	 * This instance should then be removed from any list of pending deliveries.
	 * @return True if all items were successfully delivered, otherwise false.
	 */
	public boolean deliver() {
		// If items is null, then the items have already been delivered successfully.
		if(items == null) return true;
		
		// If our target is offline, we cannot deliver to them.
		if(!target.isOnline()) return false;
		
		// They are online, so attempt delivery.
		Map<Integer, ItemStack> undelivered = target.getPlayer().getInventory().addItem(items);
		if(!undelivered.isEmpty()) {
			// Their inventory is full - save the undelivered items for later.
			target.getPlayer().sendMessage("Some items wouldn't fit in your inventory. They've been saved for later.");
			items = undelivered.values().toArray(new ItemStack[undelivered.values().size()]);
			return false;
		}
		this.items = null;
		return true;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> serial = new LinkedHashMap<String, Object>();
		
		serial.put("target", target.getName());
		// Serialize items
		List<Map<String, Object>> itemstacks = new ArrayList<Map<String,Object>>();
		for(int i=0; i<items.length; i++) {
			ItemStack s = items[i];
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
