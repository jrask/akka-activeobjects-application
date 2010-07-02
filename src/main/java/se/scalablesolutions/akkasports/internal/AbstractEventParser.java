package se.scalablesolutions.akkasports.internal;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import se.scalablesolutions.akkasports.EventParser;
import se.scalablesolutions.akkasports.MatchActor;
import se.scalablesolutions.akkasports.MatchEvent;
import se.scalablesolutions.akkasports.MatchEventListener;
import se.scalablesolutions.akkasports.NewMatchEvent;

/**
 * <p>Base class for implementing {@link EventParser}</p>
 * This class manages Matches and MatchRegistry and delegates events
 * to correct Match.
 * 
 * @author johanrask
 *
 */
public abstract class AbstractEventParser implements EventParser,ApplicationContextAware {

	protected ApplicationContext ctx;
	
	private MatchRegistry registry;
	private MatchEventListener tracker;
	
	public void setRegistry(MatchRegistry registry) {
		this.registry = registry;
	}
	
	public void setTracker(MatchEventListener tracker) {
		this.tracker = tracker;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.ctx = ctx;
		
	}
	
	
	// Hmm... this method is not called in the same thread.
	// How can this be avoided?
	protected void delegate(MatchEvent event) {
		if(event instanceof NewMatchEvent) {
			registry.createMatch((NewMatchEvent)event);
			return;
		}
		
		MatchActor match = registry.getMatch(event.getUid());
		if(match == null) {
			tracker.handleFailedEvent(event);
			throw new RuntimeException("Unable to find Match uid: " + event.getUid());
		} else {
			match.handleMatchEvent(event);
		}		
	}

	
	/*
	 * I would like to implement parse method here
	 * and then force subclasses to implement
	 * MatchEvent parse(String) but since this
	 * method has to be protected it will have to use
	 * a Future.
	 */
	@Override
	public abstract void parse(String event);

	

}
