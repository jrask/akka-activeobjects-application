package se.scalablesolutions.akkasports;

import java.io.Serializable;

public class Team implements Serializable{
	
	private static final long serialVersionUID = -6689887209890270042L;
	
	private String name;
	private String uid;
	
	public Team(String name, String uid) {
		super();
		this.name = name;
		this.uid = uid;
	}
	
	public Team(Team team) {
		this(team.getName(),team.getUid());
	}
	
	public String getName() {
		return name;
	}
	
	public String getUid() {
		return uid;
	}
}
