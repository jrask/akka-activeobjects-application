package se.scalablesolutions.akkasports;

import java.io.Serializable;

public abstract class MatchEvent implements Serializable{
	
	private static final long serialVersionUID = -8322012367170890593L;

	public enum Type{
		NEW,STOP,GOAL,COMMENT,START
	}
	
	public abstract Type getType();
	
	private String uid;
	
	public MatchEvent(String uid) {
		this.uid = uid;
	}
	
	public String getUid() {
		return uid;
	}
}
