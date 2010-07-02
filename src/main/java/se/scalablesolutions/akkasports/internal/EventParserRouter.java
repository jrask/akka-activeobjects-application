package se.scalablesolutions.akkasports.internal;

import java.util.List;

import se.scalablesolutions.akkasports.EventParser;


public class EventParserRouter implements EventParser {

	private List<EventParser> parsers = null;
	
	private int currentParser = 0;
	
	public void setParsers(List<EventParser> parsers) {
		this.parsers = parsers;
	}
	
	@Override
	public void parse(String event) {
		parsers.get(currentParser).parse(event);
		currentParser++;
		if(currentParser >= parsers.size()) {
			currentParser = 0;
		}
	}

}
