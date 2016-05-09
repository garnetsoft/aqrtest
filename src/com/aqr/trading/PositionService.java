package com.aqr.trading;

public class PositionService 
{
	public static void main(String[] args) {
		if (args.length == 0) {

			try {
				PositionContainer container = new PositionContainer();

				// initialize message reader
				MessageReader priceReader = new MessageReader(args[0]);
				MessageReader fillsReader = new MessageReader(args[1]);
				
				PriceUpdateMessage price = (PriceUpdateMessage) priceReader.readNextMessage(0);
				FillMessage fill = (FillMessage) fillsReader.readNextMessage(0);
				
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
		else {
			usage();
		}
	}
	
	public static void usage() {
		System.out.println("usage: javac com/aqr/trading/*.java (compile the source code) ");
		System.out.println("       java com.aqr.trading.PositionService fills.gz prices.gz (run the program with 2 input files");		
	}
}
