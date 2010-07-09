package se.scalablesolutions.akkasports;

public class NewMatchEvent extends MatchEvent {

	private static final long serialVersionUID = -8543906868936864757L;

	@Override
	public Type getType() {
		return Type.NEW;
	}

	String teamHomeUid;
	String teamAwayUid;
	
	public NewMatchEvent(String ticket,String uid,String teamHomeUid,String awayUid){
		super(ticket,uid);
		this.teamHomeUid = teamHomeUid;
		this.teamAwayUid = awayUid;
	}

	public String getHomeUid() {
		return teamHomeUid;
	}
	
	public String getAwayUid() {
		return teamAwayUid;
	}
	
}
