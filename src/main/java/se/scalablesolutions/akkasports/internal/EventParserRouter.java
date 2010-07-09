package se.scalablesolutions.akkasports.internal;

import java.util.List;

import se.scalablesolutions.akka.actor.ActiveObject;
import se.scalablesolutions.akka.actor.annotation.shutdown;
import se.scalablesolutions.akkasports.EventParser;


public class EventParserRouter implements EventParser {

	private List<EventParser> parsers = null;
	
	private int currentParser = 0;
	
	public void setParsers(List<EventParser> parsers) {
		this.parsers = parsers;
	}
	
	@shutdown
	public void shutdown() {
		for(EventParser parser : parsers) {
			ActiveObject.stop(parser);
		}
	}
	
	@Override
	public void parse(String event,String ticket) {
		parsers.get(currentParser).parse(event,ticket);
		currentParser++;
		if(currentParser >= parsers.size()) {
			currentParser = 0;
		}
	}
}
