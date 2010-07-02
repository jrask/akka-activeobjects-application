package se.scalablesolutions.akkasports;

import java.util.List;

public interface MatchActor {
	
	public void handleMatchEvent(MatchEvent... events);
	public String getScore();
	public List<String> getComments();
	public String getUid();
	public void init(String uid);
}
