package net.lethargiclion.tradingpost.test;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.lethargiclion.tradingpost.GenericBid;
import net.lethargiclion.tradingpost.ItemBid;
import net.lethargiclion.tradingpost.TradeNotFoundException;
import net.lethargiclion.tradingpost.TradeOffer;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;

public class TradeOfferTest {
	
	TradeOffer test;
	
	final static int ID = 42;
	final static int ACCEPTED_BID_ID = 55;
	final static TestPlayer PLAYER = new TestPlayer("Bob");
	final static Date TIMESTAMP = generateTestDate();
	final static List<ItemStack> ITEMS = generateTestItems();
	final static Collection<Integer> BIDS = generateTestBidIDs();
	
	// Must be last as it relies on the above initializations
	final static Map<String, Object> SERIAL_DATA = generateTestSerialData();
	
	private static Map<String, Object> generateTestSerialData() {
		Map<String, Object> serial = new LinkedHashMap<String, Object>();
		
		serial.put("id", ID);
		serial.put("owner", PLAYER.getName());
		serial.put("timestamp", TIMESTAMP);
		serial.put("acceptedBid", ACCEPTED_BID_ID);
		serial.put("status", "open");
		
		// In the real world, Bukkit has deserialized this automatically
		serial.put("items", ITEMS);
		
		serial.put("bids", BIDS);
		
		return serial;
	}
	
	private static Collection<Integer> generateTestBidIDs() {
		Collection<Integer> bids = new ArrayList<Integer>();
		bids.add(22); bids.add(33); bids.add(55);
		return bids;
	}

	private static Date generateTestDate() {
		try {
			return DateFormat.getDateInstance().parse("2012-02-05T18:03:01.516Z");
		} catch(ParseException ex) {
			return new Date();
		}
	}
	
	public static List<ItemStack> generateTestItems() {
		List<ItemStack> items = new ArrayList<ItemStack>();
		items.add(new ItemStack(Material.GLOWSTONE_DUST, 48));
		items.add(new ItemStack(Material.DIAMOND, 3));
		return items;
	}

	@Before
	public void setUp() throws Exception {
		test = new TradeOffer(ID, PLAYER, ITEMS);
	}
	
	@Test
	public void bidHandling() throws TradeNotFoundException {
		GenericBid testBid = new ItemBid(ItemBidTest.ID, ItemBidTest.PLAYER, ItemBidTest.ITEMS, ID);
		
		// Check that our testBid is suitable for testing
		assertEquals("Test bid's ID does not match the ID we gave it!", ItemBidTest.ID, testBid.getId());
		
		// Bids should initially be empty
		assertTrue("List of bids should be empty", test.getBids().isEmpty());
		
		// Add our test bid
		test.addBid(testBid);
		
		// Bids should contain the bid ID we just added
		assertFalse("List of bids should no longer be empty", test.getBids().isEmpty());
		assertTrue("List of bids should contain test bid ID", test.getBids().contains(ItemBidTest.ID));
		
		// Mark test bid as the accepted bid
		test.markAccepted(ItemBidTest.ID);
		assertEquals("Accepted bid ID should match our test bid!",  ItemBidTest.ID, test.getAcceptedBidId());
	}
	
	@Test(expected=TradeNotFoundException.class)
	public void bidException() throws TradeNotFoundException {
		GenericBid testBid = new ItemBid(ItemBidTest.ID, ItemBidTest.PLAYER, ItemBidTest.ITEMS, ID);
		
		test.addBid(testBid);
		
		assertFalse("Bids list should not contain our test value", test.getBids().contains(123));
		
		// This should throw a TradeNotFound exception
		test.markAccepted(123);
	}

}
