package se.scalablesolutions.akkasports.internal;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import se.scalablesolutions.akkasports.MatchEvent;
import se.scalablesolutions.akkasports.MatchEventListener;

public class MatchEventTracker implements MatchEventListener,Serializable {

	private static final long serialVersionUID = 5398297175472273428L;

	private AtomicInteger counter = new AtomicInteger(0);
	private AtomicInteger failedEventsCounter = new AtomicInteger(0);
	
	@Override
	public void handleEvent(MatchEvent event) {
		counter.incrementAndGet();
	}
	
	@Override
	public int countSuccessfulEvents() {
		return counter.get();
	}
	
	@Override
	public int countFailedEvents() {
		return failedEventsCounter.get();
	}

	@Override
	public void handleFailedEvent(MatchEvent event) {
		failedEventsCounter.incrementAndGet();
	}

}
