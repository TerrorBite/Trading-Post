package net.lethargiclion.tradingpost;

import java.util.ArrayList;
import java.util.Collection;
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
public class QueuedItemDelivery implements ConfigurationSerializable {
	
	/**
	 * This enum provices a range of values which describe the result of an attempt to deliver items to a player.
	 * @author TerrorBite
	 *
	 */
	public enum DeliveryResult {
		/**
		 * No items could be delivered because the player is not online.
		 */
		PLAYER_OFFLINE,
		/**
		 * Items could not be delivered because the user ran out of inventory space.
		 */
		NOT_ENOUGH_SPACE,
		/**
		 * All items were delivered successfully.
		 */
		SUCCESS,
		/**
		 * There are no items to be delivered.
		 */
		NO_ITEMS
	}
	
	/**
	 * Enum for the reason for receiving items.
	 * @author trainmeditations
	 *
	 */
	public enum DeliveryReason {
		/**
		 * Trade Accepted
		 */
		TRADE_ACCEPTED("You have received items from a successful trade. ID: %d"),
		/**
		 * Trade Reject
		 */
		TRADE_REJECTED("Trade %d was rejected. Your items have been returned."),
		/**
		 * Trade Withdrawn
		 */
		TRADE_WITHDRAWN("You withdrew trade %d. Your items have been returned."),
		/**
		 * Other
		 */
		OTHER("Items delivered for trade %d with no reason.");
		
		private String reason;
		DeliveryReason(String reason){
			this.reason = reason;
		}
		public String getMessage(int tradeId) {
			return String.format(reason, tradeId);
		}
	}
	
	ItemStack[] items;
	OfflinePlayer target;
	
	public QueuedItemDelivery(OfflinePlayer p, ItemStack[] items) {
		target = p;
		this.items = items;
	}
	
	public QueuedItemDelivery(OfflinePlayer p, Collection<ItemStack> items) {
		this(p, items.toArray(new ItemStack[items.size()]));
	}
	
	@SuppressWarnings("unchecked")
	public QueuedItemDelivery(Map<String, Object> serialData) {
		List<ItemStack> newItems = (List<ItemStack>)serialData.get("items");
		items = newItems.toArray(new ItemStack[newItems.size()]);
		target = org.bukkit.Bukkit.getServer().getOfflinePlayer((String)serialData.get("target"));
	}
	
	public OfflinePlayer getTarget() {
		return target;
	}
	
	/**
	 * Attempts to deliver the items to the target {@link OfflinePlayer}.<br>
	 * If all items were delivered successfully, this method will return {@link DeliveryResult.SUCCESS}.
	 * This instance should then be removed from any list of pending deliveries of which it is a member.
	 * @return A {@link DeliveryResult} that describes the result of the delivery attempt.
	 */
	public DeliveryResult deliver() {
		// If items is null, then the items have already been delivered successfully.
		if(items == null) return DeliveryResult.NO_ITEMS;
		
		// If our target is offline, we cannot deliver to them.
		if(!target.isOnline()) return DeliveryResult.PLAYER_OFFLINE;
		
		// They are online, so attempt delivery.
		Map<Integer, ItemStack> undelivered = target.getPlayer().getInventory().addItem(items);
		if(!undelivered.isEmpty()) {
			// Their inventory is full - save the undelivered items for later.
			items = undelivered.values().toArray(new ItemStack[undelivered.values().size()]);
			return DeliveryResult.NOT_ENOUGH_SPACE;
		}
		this.items = null;
		return DeliveryResult.SUCCESS;
	}

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
