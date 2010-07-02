package se.scalablesolutions.akkasports;

import se.scalablesolutions.akka.actor.annotation.postrestart;
import se.scalablesolutions.akka.actor.annotation.prerestart;

public class MyPojo {
	
	public boolean pre = false;
	public boolean post = false;
	
	private String name;
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@prerestart
	public void pre() {
		System.out.println("** pre()");
		pre = true;
	}
	
	@postrestart
	public void post() {
		System.out.println("** post()");
		post = true;
	}

	public void throwException() {
		throw new RuntimeException();
	}
}
