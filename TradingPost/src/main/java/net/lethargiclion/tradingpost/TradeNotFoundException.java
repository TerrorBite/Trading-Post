package net.lethargiclion.tradingpost;

public class TradeNotFoundException extends Exception {

	/**
	 * Serial version
	 */
	private static final long serialVersionUID = 1L;
	
	public TradeNotFoundException(String reason) {
		super(reason);
	}

}
