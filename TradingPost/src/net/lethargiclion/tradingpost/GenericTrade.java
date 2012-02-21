package net.lethargiclion.tradingpost;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;

/**
 * Represents one side of a trade (an offer or counter-offer) that has some
 * quantity of items associated with it.
 * @author TerrorBite
 *
 */
public abstract class GenericTrade implements ConfigurationSerializable {

	protected int id;
	protected Collection<ItemStack> items;
	protected OfflinePlayer owner;
	protected Date timestamp;
	protected TradeStatus status;
	
	/**
	 * Creates a new trade with the given ID, owner and items.
	 * The timestamp of this trade is set to the current time,
	 * and the status is set to {@code TradeStatus.OPEN}.
	 * @param id The ID to assign to this trade.
	 * @param p The owner of this trade.
	 * @param items The items contained by this trade.
	 */
	GenericTrade(int id, OfflinePlayer p, Collection<ItemStack> items) {
		this.id = id;
		this.owner = p;
		this.items = new ArrayList<ItemStack>();
		this.status = TradeStatus.open;
		this.timestamp = new Date();
		for(ItemStack i: items) {
			// Clone the items instead of storing references.
			// Not as memory-efficient but ensures that our copy won't be changed later.
			this.items.add(i.clone());
		}
	}
	
	/**
	 * Constructor to deserialize this object. Bukkit will call this
	 * constructor while deserializing the object.
	 * @param serialData The serialized data to create the object from.
	 * @throws InstantiationException if deserialization failed.
	 */
	GenericTrade(Map<String, Object> serialData) throws InstantiationException {
		// Get owner of this trade.
		try {
			this.owner = Bukkit.getOfflinePlayer((String)serialData.get("owner"));
		} catch(ClassCastException ex) {
			throw new InstantiationException("Cannot deserialize: \"owner\" is not a String.");
		}
		
		// Get ID number of this trade.
		try {
			this.id = (Integer)serialData.get("id");
		} catch(ClassCastException ex) {
			throw new InstantiationException("Cannot deserialize: \"id\" is not an Integer.");
		}
		
		// Get status of this trade.
		try {
			this.status = TradeStatus.valueOf((String)serialData.get("status"));
		} catch(ClassCastException ex) {
			throw new InstantiationException("Cannot deserialize: \"status\" is not a String.");
		}
		
		// Get timestamp of this trade.
		try {
			this.timestamp = (Date)serialData.get("timestamp");
		} catch(ClassCastException ex) {
			throw new InstantiationException("Cannot deserialize: \"timestamp\" is not a Date.");
		}
		
		/* Get list of items held by this trade.
		 * Bukkit's parser deserializes the itemlist for us, but we need to
		 * check if we are being given a valid collection of ItemStacks.
		 */
		Collection<?> itemlist = null;
		try {
			itemlist = (Collection<?>)serialData.get("items");
		} catch(ClassCastException ex) {
			throw new InstantiationException("Cannot deserialize: \"items\" is not a Collection.");
		}
		
		// Check type of each element.
		this.items = new ArrayList<ItemStack>();
		for(Object i: itemlist) {
			// Ignore any elements that are not ItemStacks.
			if(i instanceof ItemStack) {
				this.items.add((ItemStack)i);
			}
		}
	}
	
	// Returns the ID that this trade has been assigned.
	public int getId() {
		return id;
	}
	
	/**
	 * Returns a copy of the collection of item that this trade is holding.
	 * Modifying the collection returned by this accessor will not modify the
	 * collection maintained by this trade object.
	 * @return A copy of the items contained in this trade.
	 */
	public Collection<ItemStack> getItems() {
		Collection<ItemStack> itemcopy = new ArrayList<ItemStack>();
		for(ItemStack i: items) {
			itemcopy.add(i.clone());
		}
		// Return a deep copy of our item collection.
		return itemcopy;
	}
	
	/**
	 * Gets the OfflinePlayer who owns this trade.
	 * @return
	 */
	public OfflinePlayer getOwner() {
		return owner;
	}
	
	/**
	 * Gets the time this trade was created.
	 * @return
	 */
	public Date getTimestamp() {
		return timestamp;
	}
	
	private enum DateValue {
		YEAR(Calendar.YEAR, "year"),
		WEEK(Calendar.WEEK_OF_YEAR, "week"),
		DAY(Calendar.DAY_OF_MONTH, "day"),
		HOUR(Calendar.HOUR, "hour"),
		MINUTE(Calendar.MINUTE, "minute"),
		SECOND(Calendar.SECOND, "second");
		
