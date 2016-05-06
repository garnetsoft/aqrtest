package com.aqr.trading;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.aqr.trading.Message.MessageType;

public class PositionTest {
	
	@Test
	public void test() throws Exception {
		PositionContainer posContainer = new PositionContainer();
		List<Message> fills = new LinkedList<Message>();

		//F,1388534400000,MSFT,42.43,300,B

		// mixture of buy, sell/short positions	
		FillMessage f1 = new FillMessage(MessageType.Fill, 1388534400000L, "MSFT", 100.0, 12, FillMessage.Side.valueOf("B"));
		FillMessage f2 = new FillMessage(MessageType.Fill, 1388534400000L, "MSFT", 99.0, 17, FillMessage.Side.valueOf("B"));
		FillMessage f3 = new FillMessage(MessageType.Fill, 1388534400000L, "MSFT", 101.0, -9, FillMessage.Side.valueOf("S"));
		FillMessage f4 = new FillMessage(MessageType.Fill, 1388534400000L, "MSFT", 105.0, -4, FillMessage.Side.valueOf("S"));
		FillMessage f5 = new FillMessage(MessageType.Fill, 1388534400000L, "MSFT", 103.0, 3, FillMessage.Side.valueOf("B"));
		
		fills.add(f1);
		fills.add(f2);
		fills.add(f3);
		fills.add(f4);
		fills.add(f5);
		Collections.sort(fills);
		
		for (Message m : fills) {
			posContainer.updateMessage(m);
		}
		
		// lastes price update
		PriceUpdateMessage pxupd = new PriceUpdateMessage(MessageType.PriceUpdate, 1388534400000L, "MSFT", 99.0);
		posContainer.updateMessage(pxupd);
		
		assertEquals(19, posContainer.getPNLBySymbol("MSFT").getSize());
		assertEquals(18.0000, posContainer.getPNLBySymbol("MSFT").getPNL(), 0.00001);
		
	}

	@Test
	public void testIncreasePos() throws Exception {
		PositionContainer posContainer = new PositionContainer();
		List<Message> fills = new LinkedList<Message>();

		FillMessage f1 = new FillMessage(MessageType.Fill, 1388534400000L, "MSFT", 100.0, 12, FillMessage.Side.valueOf("B"));
		FillMessage f2 = new FillMessage(MessageType.Fill, 1388534400000L, "MSFT", 99.0, 17, FillMessage.Side.valueOf("B"));
		FillMessage f3 = new FillMessage(MessageType.Fill, 1388534400000L, "MSFT", 101.0, -9, FillMessage.Side.valueOf("S"));
		FillMessage f4 = new FillMessage(MessageType.Fill, 1388534400000L, "MSFT", 105.0, -4, FillMessage.Side.valueOf("S"));
		FillMessage f5 = new FillMessage(MessageType.Fill, 1388534400000L, "MSFT", 103.0, 3, FillMessage.Side.valueOf("B"));
		
		fills.add(f1);
		fills.add(f2);
		fills.add(f3);
		fills.add(f4);
		fills.add(f5);
		
		// scenario 1: receiving new fills that increase your position
		FillMessage f6 = new FillMessage(MessageType.Fill, 1388534400000L, "MSFT", 100.0, 10, FillMessage.Side.valueOf("B"));
		fills.add(f6);
		Collections.sort(fills);
		
		for (Message m : fills) {
			posContainer.updateMessage(m);
		}

		PriceUpdateMessage pxupd = new PriceUpdateMessage(MessageType.PriceUpdate, 1388534400000L, "MSFT", 99.0);
		posContainer.updateMessage(pxupd);
		
		assertEquals(0, 29, posContainer.getPNLBySymbol("MSFT").getSize());
		assertEquals(8.0000, posContainer.getPNLBySymbol("MSFT").getPNL(), 0.00001);

	}
	
