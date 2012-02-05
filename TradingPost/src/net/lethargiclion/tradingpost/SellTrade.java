package net.lethargiclion.tradingpost;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a trade where a user is offering items in return for bids.
 * @author TerrorBite
 *
 */
public class SellTrade implements ConfigurationSerializable {
	
	List<ItemBid> bids = new ArrayList<ItemBid>();
	List<ItemStack> items = new ArrayList<ItemStack>();
	OfflinePlayer owner;
	TradeStatus status;
	int AcceptedBidID;
	
	public SellTrade(OfflinePlayer p, ItemStack[] items) {
		
	}

	@Override
	public Map<String, Object> serialize() {
		// TODO Auto-generated method stub
		return null;
	}

}
