package com.aqr.trading;

public final class PriceUpdateMessage extends AbstractMessage 
{
	final double price;

	public PriceUpdateMessage(MessageType type, long time, String symbol,
			double price) {
		super(type, time, symbol);
		this.price = price;
	}

	public double getPrice() {
		return this.price;
	}
	
	@Override
	public String toString() {
		return String.format("%s %d %s %f%n", type, time, symbol, price);
	}
}