		private int field;
		private String text;
		DateValue(int field, String text) {
			this.field = field;
			this.text = text;
		}
		
		public int getField() {
			return field;
		}
		
		public String getText(int value) {
			return String.format("%s%s", this.text, value==1?"":"s");
		}
	}
	
	public String getTimeString() {
		
		List<String> strings = new ArrayList<String>();
		
		Calendar ref = new GregorianCalendar();
		ref.setTimeInMillis(0);
		
		Calendar time = new GregorianCalendar();
		long millisecs = new Date().getTime() - timestamp.getTime();
		time.setTimeInMillis(millisecs);
		
		for(DateValue dv: DateValue.values()) {
			int field = dv.getField();
			if(ref.get(field) != time.get(field)) {
				int value = time.get(field) - ref.get(field);
				strings.add(String.format("%d %s",
						value,
						dv.getText(value)
				));
			}
		}
		
		if(strings.size() == 0) {
			strings.add("0 seconds");
		}
		
		StringBuilder b = new StringBuilder();
		Iterator<String> i = strings.iterator();
		while(i.hasNext()) {
			b.append(i.next());
			if(i.hasNext()) b.append(", ");
		}
		b.append(" ago");
		
		return b.toString();
		
	}
	
	/**
	 * Gets the status of this trade.
	 * @return A TradeStatus representing the status of this trade.
	 */
	public TradeStatus getStatus() {
		return status;
	}
	
	/**
	 * Marks this trade as withdrawn, meaning the owner of this trade
	 * has withdrawn their offer/bid and that all items have been returned.
	 */
	public void markWithdrawn() {
		if(status == TradeStatus.open) {
			status = TradeStatus.withdrawn;
		}
	}
	
	/**
	 * Marks this trade as expired, meaning that the trade has remained unaccepted
	 * for too long and is no longer open, and that all items have been returned
	 * to their owners.
	 */
	public void markExpired() {
		if(status == TradeStatus.open) {
			status = TradeStatus.expired;
		}
	}
	
	/** Marks this trade as completed, meaning that the trade has been
	 * concluded successfully and all items have been delivered to their
	 * new owners.
	 */
	public void markCompleted() {
		if(status == TradeStatus.open) {
			status = TradeStatus.completed;
		}
	}
	
	/**
	 * Returns a Map containing all the data required for Bukkit to
	 * later reconstruct this object from a config file.<br>
	 * <br>
	 * <b>Note:</b> Subclasses <em>must</em> override this method. They should
	 * first call {@code super.serialize()} to obtain a {@code Map}
	 * pre-populated with serialized data from their superclass.
	 * The subclass should then add their own custom fields using {@code Map.put()}
	 * before returning the updated {@code Map}.
	 */
	@Override
	public Map<String, Object> serialize() {
		// Create a new LinkedHashMap to store serialized data.
		// LinkedHashMap maintains the order of its entries.
		Map<String, Object> serial = new LinkedHashMap<String, Object>();
		
		// Store this trade's ID number
		serial.put("id", this.id);
		
		// Store username of this trade's owner
		serial.put("owner",	this.owner.getName());

		// Store trade status
		serial.put("status", this.status.name());
		
		// Store timestamp
		serial.put("timestamp", this.timestamp);
		
		// Store the items held by this trade.
		List<Map<String, Object>> itemstacks = new ArrayList<Map<String,Object>>();
		Iterator<ItemStack> i = items.iterator();
		while(i.hasNext()) {
			ItemStack s = i.next();
			Map<String, Object> stack = new LinkedHashMap<String, Object>();
			
			// Mark this as a serialized ItemStack class so that Bukkit will
			// automatically deserialize it for us later
			stack.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY,
					ItemStack.class.getName());
			
			// Call the ItemStack's own serialize() and append the result to the list
			stack.putAll(s.serialize());
			itemstacks.add(stack);
		}
		// Add the list to the map
		serial.put("items", itemstacks);
		
		// Return serialized data. Subclasses can continue to add their own data
		return serial;
	}
	
	@Override
	public boolean equals(Object obj) {
		// Reference equals?
		if(obj == this) return true;
		
		// Different objects, but are the contents the same?
		if(obj instanceof GenericTrade) {
			GenericTrade eq = (GenericTrade)obj;
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
