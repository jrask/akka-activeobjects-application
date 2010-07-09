package se.scalablesolutions.akkasports.internal;

import se.scalablesolutions.akka.actor.annotation.postrestart;
import se.scalablesolutions.akka.actor.annotation.prerestart;
import se.scalablesolutions.akka.actor.annotation.shutdown;
import se.scalablesolutions.akkasports.CommentEvent;
import se.scalablesolutions.akkasports.GoalEvent;
import se.scalablesolutions.akkasports.MatchEvent;
import se.scalablesolutions.akkasports.NewMatchEvent;

public class TextEventParser extends AbstractEventParser {
    
	@Override
	public void parse(String event,String ticket) {	
		
		//Add some extra processing
//		for(int i = 0; i < 100;i++) {
//			for(byte b : event.getBytes()) {
//				event.replace(":", new String(new byte[]{b}));
//			}
//		}
		
		String[]str = event.split(":");
		System.out.println("UID:" + str[1]);
		if(str[0].equals(MatchEvent.Type.COMMENT.name())) {
			delegate(new CommentEvent(ticket,str[1],str[2]));
		} else if(str[0].equals(MatchEvent.Type.GOAL.name())){
			delegate(new GoalEvent(ticket,str[1],str[2],str[3]));
		} else if(str[0].equals(MatchEvent.Type.NEW.name())) {
			delegate(new NewMatchEvent(ticket,str[1], str[2], str[3]));
		}
		else {
			System.out.println("Failed: "  +Thread.currentThread().getName());
			throw new RuntimeException("Unknown type: " + str[0] + " ("+Thread.currentThread().getName() + ")");
		}
	}
    	
	@shutdown
	public void shutdown() {
		System.out.println("* * * * TextEventParser.shutdown()");
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
