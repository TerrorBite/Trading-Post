package net.lethargiclion.tradingpost;

public class CommandNotFoundException extends java.lang.Exception {

	String reason;
	
	/**
	 * Constructs a new CommandNotFoundException.
	 * @param reason The reason for this exception.
	 */
	public CommandNotFoundException(String reason) {
		super(reason);
	}
	
	@Override
	public String getMessage() {
		return reason;
	}
	/**
	 * Default serial thing
	 */
	private static final long serialVersionUID = 1L;
	
}