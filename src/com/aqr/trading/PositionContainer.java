package com.aqr.trading;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.aqr.trading.Message.MessageType;

public class PositionContainer 
{
	private final Map<String, PNLMessage> pnlSymbolMap;  // latest PNL map by symbol

	private final Map<String, FillMessage> buyPositions; 
	private final Map<String, FillMessage> sellPositions;
			
	public PositionContainer() {
		this.pnlSymbolMap = new HashMap<String, PNLMessage>();

		this.buyPositions = new ConcurrentHashMap<String, FillMessage>();
		this.sellPositions = new ConcurrentHashMap<String, FillMessage>();		
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
				String side = fill.getSide();
				
				if ("B".equals(side)) {
					updateBuyPosition(fill);
				}
				else {
					updateSellPosition(fill);
				}
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
	
	private void updateBuyPosition(FillMessage fill) {
		// update existing buy position with new Fill
		
		if (buyPositions.containsKey(fill.getSymbol())) {
			
			FillMessage buyPos = buyPositions.get(fill.getSymbol());
			
			// check for null
			if (null != buyPos) {
				FillMessage fClone = fill.clone();
				
				int newSize = buyPos.getSize() + fill.getSize();
				double vwap = (buyPos.getPrice() * buyPos.getSize() + fill.getPrice() * fill.getSize()) / (newSize);

				// note price is value weighted 
				fClone.setSize(newSize);
				fClone.setPrice(vwap);
				
				buyPositions.put(fill.getSymbol(), fClone);
			}
		}
		else {
			// new buy pos
			buyPositions.put(fill.getSymbol(), fill);
		}
	}
	
	private void updateSellPosition(FillMessage fill) {
		// update existing sell position with new Fill
		
		if (sellPositions.containsKey(fill.getSymbol())) {			
			FillMessage buyPos = sellPositions.get(fill.getSymbol());
			
			// check for null
			if (null != buyPos) {
				FillMessage fClone = fill.clone();
				
				int newSize = buyPos.getSize() + fill.getSize();
				double vwap = (buyPos.getPrice() * buyPos.getSize() + fill.getPrice() * fill.getSize()) / (newSize);
				
				// note price is value weighted 
				fClone.setSize(newSize);
				fClone.setPrice(vwap);
				
				sellPositions.put(fill.getSymbol(), fClone);
			}
		}
		else {
			// new sell pos
			sellPositions.put(fill.getSymbol(), fill);
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
		
		if (buySize > 0 && sellSize < 0) {
			// realized pnl
			if (posSize >= 0) {
				// net long
				pnlValue = (posSize - buySize) * buyPrice - sellSize * sellPrice;
			}
			else {
				// net short
				pnlValue = (posSize - sellSize) * sellPrice - buySize * buyPrice;				
			}
		}
		
		if (posSize >= 0) {
			// long position
			markToMktVal = (priceUpdate.getPrice() - buyPrice) * posSize;
		}
		else {
			// short position
			markToMktVal = (priceUpdate.getPrice() - sellPrice) * posSize;
		}
		
		double totalPNL = pnlValue + markToMktVal;
		
		PNLMessage pnlMsg = new PNLMessage(MessageType.PNL
				, priceUpdate.getTimestamp()
				, priceUpdate.getSymbol()
				, posSize
				, totalPNL);
		
		synchronized(this.pnlSymbolMap) {
			this.pnlSymbolMap.put(pnlMsg.getSymbol(), pnlMsg);
		}
		
		System.out.println(pnlMsg);
	}
	
	public Map<String, PNLMessage> getPNLMessages() {
		Map<String, PNLMessage> pnlCopyMap = Collections.emptyMap();
		
		synchronized(this.pnlSymbolMap) {
			pnlCopyMap = new HashMap<String, PNLMessage>(this.pnlSymbolMap);
		}
		
		return pnlCopyMap;
	}
	
	public PNLMessage getPNLBySymbol(String symbol) throws Exception {
		PNLMessage pnlMsg = getPNLMessages().get(symbol);
		
		if (pnlMsg != null)
			return pnlMsg;
		
		throw new Exception("PNL not found for " + symbol);
	}
}
