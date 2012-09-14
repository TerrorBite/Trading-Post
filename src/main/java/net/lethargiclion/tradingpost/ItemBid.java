package net.lethargiclion.tradingpost;

import java.util.Collection;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a bid on a sale.
 * @author TerrorBite
 *
 */
public class ItemBid extends GenericBid {
	
	/**
	 * Constructs a new ItemBid.
	 * This particular constructor is designed to be used when the user actually makes a bid.
	 * @param id The id that this bid should be given.
	 * @param owner The user making the bid.
	 * @param items The items that they are bidding.
	 * @param parent The ID of the trade being bid upon.
	 */
	public ItemBid(int id, OfflinePlayer owner, Collection<ItemStack> items, int parent) {
		super(id, owner, items, parent);
	}
	
	/**
	 * Constructs a new ItemBid.
	 * This particular constructor is designed to be used for recreating a Bukkit-serialized ItemBid instance.
	 * @param serialData The serialized data to use.
	 * @throws InstantiationException if deserialization failed.
	 */
	public ItemBid(Map<String, Object> serialData) throws InstantiationException {
		super(serialData);
	}
	
	@Override
	public Map<String, Object> serialize() {
		return super.serialize();
	}
	
}
