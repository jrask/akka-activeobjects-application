package se.scalablesolutions.akkasports;

import java.io.Serializable;

public abstract class MatchEvent implements Serializable{
	
	private static final long serialVersionUID = -8322012367170890593L;

	private String ticket;

	private long start = System.currentTimeMillis();
	private long time = -1L;
	
	public enum Type{
		NEW,STOP,GOAL,COMMENT,START
	}
	
	public abstract Type getType();
	
	private String uid;
	
	public MatchEvent(String ticket,String uid) {
		this.uid = uid;
		this.ticket = ticket;
	}
	
	public String getUid() {
		return uid;
	}

	public String getTicket() {
		return ticket;
	}
	
	public void stop() {
		time = System.currentTimeMillis() - start;
	}
	
	public long getTime() {
		if(time == -1L) {
			return System.currentTimeMillis() - start;
		}
		return time;
	}
	
	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ticket == null) ? 0 : ticket.hashCode());
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MatchEvent other = (MatchEvent) obj;
		if (ticket == null) {
			if (other.ticket != null)
				return false;
		} else if (!ticket.equals(other.ticket))
			return false;
		return true;
	}
}
