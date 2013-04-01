package net.lethargiclion.tradingpost;

import java.util.Collection;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

/**
 * Represents a bid against a trade offer.
 * @author TerrorBite
 *
 */
public abstract class GenericBid extends GenericTrade {

	private int parentId;

	GenericBid(int id, String p, Collection<ItemStack> items, int parentId) {
		super(id, p, items);
		this.parentId = parentId;
	}
	
	GenericBid(Map<String, Object> serialData) throws InstantiationException {
		super(serialData);
		if(serialData.containsKey("parent")) {
			try {
				parentId = (Integer)serialData.get("parent");
			} catch(ClassCastException ex) {
				throw new InstantiationException("Cannot deserialize: \"parent\" is not an Integer.");
			}
		} else parentId = 0;
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
	
	@Override
	public void markCompleted() {
		// Alias of markAccepted().
		markAccepted();
	}
	
	public int getParentId() {
		return parentId;
	}

	@Override
	public Map<String, Object> serialize() {
		// Serialize superclass data
		Map<String, Object> serial = super.serialize();
		
		// Add our own data
		serial.put("parent", parentId);
		return serial;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode() + (this.parentId << 6);
	}

}
