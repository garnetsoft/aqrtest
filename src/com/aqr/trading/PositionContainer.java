package com.aqr.trading;

import java.util.Map;
import java.util.HashMap;
import com.aqr.trading.Message.MessageType;
import com.aqr.trading.FillMessage.Side;

public class PositionContainer 
{
	private final Map<String, PNLMessage> pnlSymbolMap;  // latest PNL map by symbol
	private final Map<String, FillMessage> buyPositions; 
	private final Map<String, FillMessage> sellPositions;
			
	public PositionContainer() {
		this.pnlSymbolMap = new HashMap<String, PNLMessage>();

		this.buyPositions = new HashMap<String, FillMessage>();
		this.sellPositions = new HashMap<String, FillMessage>();		
	}

	public void updateMessage(Message m) {
		if (m == null) {
			return;
		}
		
		MessageType type = m.getMessageType();
		
		switch (type) {
			case Fill:
				// update positions for each fill				
				FillMessage fill = (FillMessage) m;				
				updatePosition(fill);
				break;
				
			case PriceUpdate:
				PriceUpdateMessage priceUpdate = (PriceUpdateMessage) m;
				computePNL(priceUpdate);				
				break;
			
			default: 
				System.err.println("Unkown message: " + m);
				break;
				
		}	
	}
	
	private void updatePosition(FillMessage fill) {
		// update existing position map (buyPositions or sellPositions) with new Fill
		Map<String, FillMessage> posMap = fill.getSide().equals(Side.B) ? buyPositions : sellPositions;		
		FillMessage curPos = posMap.get(fill.getSymbol());
		
		if (curPos != null) {
			double vwap = (curPos.getPrice() * curPos.getSize() + fill.getPrice() * fill.getSize()) / (curPos.getSize() + fill.getSize());

			// note price is value weighted 
			fill.setSize(curPos.getSize() + fill.getSize());
			fill.setPrice(vwap);
			
			posMap.put(fill.getSymbol(), fill);
		}
		else {
			// init position
			posMap.put(fill.getSymbol(), fill);
		}
	}
		
	/*
	 * https://www.tradingtechnologies.com/help/fix-adapter-reference/pl-calculation-algorithm/understanding-pl-calculations/
	 */
	private void computePNL(PriceUpdateMessage priceUpdate) {
		// aggregate buy and sell orders and calculate the latest position
		FillMessage buyPos = buyPositions.get(priceUpdate.getSymbol());
		FillMessage sellPos = sellPositions.get(priceUpdate.getSymbol());
		
		int buySize = 0, sellSize = 0;
		double buyPrice = 0.0, sellPrice = 0.0;

		if (null != buyPos) {
			buySize = buyPos.getSize();
			buyPrice = buyPos.getPrice();
		}
		
		if (null != sellPos) {
			sellSize = sellPos.getSize();
			sellPrice = sellPos.getPrice();
		}

		int posSize = buySize + sellSize; // net position		
		double pnlValue = 0.0; // realized
		double markToMktVal = 0.0;

		// realized pnl
		if (buySize > 0 && sellSize < 0) {
			if (posSize >= 0) {
				// net long
				pnlValue = (posSize - buySize) * buyPrice - sellSize * sellPrice;
			}
			else {
				// net short
				pnlValue = (posSize - sellSize) * sellPrice - buySize * buyPrice;				
			}
		}
		
		double openPrice = posSize >=0 ? buyPrice : sellPrice;
		markToMktVal = (priceUpdate.getPrice() - openPrice) * posSize;
		double totalPNL = pnlValue + markToMktVal;
		
		PNLMessage pnlMsg = new PNLMessage(MessageType.PNL
				, priceUpdate.getTimestamp()
				, priceUpdate.getSymbol()
				, posSize
				, totalPNL);
		
		this.pnlSymbolMap.put(pnlMsg.getSymbol(), pnlMsg);

		// output
		System.out.println(pnlMsg);
	}
	
	public Map<String, PNLMessage> getPNLMessages() {
		return pnlSymbolMap;
	}
	
	public PNLMessage getPNLBySymbol(String symbol) throws Exception {
		PNLMessage pnlMsg = getPNLMessages().get(symbol);
		
		if (pnlMsg != null)
			return pnlMsg;
		
		throw new Exception("PNL not found for " + symbol);
	}
}