	@Test
	public void testDecreasePos() throws Exception {
		PositionContainer posContainer = new PositionContainer();
		List<Message> fills = new LinkedList<Message>();

		FillMessage f1 = new FillMessage(MessageType.Fill, 1388534400000L, "MSFT", 100.0, 12, FillMessage.Side.valueOf("B"));
		FillMessage f2 = new FillMessage(MessageType.Fill, 1388534400000L, "MSFT", 99.0, 17, FillMessage.Side.valueOf("B"));
		FillMessage f3 = new FillMessage(MessageType.Fill, 1388534400000L, "MSFT", 101.0, -9, FillMessage.Side.valueOf("S"));
		FillMessage f4 = new FillMessage(MessageType.Fill, 1388534400000L, "MSFT", 105.0, -4, FillMessage.Side.valueOf("S"));
		FillMessage f5 = new FillMessage(MessageType.Fill, 1388534400000L, "MSFT", 103.0, 3, FillMessage.Side.valueOf("B"));
		
		fills.add(f1);
		fills.add(f2);
		fills.add(f3);
		fills.add(f4);
		fills.add(f5);
		
		// scenario 2: receiving new fills that decrease your position
		FillMessage f6 = new FillMessage(MessageType.Fill, 1388534400000L, "MSFT", 101.0, -12, FillMessage.Side.valueOf("S"));
		fills.add(f6);
		
		Collections.sort(fills);
		
		for (Message m : fills) {
			posContainer.updateMessage(m);
		}

		PriceUpdateMessage pxupd = new PriceUpdateMessage(MessageType.PriceUpdate, 1388534400000L, "MSFT", 99.0);
		posContainer.updateMessage(pxupd);
		
		assertEquals(7, posContainer.getPNLBySymbol("MSFT").getSize());
		assertEquals(42.0000, posContainer.getPNLBySymbol("MSFT").getPNL(), 0.00001);

	}
	
	@Test
	public void testFlatterPosition() throws Exception {
		PositionContainer posContainer = new PositionContainer();
		List<Message> fills = new LinkedList<Message>();

		FillMessage f1 = new FillMessage(MessageType.Fill, 1388534400000L, "MSFT", 100.0, 12, FillMessage.Side.valueOf("B"));
		FillMessage f2 = new FillMessage(MessageType.Fill, 1388534400000L, "MSFT", 99.0, 17, FillMessage.Side.valueOf("B"));
		FillMessage f3 = new FillMessage(MessageType.Fill, 1388534400000L, "MSFT", 101.0, -9, FillMessage.Side.valueOf("S"));
		FillMessage f4 = new FillMessage(MessageType.Fill, 1388534400000L, "MSFT", 105.0, -4, FillMessage.Side.valueOf("S"));
		FillMessage f5 = new FillMessage(MessageType.Fill, 1388534400000L, "MSFT", 103.0, 3, FillMessage.Side.valueOf("B"));
		
		fills.add(f1);
		fills.add(f2);
		fills.add(f3);
		fills.add(f4);
		fills.add(f5);
		
		// scenario 3: receiving new fills that flatten your position
		FillMessage f6 = new FillMessage(MessageType.Fill, 1388534400000L, "MSFT", 101.0, -19, FillMessage.Side.valueOf("S"));
		fills.add(f6);
		
		Collections.sort(fills);
		
		for (Message m : fills) {
			posContainer.updateMessage(m);
		}

		PriceUpdateMessage pxupd = new PriceUpdateMessage(MessageType.PriceUpdate, 1388534400000L, "MSFT", 99.0);
		posContainer.updateMessage(pxupd);
		
		assertEquals(0, posContainer.getPNLBySymbol("MSFT").getSize());
		assertEquals(56.0000, posContainer.getPNLBySymbol("MSFT").getPNL(), 0.00001);

	}
	
	@Test
	public void testReversePosition() throws Exception {
		PositionContainer posContainer = new PositionContainer();
		List<Message> fills = new LinkedList<Message>();

		FillMessage f1 = new FillMessage(MessageType.Fill, 1388534400000L, "MSFT", 100.0, 12, FillMessage.Side.valueOf("B"));
		FillMessage f2 = new FillMessage(MessageType.Fill, 1388534400000L, "MSFT", 99.0, 17, FillMessage.Side.valueOf("B"));
		FillMessage f3 = new FillMessage(MessageType.Fill, 1388534400000L, "MSFT", 101.0, -9, FillMessage.Side.valueOf("S"));
		FillMessage f4 = new FillMessage(MessageType.Fill, 1388534400000L, "MSFT", 105.0, -4, FillMessage.Side.valueOf("S"));
		FillMessage f5 = new FillMessage(MessageType.Fill, 1388534400000L, "MSFT", 103.0, 3, FillMessage.Side.valueOf("B"));
		
		fills.add(f1);
		fills.add(f2);
		fills.add(f3);
		fills.add(f4);
		fills.add(f5);
		
		// scenario 4: receiving new fills that reverse your position
		FillMessage f6 = new FillMessage(MessageType.Fill, 1388534400000L, "MSFT", 101.0, -22, FillMessage.Side.valueOf("S"));
		fills.add(f6);
		
		Collections.sort(fills);
		
		for (Message m : fills) {
			posContainer.updateMessage(m);
		}

		PriceUpdateMessage pxupd = new PriceUpdateMessage(MessageType.PriceUpdate, 1388534400000L, "MSFT", 99.0);
		posContainer.updateMessage(pxupd);
		
		assertEquals(-3, posContainer.getPNLBySymbol("MSFT").getSize());
		assertEquals(62.0000, posContainer.getPNLBySymbol("MSFT").getPNL(), 0.00001);

	}
}
