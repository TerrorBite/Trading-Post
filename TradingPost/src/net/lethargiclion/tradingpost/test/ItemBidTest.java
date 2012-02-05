package net.lethargiclion.tradingpost.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.bukkit.inventory.ItemStack;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.lethargiclion.tradingpost.ItemBid;
import net.lethargiclion.tradingpost.ItemBidStatus;


public class ItemBidTest {
	
	ItemBid test;

	@Before
	public void createTestInstance() {
		this.test = new ItemBid(new TestPlayer("Steve"),
				new ArrayList<ItemStack>(),
				55, new Date(), ItemBidStatus.open);
	}
	
	@Test
	public void serialize() {
		Map<String, Object> serial = test.serialize();
		
		Assert.assertNotNull("Serialization data is null!", serial);
		
		Assert.assertTrue("Serialization data does not contain the key id",
				serial.containsKey("id"));
		Assert.assertEquals(serial.get("id"), 55);
	}
	
}
