package com.aqr.trading;

public interface Message extends Comparable<Message> 
{
	public enum MessageType { Unknown, Fill, PriceUpdate, PNL }

	public MessageType getMessageType();

	public long getTimestamp();
	
	public String getSymbol();
	
}
