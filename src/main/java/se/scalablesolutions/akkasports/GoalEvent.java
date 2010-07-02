package se.scalablesolutions.akkasports;

public class GoalEvent extends MatchEvent{

	private static final long serialVersionUID = -3613833090481096279L;

	private String teamUid;
	private String score;
	
	@Override
	public Type getType() {
		return Type.GOAL;
	}
	
	public GoalEvent(String matchUid, String teamUid,String score) {
		super(matchUid);
		this.teamUid = teamUid;
		this.score = score;
	}
	
	public String getScore() {
		return score;
	}
	
	public String getTeamUid() {
		return teamUid;
	}
}
