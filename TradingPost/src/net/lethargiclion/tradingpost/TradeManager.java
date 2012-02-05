package net.lethargiclion.tradingpost;

/**
 * Singleton class for managing trades.
 * @author TerrorBite
 *
 */
public enum TradeManager {
	INSTANCE;
	
	/**
	 * Holds the parent TradingPost instance.
	 */
	private TradingPost plugin;
	
	/**
	 * Holds the last-used ID value, to enable auto-incrementing ID values.
	 */
	private int currentId;
	
	/**
	 * Plugin accessor function. This ensures that classes like ItemBid that only know
	 * about their TradeManager can access the main plugin instance if they need to.
	 * @return
	 */
	public TradingPost getPlugin() {
		return plugin;
	}
	
	public void setPlugin(TradingPost plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Retrieves the next ID value that should be used for a Trade or Bid.
	 * @return
	 */
	public int getNextId() {
		return currentId++;
	}

}
