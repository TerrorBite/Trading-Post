package net.lethargiclion.tradingpost.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.bukkit.inventory.ItemStack;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import net.lethargiclion.tradingpost.ItemBid;
import net.lethargiclion.tradingpost.TradeStatus;


public class ItemBidTest {
	
	ItemBid test;
	
	final static int ID = 55;
	final static TestPlayer PLAYER = new TestPlayer("Steve");

	@Before
	public void createTestInstance() {
		this.test = new ItemBid(PLAYER,
				new ArrayList<ItemStack>(),
				ID, new Date(), TradeStatus.open);
	}
	
	@Test
	public void serialization() {
		Map<String, Object> serial = test.serialize();
		
		assertNotNull("Serialization data is null!", serial);
		
		assertTrue("Serialization data does not contain the key id",
				serial.containsKey("id"));
		assertEquals(serial.get("id"), 55);
	}
	
	@Test
	public void bidId() {
		assertEquals(test.getId(), ID);
	}
	
	@Test
	public void acceptance() {
		test.accept(PLAYER);
		assertEquals(test.getStatus(), TradeStatus.accepted);
	}
	
}
