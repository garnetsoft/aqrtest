package com.aqr.trading;

public final class FillMessage extends AbstractMessage implements Cloneable
{
	public enum Side { B, S, SHORT }
	
	double price;
	int size;
	Side side;

	FillMessage() { }
	
	public FillMessage(MessageType type, long time, String symbol, double price, int size,
			Side side) {
		super(type, time, symbol);
		
		this.price = price;
		this.size = size;
		this.side = side;
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
	
	public Side getSide() {
		return side;
	}
	
	@Override
	public String toString() {
		return String.format("%s %d %s %f %d %s%n", type, time, symbol, price, size, side);
	}
}
