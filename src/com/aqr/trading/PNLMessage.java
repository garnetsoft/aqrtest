package com.aqr.trading;

public final class PNLMessage extends AbstractMessage 
{
	int signedSize;
	double pnl;
	
	public PNLMessage(MessageType type, long time, String symbol,
			int size, double pnl) {
		super(type, time, symbol);
		
		this.signedSize = size;
		this.pnl = pnl;
	}

	public int getSize() {
		return signedSize;
	}
	
	public void setSize(int size) {
		this.signedSize = size;
	}
	
	public double getPNL() {
		return this.pnl;
	}
	
	public void setPNL(double pnl) {
		this.pnl = pnl;
	}

	@Override
	public String toString() {
		return String.format("%s %d %s %d, %f%n", type, time, symbol, signedSize, pnl);
	}
}
