package com.aqr.trading;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PositionService 
{
	public static void main(String[] args) {
		if (args.length > 0) {
			//List<Message> fills = Utils.readGZFile("data/fills.gz", "F");
			//List<Message> prices = Utils.readGZFile("data/prices.gz", "P");

			List<Message> prices = Utils.readGZFile(args[0], "F");
			List<Message> fills = Utils.readGZFile(args[1], "P");			
			List<Message> all = new ArrayList<Message>(prices.size()+ fills.size());
			
			all.addAll(fills);
			all.addAll(prices);

			//System.out.println("prices: " + prices.size());
			//System.out.println("fills: " + fills.size());
			//System.out.println("count: " + all.size());
					
			Collections.sort(all);
			
			// position container
			PositionContainer positions = new PositionContainer();
			
			// replaying all messages in chronological order
			for (Message m : all) {
				positions.updateMessage(m);
			}
		}
		else {
			System.out.println("usage: javac com/aqr/trading/*.java (compile the source code) ");
			System.out.println("       java com.aqr.trading.PositionService fills.gz prices.gz (run the program with 2 input files");
		}
	}
	
	public static void usage() {
		
	}
}
