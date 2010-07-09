package se.scalablesolutions.akkasports.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.camel.ProducerTemplate;

import se.scalablesolutions.akka.actor.annotation.postrestart;
import se.scalablesolutions.akka.actor.annotation.transactionrequired;
import se.scalablesolutions.akka.camel.CamelContextManager;
import se.scalablesolutions.akka.persistence.common.PersistentVector;
import se.scalablesolutions.akka.persistence.redis.RedisStorage;
import se.scalablesolutions.akkasports.CommentEvent;
import se.scalablesolutions.akkasports.GoalEvent;
import se.scalablesolutions.akkasports.MatchActor;
import se.scalablesolutions.akkasports.MatchEvent;
import se.scalablesolutions.akkasports.MatchEventProcessingFailedException;
import se.scalablesolutions.akkasports.NewMatchEvent;
import se.scalablesolutions.akkasports.MatchEvent.Type;

/**
 * <p>This class represents a match between two teams</p>
 * It holds persistent state about which teams, comments and score
 * 
 * TODO - Manage UUID here or somewhere else?
 * 
 * @author johanrask
 *
 */
//@transactionrequired
public class MatchActorImpl implements MatchActor {
	
	// Redis map keys
	private final static String KEY_UID = "UID";
	private final static String KEY_HOME = "HOME";
	private final static String KEY_AWAY = "AWAY";
	private final static String KEY_STARTED = "STARTED";
	private final static String KEY_SCORE = "SCORE";
	
	//private PersistentMap<byte[], byte[]> matchState;
	private Map<String,String> matchState = new HashMap<String, String>();
	//private PersistentVector<byte[]> matchComments;
	private Vector<String> matchComments = new Vector<String>();
	
	// UID, stored only if restart is required
	private String uid;
	
	
	private String get(String key) {
		//return new String((matchState.get(key.getBytes()).get()));
		return matchState.get(key);
	}
	
	private void put(String key,String value) {
		matchState.put(key, value);
	}
	
	private boolean hasKey(String key) {
		return matchState.containsKey(key);
		//return matchState.contains(key.getBytes());
		//Extremely ugly method, could not figure out how to check None.
//		try {
//			matchState.get(key.getBytes()).get();
//			return true;
//		} catch(NoSuchElementException e) {
//			return false;
//		}
	}
	
	/**
	 * Real init method is made private so it can be safely called
	 * from within this class
	 */
	private void doInit(String uid) {
		//matchState = RedisStorage.getMap("match-" + uid);
		//matchComments = RedisStorage.getVector("comments-" + uid);
		this.uid = uid;
	}
	
	/**
	 * Initializes a Match that is already been stored
	 */
	public void init(String uid) {
		doInit(uid);
		if(!hasKey(KEY_HOME)) {
			throw new RuntimeException("NON EXISTING:" + uid);
		}
	}
	
	/**
	 * Creates a new Match
	 * @param readIfExists - if true, the match will be populated with the already
	 * stored information if it exists, otherwise and exception is thrown.
	 * 
	 * TODO - Not sure about the best approach for already existing matches...
	 */
	private void create(NewMatchEvent event) {
		doInit(uid);
			
		if(hasKey(KEY_AWAY)) {
			throw new MatchEventProcessingFailedException(event,"Match with " + uid + " already exists");
		}
		put(KEY_HOME,event.getHomeUid());
		put(KEY_AWAY,event.getAwayUid());
		put(KEY_SCORE,"0-0");
	}
		
	/**
	 * After a restart, state is re-initialized.
	 * TODO- Is this correct behaviour?
	 */
	@postrestart
	public void postRestart() {
		System.out.println("POST RESTART MATCH");
		doInit(uid);
	}
	

	@Override
	public void handleMatchEvent(MatchEvent... events) {
			for (MatchEvent event : events) {
				if(event.getType() == Type.NEW) {
					NewMatchEvent e = (NewMatchEvent)event;
					create(e);
				}
				else if(event.getType() == Type.COMMENT) {
					handleMatchEvent((CommentEvent)event);
				}  else if(event.getType() == Type.GOAL) {
					handleMatchEvent((GoalEvent)event);
				} else {
					throw new MatchEventProcessingFailedException(event,"Unknown event");
				}
				// All messages that are finished are sent to this endpoint
				ProducerTemplate template = CamelContextManager.template();
				template.sendBody("direct:finished",event);
			}
	}


	@Override
	public String getScore() {
		return get(KEY_SCORE);
	}
	
	@Override
	public List<String> getComments() {
		//TODO - How should this really be done?
		return Arrays.asList(matchComments.toArray(new String[0]));
//		scala.collection.Iterator<byte[]> it = matchComments.iterator();
//		List<String> tmpComments = new ArrayList<String>();
//		while(it.hasNext()) {
//			tmpComments.add(new String(it.next()));
//		}
//		return tmpComments;
	}
	
	@Override
	public String getUid() {
		return uid;
	}

	/**
	 * Implemented to support remote actors
	 */
	protected void handleMatchEvent(GoalEvent event) {
		System.out.println("(" + uid + ") MatchActor.handleMatchEvent():" +event.getType());
		String score = get(KEY_SCORE);
		// Tmp solution for verifying scores since they may not arrive in correct order.
		// Perhaps use timestamps?
		int newS = Integer.parseInt(event.getScore().substring(0,event.getScore().indexOf("-")));
		int oldS = Integer.parseInt(score.substring(0,score.indexOf("-")));
		if(newS > oldS) {
			score = event.getScore();
			System.out.println("Goal scored by " + event.getTeamUid());
			put(KEY_SCORE, score);
		}
	}

	protected void handleMatchEvent(CommentEvent event) {
		System.out.println("(" + uid + ") MatchActor.handleMatchEvent():" +event.getType());
		matchComments.add(event.getComment());
	}

	@Override
	public void testMap() {
//		System.out.println(Thread.currentThread() + " - " + new String(matchState.get(KEY_HOME.getBytes()).get()));
//		System.out.println(Thread.currentThread() + " - " + new String(matchState.get(KEY_HOME.getBytes()).get()));
//		System.out.println(Thread.currentThread() + " - " + new String(matchState.get(KEY_HOME.getBytes()).get()));
//		System.out.println(Thread.currentThread() + " - " + new String(matchState.get(KEY_HOME.getBytes()).get()));
	}
	

}
