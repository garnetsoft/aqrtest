package com.aqr.trading;

public final class FillMessage extends AbstractMessage implements Cloneable
{
	public enum Side { B, S, SHORT }
	
	double price;
	int size;
	String side;

	FillMessage() { }
	
	FillMessage(MessageType type, long time, String symbol, double price, int size,
			String side) {
		super(type, time, symbol);
		
		this.price = price;
		this.size = size;
		this.side = side;
	}

	@Override
	public MessageType getMessageType() {
		return MessageType.Fill;
	}
	
	public double getPrice() {
		return price;
	}

	public void setPrice(double p) {
		this.price = p;
	}
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int s) {
		this.size = s;
	}
	
	public String getSide() {
		return side;
	}
	
	public FillMessage clone() {
		FillMessage fill = new FillMessage();
		
		fill.type = this.type;
		fill.time = this.time;
		fill.symbol = this.symbol;
		fill.price = this.price;
		fill.size = this.size;
		fill.side = this.side;
		
		return fill;
	}
	
	@Override
	public String toString() {
		return String.format("%s %d %s %f %d %s%n", type, time, symbol, price, size, side);
	}
}
