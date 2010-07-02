package se.scalablesolutions.akkasports.internal;

import se.scalablesolutions.akka.remote.RemoteNode;

public class Server {
	
	public static void main(String[] args) {
		RemoteNode.start("localhost", 9999);
	}

}
