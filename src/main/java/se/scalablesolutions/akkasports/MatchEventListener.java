package se.scalablesolutions.akkasports;


public interface MatchEventListener {
	
	public void handleEvent(MatchEvent event);
	public void handleFailedEvent(MatchEvent event);
	public int countSuccessfulEvents();
	public int countFailedEvents();
}
