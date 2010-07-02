package se.scalablesolutions.akkasports;

public class CommentEvent extends MatchEvent {

	private static final long serialVersionUID = 1885778199122220802L;
	
	private String comment;
	
	@Override
	public Type getType() {
		return Type.COMMENT;
	}

	public CommentEvent(String uid,String comment) {
		super(uid);
		this.comment = comment;
	}

	public String getComment() {
		return comment;
	}
}
