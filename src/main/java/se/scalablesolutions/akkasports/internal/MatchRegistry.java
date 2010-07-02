package se.scalablesolutions.akkasports.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import se.scalablesolutions.akkasports.MatchActor;
import se.scalablesolutions.akkasports.NewMatchEvent;

/**
 * This class becomes a bottleneck since calls must go through this class
 * both to create matches as well as fetch matches.
 * 
 * This was a regular bean where state was guarded by a synchronized map first but
 * was turned to an actor when I tried to add persistency.
 * 
 * @author johanrask
 *
 */

public class MatchRegistry implements ApplicationContextAware{
	
	private ApplicationContext ctx;
	
	//private PersistentSortedSet<byte[]> matchState = RedisStorage.getSortedSet("MatchRegistry");//getVector("MatchRegistry");
	
	
	private Map<String,MatchActor> matches = 
		Collections.synchronizedMap(new HashMap<String, MatchActor>());
	
	public MatchActor getMatch(String uid) {
		return matches.get(uid);
	}
	
	private void addMatch(String uid,MatchActor match) {
		matches.put(uid, match);
		//matchState.add(uid.getBytes(),0f);
	}
	
	public void removeMatch(String uid) {
		matches.remove(uid);
		//matchState.remove(uid.getBytes());
	}
	
	public Collection<MatchActor> getMatches() {
		return matches.values();
	}

	
	public void createMatch(NewMatchEvent event) {
		System.out.println(" -> createMatch:" + event.getUid());
		MatchActor newMatch = (MatchActor)ctx.getBean("match");
		newMatch.handleMatchEvent(event);	
		addMatch(event.getUid(), newMatch);
		System.out.println(" <- createMatch:" + event.getUid());
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.ctx = ctx;
	}
}
