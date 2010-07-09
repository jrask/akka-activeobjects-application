package se.scalablesolutions.akkasports.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.management.DescriptorKey;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import se.scalablesolutions.akka.actor.ActiveObject;
import se.scalablesolutions.akka.actor.annotation.shutdown;
import se.scalablesolutions.akka.actor.annotation.transactionrequired;
import se.scalablesolutions.akka.persistence.common.PersistentMap;
import se.scalablesolutions.akka.persistence.common.PersistentVector;
import se.scalablesolutions.akka.persistence.redis.RedisStorage;
import se.scalablesolutions.akkasports.MatchActor;
import se.scalablesolutions.akkasports.MatchEvent;
import se.scalablesolutions.akkasports.MatchEventProcessingFailedException;
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

//@transactionrequired
public class MatchRegistry implements ApplicationContextAware{
	
	private ApplicationContext ctx;
	
	private Map<byte[],byte[]> matchUuids = new HashMap<byte[], byte[]>();
	
	
	private Map<String,MatchActor> matches = 
		Collections.synchronizedMap(new HashMap<String, MatchActor>());
	
	public MatchActor getMatch(String uid) {
		return matches.get(uid);
	}
	
	private void addMatch(String uid,MatchActor match) {
		//new RuntimeException().printStackTrace();
		System.out.println("**** addmatch");
		matches.put(uid, match);
		matchUuids.put(uid.getBytes(),"".getBytes());
		//matchState.add(uid.getBytes(),0f);
	}
	
	public void removeMatch(String uid) {
		matches.remove(uid);
		matchUuids.remove(uid.getBytes());
		//matchState.remove(uid.getBytes());
	}	
	
	public Collection<MatchActor> getMatches() {
		return matches.values();
	}

	public void handleMatchEvent(MatchEvent event) {
		if(event instanceof NewMatchEvent) {
			createMatch((NewMatchEvent)event);
			return;
		}
		MatchActor match = matches.get(event.getUid());
		match.handleMatchEvent(event);
	}
	
	public void createMatch(NewMatchEvent event) {
		//new Exception().printStackTrace();
		System.out.println(" -> createMatch:" + event.getUid());
		if(matches.containsKey(event.getUid())) {
			throw new MatchEventProcessingFailedException(event,
						"Match with uid " + event.getUid() + " already exists" + matches);
			
		}

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
	
	@shutdown
	public void shutdown() {
		System.out.println("Stopping all matches");
		for(MatchActor actor : matches.values()) {
			ActiveObject.stop(actor);
		}
	}
	
	@PostConstruct
	public void loadPersistentMatchActors() {
		System.out.println("Loading persistent actors");
		Iterator<byte[]> it = matchUuids.keySet().iterator();
		while(it.hasNext()) {
			String uid = new String(it.next());
			System.out.println("Loading match with uid: " + uid);
			MatchActor newMatch = (MatchActor)ctx.getBean("match");
			newMatch.init(uid);
			addMatch(uid, newMatch);
		}
	}
	
	@PreDestroy
	public boolean destroy() {
		System.out.println(Thread.currentThread().getName() + " ********* destroy()");
		return true;
	}
}
