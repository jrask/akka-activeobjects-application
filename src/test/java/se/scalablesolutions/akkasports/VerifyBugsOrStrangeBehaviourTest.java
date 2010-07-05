package se.scalablesolutions.akkasports;

import static org.junit.Assert.assertEquals;
import static se.scalablesolutions.akka.actor.ActiveObject.link;
import static se.scalablesolutions.akka.actor.ActiveObject.newInstance;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import se.scalablesolutions.akka.actor.ActiveObject;
import se.scalablesolutions.akka.config.OneForOneStrategy;
import se.scalablesolutions.akka.remote.RemoteNode;
import se.scalablesolutions.akka.remote.RemoteServer;
import se.scalablesolutions.akkasports.internal.MatchActorImpl;


/**
 *
 * @author johanrask
 *
 */
public class VerifyBugsOrStrangeBehaviourTest {

	static RemoteServer remoteServer;
	static ClassPathXmlApplicationContext ctx;
	
	
	@BeforeClass
	public static void setup() {
		remoteServer = RemoteNode.start("localhost", 9998);
		ctx = new ClassPathXmlApplicationContext("myPersistentPojoCtx.xml");		
	}
	
	@AfterClass
	public static void shutdown() {
	
		remoteServer.shutdown();
	}
	
	/**
	 * Verifies that normal actors work as expected
	// */
	@Test
	public void verifyActorsWork() throws InterruptedException {
		
		MyPojo newMatch1 = ActiveObject.newInstance(MyPojo.class,10000);
		MyPojo newMatch2 = ActiveObject.newInstance(MyPojo.class,10000);
		newMatch1.setName("pojo1");
		newMatch2.setName("pojo2");		
		
		Thread.sleep(1000);
		assertEquals("pojo1", newMatch1.getName());
		assertEquals("pojo2", newMatch2.getName());
	}
	
	/**
	 * This test does the same as the test above but we do not get the expected
	 * results in our assertion.
	 */
	@Test
	public void verifyRemoteActorsDoNotWork() throws InterruptedException {
			
		MyPojo newMatch1 = ActiveObject.newRemoteInstance(MyPojo.class,10000,"localhost",9998);
		MyPojo newMatch2 = ActiveObject.newRemoteInstance(MyPojo.class,10000,"localhost",9998);
		newMatch1.setName("pojo1");
		newMatch2.setName("pojo2");		
		
		Thread.sleep(1000);
		// This should be pojo1
		assertEquals("pojo2", newMatch1.getName());
		assertEquals("pojo2", newMatch2.getName());
	}
	
	
	
	/**
	 * This test shows that postRestart method is not invoked on an actor.
	 * 
	 * THIS BUG IS NOW FIXED, THIS TEST WORKS AS EXPECTED
	 *
	 */
	@Test
	public void testFailingPostRestartInvocation() throws InterruptedException {
		MyPojo pojo = newInstance(MyPojo.class,500);
		MyPojo supervisor = newInstance(MyPojo.class,500);
		link(supervisor,pojo,new OneForOneStrategy(3, 2000),new Class[]{Throwable.class});
		pojo.throwException();
		Thread.sleep(1000);
		Assert.assertTrue(pojo.pre);
		Assert.assertTrue(pojo.post);
	}
	
	
}
