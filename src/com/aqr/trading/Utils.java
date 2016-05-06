package com.aqr.trading;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import com.aqr.trading.Message.MessageType;

public class Utils 
{
	public static List<Message> readGZFile(String gzfile, String type) {
		List<Message> list = new LinkedList<>();

		BufferedReader in = null;

		try {
			in = new BufferedReader(new InputStreamReader(new GZIPInputStream(
					new FileInputStream(gzfile))));

			String content;

			while ((content = in.readLine()) != null) {
				//System.out.println(content);

				Message m = null;
				
				if ("P".equals(type)) {
					String[] values = content.split(" ");
					String tp = values[0];
					long time = Long.parseLong(values[1]);
					String symbol = values[2];
					double price = Double.parseDouble(values[3]);
					
					m = new PriceUpdateMessage(MessageType.PriceUpdate, time, symbol, price);
					
				} else if ("F".equals(type)) {
					String[] values = content.split(" ");
					String tp = values[0];
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

				if (m != null) {
					list.add(m);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return list;
	}
	
	public static List<Message> readCSVFile(String gzfile, String type) {
		List<Message> list = new LinkedList<>();

		BufferedReader in = null;

		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(gzfile)));

			String content = in.readLine();  // skip header line

			while ((content = in.readLine()) != null) {
				//System.out.println(content);

				Message m = null;
				
				if ("P".equals(type)) {
					String[] values = content.split(",");
					String tp = values[0];
					long time = Long.parseLong(values[1]);
					String symbol = values[2];
					double price = Double.parseDouble(values[3]);
					
					m = new PriceUpdateMessage(MessageType.PriceUpdate, time, symbol, price);
					
				} else if ("F".equals(type)) {
					String[] values = content.split(",");
					//String tp = values[0];
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

				if (m != null) {
					list.add(m);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return list;
	}
}
