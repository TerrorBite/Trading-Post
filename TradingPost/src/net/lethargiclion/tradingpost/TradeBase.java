package net.lethargiclion.tradingpost;

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
	
	public int getId() {
		return id;
	}
	
	public List<ItemStack> getItems() {
		return items;
	}
	
	public OfflinePlayer getOwner() {
		return owner;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}

};
