package se.scalablesolutions.akkasports;

public class MatchEventProcessingFailedException extends RuntimeException {
	

	private static final long serialVersionUID = 8534323711275878016L;

	private MatchEvent event;

	public MatchEventProcessingFailedException(MatchEvent event) {
		this.event = event;
	}	
	
	public MatchEventProcessingFailedException(MatchEvent event,String reason) {
		super(reason);
		this.event = event;
	}	
	
	public MatchEventProcessingFailedException(MatchEvent event,Throwable t) {
		super(t);
		this.event = event;
	}	
	
	public MatchEventProcessingFailedException(MatchEvent event,Throwable t, String reason) {
		super(reason,t);
		this.event = event;
	}	
	
	
	public MatchEvent getEvent() {
		return event;
	}
}
