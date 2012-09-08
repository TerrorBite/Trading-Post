package net.lethargiclion.tradingpost;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an initial offer for trade, that can have multiple bids placed against it.
 * This class keeps track of the IDs of bids placed against it, and when the owner
 * accepts a bid, the ID of the accepted bid is also stored.
 * @author TerrorBite
 *
 */
public abstract class GenericOffer extends GenericTrade {

	public GenericOffer(Map<String, Object> serialData) throws InstantiationException {
		super(serialData);
		
		// Get Accepted Bid ID for this trade.
		try {
			Integer accepted = (Integer)serialData.get("acceptedBid");
			// If there's no acceptedBid in the serial data, assume -1
			this.acceptedBidId = (accepted == null) ? -1 : accepted;
		} catch(ClassCastException ex) {
			throw new InstantiationException("Cannot deserialize: \"acceptedBid\" is not an Integer.");
		}
		
		// Get list of bids made against this trade.
		Collection<?> bidlist = null;
		try {
			bidlist = (Collection<?>)serialData.get("bids");
		} catch(ClassCastException ex) {
			throw new InstantiationException("Cannot deserialize: \"bids\" is not a Collection.");
		}
		
		// Check type of each element.
		this.bids = new ArrayList<Integer>();
		for(Object i: bidlist) {
			// Ignore any elements that are not Integers.
			if(i instanceof Integer) {
				this.bids.add((Integer)i);
			}
		}
	}
	
	public GenericOffer(int id, OfflinePlayer p, Collection<ItemStack> items) {
		super(id, p, items);
		this.bids = new ArrayList<Integer>();
	}

	Collection<Integer> bids;
	int acceptedBidId = -1;
	
	/**
	 * Gets the list of bids placed against this offer.
	 * @return A read-only view of this object's collection of bid IDs.
	 */
	public Collection<Integer> getBids() {
		return Collections.unmodifiableCollection(bids);
	}
	

	public int bidCount() {
		return this.bids.size();
	}
	
	public boolean hasBid(int bidId) {
		return bids.contains((Integer)bidId);
	}
	
	/**
	 * Adds the given bid's ID to the list of bid IDs on this offer.<br>
	 * Only bids whose parent is set to this bid's ID may be added.
	 * @throws AssertionError if the bid's parent ID is not the ID of this offer.
	 * @param bid The bid whose ID should be added.
	 */
	public void addBid(GenericBid bid) {
		assert(bid.getParentId() == id);
		bids.add(bid.getId());
	}
	
	/**
	 * Gets the ID of the bid that was accepted on this offer, if any.
	 * @return The ID of the bid that was accepted on this offer, or -1 if no bid has been accepted yet.
	 */
	public int getAcceptedBidId() {
		return acceptedBidId;
	}
	
	/**
	 * Mark this offer as completed; i.e. the owner has accepted one of the
	 * bids placed against it and the trade has been completed successfully.
	 * 
	 * Please note: It is up to the TradeManager to mark the bids themselves as
	 * accepted/rejected and to deliver items, etc. This method only marks
	 * this trade as completed.
	 * @param bidId the ID of the bid to accept.
	 * @throws TradeNotFoundException if the bid with this ID was not placed against this offer.
	 */
	public void markAccepted(int bidId) throws TradeNotFoundException {
		if(!bids.contains(bidId)) {
			throw new TradeNotFoundException(String.format("The bid with ID %d was not placed against this offer.", bidId));
		}
		status = TradeStatus.completed;
		// Accept this bid
		this.acceptedBidId = bidId;
	}
	
	@Override
	public Map<String, Object> serialize() {
		// Call superclass serialize() to obtain data fields stored by superclass.
		Map<String, Object> serial = super.serialize();
		
		// Add list of bid IDs.
		serial.put("bids", this.bids);
		
		// Add accepted bid ID only if not -1
		if(this.acceptedBidId >= 0) {
			serial.put("acceptedBid", this.acceptedBidId);
		}
		
		// Return data. Subclasses can continue to add their own custom data.
		return serial;
	}
	
	@Override
	public int hashCode() {
		Iterator<Integer> i = bids.iterator();
		int bidhash = 0;
		int n = bids.size();
		while(i.hasNext()) {
			bidhash += (int)(Math.pow(i.next().intValue()*31, --n));
		}
		return super.hashCode() ^ bidhash;
	}

}
