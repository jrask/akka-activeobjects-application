package se.scalablesolutions.akkasports;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import se.scalablesolutions.akka.camel.CamelContextManager;
import se.scalablesolutions.akka.persistence.redis.RedisStorageBackend;
import se.scalablesolutions.akkasports.internal.MatchRegistry;

public class CamelTest {

	
	static ClassPathXmlApplicationContext ctx;
	
	String uid;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ctx = new ClassPathXmlApplicationContext("akkaSportsContext.xml");	
		
	}
	
	@AfterClass
	public static void after() {
		ctx.close();
	}
	
	@Before
	public void setup() {
		uid = "cameltest-" + System.currentTimeMillis();
	}

	@After
	public void deleteUidContents() {
		RedisStorageBackend.remove(uid);
	}
	
	@Test
	public void publishNewMatchSuccessful() throws InterruptedException {
		
		MatchRegistry registry = (MatchRegistry)ctx.getBean(MatchRegistry.class);
		CamelContextManager.template().
				requestBody("jetty:http://localhost:8877/camel/akkasports", 
						String.format("NEW:%s:AIK:HIF",uid));
		Thread.sleep(100);
		Assert.assertNotNull(registry.getMatch(uid));
	}
	
	
}
