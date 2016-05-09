package com.aqr.trading;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import com.aqr.trading.Message.MessageType;

public class MessageReader 
{
	BufferedReader reader = null;

	public MessageReader(String inFile) throws Exception {		
		this.reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(
				new FileInputStream(inFile))));
		
	}
	
	public Message readNextMessage(long nextMsg) throws Exception{

		String content = reader.readLine();
		Message m = null;

		if (content != null) {
			//System.out.println(content);

			String[] values = content.split(" ");
			String tp = values[0];
			
			if (tp.equals("F")) {
			long time = Long.parseLong(values[1]);
			String symbol = values[2];
			double price = Double.parseDouble(values[3]);
			int size = Integer.parseInt(values[4]);
			String side = values[5];
			
			if (!"B".equals(side)) {
				size = size * (-1); // for Sell or Short
			}
			
			m = new FillMessage(MessageType.Fill, time, symbol, price, size, FillMessage.Side.valueOf(side));					

			}
			else if (tp.equals("P")) {
				long time = Long.parseLong(values[1]);
				String symbol = values[2];
				double price = Double.parseDouble(values[3]);
				
				m = new PriceUpdateMessage(MessageType.PriceUpdate, time, symbol, price);
			}
		}		
		else {
			// EOF clean up
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
				
		return m;
	}
	
	public static void main(String[] args) {
		try {
			PositionContainer container = new PositionContainer();
			
			MessageReader fillsReader = new MessageReader("data/fills.gz");
			MessageReader priceReader = new MessageReader("data/prices.gz");
			
			PriceUpdateMessage price = (PriceUpdateMessage) priceReader.readNextMessage(0);
			FillMessage fill = (FillMessage) fillsReader.readNextMessage(0);
//			System.out.println("1st price: " + price);
//			System.out.println("1st fill: " + fill);
			
			while (price != null) {
				// read fills up to current priceUpd time				
				while (fill.getTimestamp() <= price.getTimestamp()) {
					container.updateMessage(fill);
					fill = (FillMessage) fillsReader.readNextMessage(price.getTimestamp());
				}
			
				container.updateMessage(price);
				price = (PriceUpdateMessage) priceReader.readNextMessage(0);
			}

			// process the rest of fills after the last price update
			while (fill != null) {
				fill = (FillMessage) fillsReader.readNextMessage(-1);
				container.updateMessage(fill);
			}

		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
