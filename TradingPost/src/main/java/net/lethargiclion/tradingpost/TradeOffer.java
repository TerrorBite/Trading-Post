package net.lethargiclion.tradingpost;

import java.util.Collection;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a trade where a user is offering a collection of items, upon
 * which bids can be placed.
 * This class 
 * @author TerrorBite
 *
 */
public class TradeOffer extends GenericOffer {
	
	public TradeOffer(int id, OfflinePlayer p, Collection<ItemStack> items) {
		super(id, p, items);
	}
	
	public TradeOffer(Map<String, Object> serialData) throws InstantiationException {
		super(serialData);
		
	}
	
	@Override
	public Map<String, Object> serialize() {
		return super.serialize();
	}

}
