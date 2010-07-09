package se.scalablesolutions.akkasports.bin;

import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import se.scalablesolutions.akka.persistence.redis.RedisStorageBackend;


/**
 * This class fires up the Application
 * 
 * @author johanrask
 *
 */
public class Start {

	
	static ClassPathXmlApplicationContext ctx;
	
	public static void main(String args[]) throws InterruptedException, IOException {

		ctx = new ClassPathXmlApplicationContext("akkaSportsContext.xml");	
		
		
		//RedisStorageBackend.flushDB();
		
		//final MatchRegistry registry = (MatchRegistry)ctx.getBean("matchRegistry");
	}
}
