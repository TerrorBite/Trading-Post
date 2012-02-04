package net.lethargiclion.tradingpost;

public class TradeManager {
	
	/**
	 * Holds a reference to the parent plugin instance.
	 */
	private TradingPost plugin;
	
	/**
	 * Holds the TradeManager singleton.
	 */
	private static TradeManager currentMgr = null;
	
	/**
	 * Holds the last-used ID value, to enable auto-incrementing ID values.
	 */
	private int currentId;
	
	/**
	 * Constructs a new TradeManager instance. Please note that this is a singleton class.
	 * @param plugin The parent TradingPost plugin instance.
	 * @throws InstantiationException if an instance of TradeManager already exists.
	 */
	public TradeManager(TradingPost plugin) throws InstantiationException {
		this.plugin = plugin;
		if(TradeManager.currentMgr != null) {
			// Enforce being a singleton.
			throw new java.lang.InstantiationException("Only one instance of TradeManager may exist at a time.");
		}
		TradeManager.currentMgr = this;
	}

	@Override
	protected void finalize() throws Throwable {
		// This is mostly just here for sanity.
		TradeManager.currentMgr = null;
		super.finalize();
	}
	
	/**
	 * Plugin accessor function. This ensures that classes like ItemBid that only know
	 * about their TradeManager can access the main plugin instance if they need to.
	 * @return
	 */
	public TradingPost getPlugin() {
		return plugin;
	}
	
	/**
	 * Retrieves the next ID value that should be used for a Trade or Bid.
	 * @return
	 */
	public int getNextId() {
		return currentId++;
	}
	
	/**
	 * Returns the current instance of the {@link TradeManager}.
	 * This ensures that classes like {@link ItemBid} can always access the current TradeManager, since when they
	 * are being deserialized by Bukkit they cannot be passed a reference to it via their constructor.
	 * @return The current TradeManager instance, or {@code null} if there isn't one.
	 */
	public static synchronized TradeManager getManager() {
		// Synchronized for thread safety - not that we even use threads, but you can never be too sure
		return TradeManager.currentMgr;
	}

}
