package se.scalablesolutions.akkasports.internal;

import java.util.concurrent.atomic.AtomicInteger;

import se.scalablesolutions.akkasports.EventParser;

public class EventReceiver {

	public AtomicInteger counter = new AtomicInteger(0);
	
	private EventParser parser;
	
	public void setParser(EventParser parser) {
		this.parser = parser;
	}
	
	public String receive(String event) {
		int ticket = counter.incrementAndGet();
		parser.parse(event,String.valueOf(ticket));
		return String.valueOf(ticket);
	}
}
