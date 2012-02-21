package net.lethargiclion.tradingpost.test;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import net.lethargiclion.tradingpost.ItemBid;
import net.lethargiclion.tradingpost.TradeStatus;


public class ItemBidTest {
	
	ItemBid test;
	
	final static int ID = 55;
	final static int PARENT_ID = 1;
	final static TestPlayer PLAYER = new TestPlayer("Steve");
	final static Date TIMESTAMP = generateTestDate();
	final static List<ItemStack> ITEMS = generateTestItems();
	
	// Must be last as it relies on the above initializations
	final static Map<String, Object> SERIAL_DATA = generateTestSerialData();
	
	public ItemBidTest() {
		// Set up for testing.
		if(org.bukkit.Bukkit.getServer() == null) {
			org.bukkit.Server dummyServer = new TestServer();
			org.bukkit.Bukkit.setServer(dummyServer);
		}
	}
	
	/**
	 * Generates reference serial data for testing.
	 * @return
	 */
	public static Map<String, Object> generateTestSerialData() {
		Map<String, Object> serial = new LinkedHashMap<String, Object>();
		
		serial.put("id", ID);
		serial.put("owner", PLAYER.getName());
		serial.put("timestamp", TIMESTAMP);
		serial.put("parent", PARENT_ID);
		serial.put("status", "open");
		
		// In the real world, Bukkit has deserialized this automatically
		serial.put("items", ITEMS);
		
		return serial;
	}
	
	public static Date generateTestDate() {
		try {
			return DateFormat.getDateInstance().parse("2012-02-05T18:03:01.516Z");
		} catch(ParseException ex) {
			return new Date();
		}
	}
	
	public static List<ItemStack> generateTestItems() {
		List<ItemStack> items = new ArrayList<ItemStack>();
		items.add(new ItemStack(Material.OBSIDIAN, 30));
		return items;
	}

	@Before
	public void createTestInstance() {
		/*this.test = new ItemBid(PLAYER, ITEMS,
				ID, TIMESTAMP, TradeStatus.open, PARENT_ID);*/
		this.test = new ItemBid(ID, PLAYER, ITEMS, PARENT_ID);
	}
	
	@Test
	public void serialize() {		
		// Serialize our test object.
		Map<String, Object> serial = test.serialize();
		
		// Test for null object being returned.
		assertNotNull("Serialization data is null!", serial);
		
		// Test for missing keys.
		assertTrue("Serialization data does not contain the key id",
				serial.containsKey("id"));
		
		// Test the value of a key.
		assertEquals("Serializd value of the \"id\" field does not match", ID, serial.get("id"));
		
	}
	
	@Test
	public void deserialize() {
		// Deserialize as a new object.
		ItemBid test2 = null;
		try {
			test2 = new ItemBid(SERIAL_DATA);
		} catch (InstantiationException e) {
			fail("Deserialization failed");
			return;
		}
		
		assertEquals(ID, test2.getId());
		assertEquals(PARENT_ID, test2.getParentId());
		assertEquals(TradeStatus.open, test2.getStatus());
		assertTrue(test2.getItems().contains(ITEMS.get(0)));
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void persist() {
		// Serialize our test object.
		Map<String, Object> serial = test.serialize();
		
		assertTrue(serial.get("items") instanceof List<?>);
		
		// We need to simulate Bukkit's automatic deserializing of objects.
		List<Map<String, Object>> itemdata = (List<Map<String, Object>>) serial.get("items");
		Iterator<Map<String, Object>> i = itemdata.iterator();
		List<ItemStack> newlist = new ArrayList<ItemStack>();
		while(i.hasNext()) {
			newlist.add(ItemStack.deserialize(i.next()));
		}
		serial.put("items", newlist);
		
		// Deserialize as a new object.
		ItemBid test2;
		try {
			test2 = new ItemBid(serial);
		} catch (InstantiationException e) {
			fail("Deserialization failed");
			return;
		}
		
		assertEquals("test and test2 have different hashCodes", test.hashCode(), test2.hashCode());
		
		// Check if they match.
		assertTrue("ItemBid.equals() says the deserialized object doesn't equal the original.",
				test.equals(test2));
	}
	
	@Test
	public void accessors() {
		assertEquals(ID, test.getId());
		assertEquals(PARENT_ID, test.getParentId());
		assertEquals(PLAYER, test.getOwner());
		assertTrue(test.getItems().containsAll(ITEMS));
		assertEquals(TradeStatus.open, test.getStatus());
		assertNotNull(test.getTimestamp());
	}
	
	@Test
	public void acceptBid() {
		test.markAccepted();
		assertEquals(TradeStatus.accepted, test.getStatus());
	}
	
	@Test
	public void rejectBid() {
		test.markRejected();
		assertEquals(TradeStatus.rejected, test.getStatus());
	}
	
	@Test
	public void withdrawBid() {
		test.markWithdrawn();
		assertEquals(TradeStatus.withdrawn, test.getStatus());
	}
	
	@Test
	public void expireBid() {
		test.markExpired();
		assertEquals(TradeStatus.expired, test.getStatus());
	}
	
	@Test
	public void completeBid() {
		test.markCompleted();
		// This should alias to accepted status
		assertEquals(TradeStatus.accepted, test.getStatus());
	}
	
	@Test
	public void statusAlreadySet() {
		test.markWithdrawn();
		// Second call should have no effect - once marked it
		// should not be possible to change its status again
		test.markAccepted();
		assertEquals(TradeStatus.withdrawn, test.getStatus());
		test.markRejected();
		assertEquals(TradeStatus.withdrawn, test.getStatus());
		test.markExpired();
		assertEquals(TradeStatus.withdrawn, test.getStatus());
	}
	
	@Test
	public void statusAlreadySet2() {
		test.markAccepted();
		// Second call should have no effect - once marked it
		// should not be possible to change its status again
		test.markWithdrawn();
		assertEquals(TradeStatus.accepted, test.getStatus());
		test.markRejected();
		assertEquals(TradeStatus.accepted, test.getStatus());
		test.markExpired();
		assertEquals(TradeStatus.accepted, test.getStatus());
	}
	
	@Test
	public void checkTimeString() {
		try {
			synchronized(this) {
				this.wait(2000);
			}
		} catch (InterruptedException e) {
			return;
		}
		
		assertEquals("2 seconds ago", test.getTimeString());
	}
}
