package se.scalablesolutions.akkasports;

import java.util.List;


public interface MatchEventTracker {
	
	public void addEvent(MatchEvent event);
	public void handleCompletedEvent(MatchEvent event);
	public int countEventsInProgress();
	public String getStatus(String ticket);
	public List<String> getNonCompletedEvents();
	public String getInfo();
}
