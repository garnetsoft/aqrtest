package com.aqr.trading;

public abstract class AbstractMessage implements Message 
{
	MessageType type;
	long time;
	String symbol;
	
	AbstractMessage() {}
	
	AbstractMessage(MessageType type, long time, String symbol) {
		this.type = type;
		this.time = time;
		this.symbol = symbol;
	}

	public abstract MessageType getMessageType();
	
	public long getTimestamp() {
		return this.time;
	}

	public String getSymbol() {
		return this.symbol;
	}
	
	@Override
	public int compareTo(Message m) {
		AbstractMessage message = (AbstractMessage) m;

		return (this.time < message.time) ? -1 : (this.time > message.time) ? 1
				: 0;
	}

}
