package net.lethargiclion.tradingpost;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
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
	
	public void markWithdrawn() {
		if(status == TradeStatus.open) {
			status = TradeStatus.withdrawn;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		// Reference equals?
		if(obj == this) return true;
		
		// Different objects, but are the contents the same?
		if(obj instanceof ItemBid) {
			ItemBid eq = (ItemBid)obj;
			if(eq.hashCode() == this.hashCode() &&
					eq.id == this.id) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = this.timestamp.hashCode();
		hash ^= this.owner.getName().hashCode();
		hash ^= this.status.hashCode() + (this.id << 3);
		Iterator<ItemStack> i = this.items.iterator();
		while(i.hasNext()) {
			hash ^= i.next().hashCode();
		}
		
		return hash;
	}

};
