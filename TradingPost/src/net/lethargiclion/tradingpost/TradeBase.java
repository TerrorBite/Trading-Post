package net.lethargiclion.tradingpost;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

/**
 * Base class for all trades and bids.
 * @author TerrorBite
 *
 */
public abstract class TradeBase implements ConfigurationSerializable {

	protected int id;
	protected List<ItemStack> items;
	protected OfflinePlayer owner;
	protected Date timestamp;
	protected TradeStatus status;
	
	public int getId() {
		return id;
	}
	
	public List<ItemStack> getItems() {
		// Return a read-only version of our items.
		return Collections.unmodifiableList(items);
	}
	
	public OfflinePlayer getOwner() {
		return owner;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	public TradeStatus getStatus() {
		return status;
	}

};
