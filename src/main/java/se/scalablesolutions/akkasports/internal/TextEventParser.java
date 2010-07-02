package se.scalablesolutions.akkasports.internal;

import se.scalablesolutions.akka.actor.annotation.postrestart;
import se.scalablesolutions.akka.actor.annotation.prerestart;
import se.scalablesolutions.akkasports.CommentEvent;
import se.scalablesolutions.akkasports.GoalEvent;
import se.scalablesolutions.akkasports.MatchEvent;
import se.scalablesolutions.akkasports.NewMatchEvent;

public class TextEventParser extends AbstractEventParser {
    
	// MATCH:MATCHID:TEAMID:TEAMID
	// GOAL:MATCHID:TEAMID:1-1
	// COMMENT:MATCHID:STRING
	@Override
	public void parse(String event) {	
		
		//System.out.println("parse() "  +toString());
		String[]str = event.split(":");
		System.out.println("UID:" + str[1]);
		if(str[0].equals(MatchEvent.Type.COMMENT.name())) {
			parseComment(str);
		} else if(str[0].equals(MatchEvent.Type.GOAL.name())){
			parseGoal(str);
		} else if(str[0].equals(MatchEvent.Type.NEW.name())) {
			parseNewMatch(str);
		}
		else {
			System.out.println("Failed: "  +Thread.currentThread().getName());
			throw new RuntimeException("Unknown type: " + str[0] + " ("+Thread.currentThread().getName() + ")");
		}
	}
    
	private void parseNewMatch(String[] str) {
		delegate(new NewMatchEvent(str[1], str[2], str[3]));
	}
	
	
	private void parseGoal(String[] str) {
		delegate(new GoalEvent(str[1],str[2],str[3]));
	}

	private void parseComment(String[] str) {
		delegate(new CommentEvent(str[1],str[2]));
	}
	
	@prerestart
	public void preRestart() {
		System.out.println("* * * * * preRestart()");
	}
	
	@postrestart
	public void postRestart() {
		System.out.println("* * * * * postRestart()");
	}

}
