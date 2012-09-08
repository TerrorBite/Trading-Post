package net.lethargiclion.tradingpost;

public enum TradeStatus {
	/**
	 * This trade offer is in an active state.
	 */
	open,
	
	/**
	 * This trade offer has been successfully completed.
	 */
	completed,
	
	/**
	 * This trade offer has been accepted by the other party.
	 */
	accepted,
	
	/**
	 * This trade offer was rejected by the other party.
	 */
	rejected,
	
	/**
	 * This trade offer was withdrawn by its owner.
	 */
	withdrawn,
	
	/**
	 * The time limit for this trade offer has expired.
	 */
	expired;

}
