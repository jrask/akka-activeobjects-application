package se.scalablesolutions.akkasports.camel;

import org.apache.camel.builder.RouteBuilder;

public class CustomRouteBuilder extends RouteBuilder {

	
	@Override
	public void configure() throws Exception {
		 from("file:data/events").to("active-object:router?method=parse");
		 from("file:data/new").to("active-object:router?method=parse");
		 from("jetty:http://localhost:8877/camel/akkasports").to("active-object:router?method=parse");
	}
}
