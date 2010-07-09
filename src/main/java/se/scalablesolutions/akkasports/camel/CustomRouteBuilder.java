package se.scalablesolutions.akkasports.camel;

import org.apache.camel.builder.RouteBuilder;

public class CustomRouteBuilder extends RouteBuilder {

	
	@Override
	public void configure() throws Exception {
		 from("file:data/events").to("active-object:router?method=parse");
		 from("file:data/new").to("active-object:router?method=parse");

		 from("jetty:http://localhost:8877/camel/akkasports").
		 	to("active-object:receiver?method=receive");
		 
		 from("jetty:http://localhost:8877/camel/akkasports/cnt").
		 	to("active-object:tracker?method=countEventsInProgress");
		 
		 from("jetty:http://localhost:8877/camel/akkasports/info").
		 	to("active-object:tracker?method=getInfo");
		 
		 from("jetty:http://localhost:8877/camel/akkasports/status").
		 	to("active-object:tracker?method=getStatus");
		 
		 from("direct:finished").to("active-object:tracker?method=handleCompletedEvent");
		 from("direct:resend").to("active-object:matchRegistry?method=handleMatchEvent");
	}
}
