package se.scalablesolutions.akkasports;

import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import se.scalablesolutions.akka.dispatch.FutureTimeoutException;
import se.scalablesolutions.akka.persistence.redis.RedisStorageBackend;
import se.scalablesolutions.akkasports.internal.MatchRegistry;

public class MatchActorTest {
	
	static ClassPathXmlApplicationContext ctx;
	
	private String uid;

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
		uid = "matchactortest-" + System.currentTimeMillis();
	}

	@After
	public void deleteUidContents() {
		RedisStorageBackend.remove(uid);
	}
	
	/**
	 * Do not understand why this method hangs when I try to add two events?!
	 * Does not matter if it is the same uid or not
	 */
	@Test//(expected = FutureTimeoutException.class)
	public void verifyNestedTransactionDoesNotWork() {
		MatchRegistry registry = (MatchRegistry)ctx.getBean(MatchRegistry.class);

		// Invoke two times.
		registry.createMatch(new NewMatchEvent(uid, "AIK", "MFF"));
		registry.createMatch(new NewMatchEvent(uid, "AIK", "MFF"));
		
		// For some reason it hangs here, regardless of how long the timeout is set
		MatchActor match = registry.getMatch(uid);
		match.handleMatchEvent(new GoalEvent(uid, "AIK", "1-0"));
		Assert.assertEquals("1-0",match.getScore());
	}
	
	@Test
	public void testMatchRegistry() {
		MatchRegistry registry = (MatchRegistry)ctx.getBean(MatchRegistry.class);
		registry.createMatch(new NewMatchEvent(uid, "AIK", "MFF"));
		MatchActor match = registry.getMatch(uid);
		Assert.assertEquals("0-0",match.getScore());
		match.handleMatchEvent(new GoalEvent(uid, "AIK", "1-0"));
		Assert.assertEquals("1-0",match.getScore());
	}
	
	@Test
	public void testSaveMatchActor() throws InterruptedException {
		
		MatchActor match = (MatchActor)ctx.getBean("match");
		match.handleMatchEvent(new NewMatchEvent(uid, "MMF", "HIF"));
		match.handleMatchEvent(new GoalEvent(uid, "MFF", "1-0"));
		Assert.assertEquals("1-0",match.getScore());
		
		match.handleMatchEvent(new CommentEvent(uid, "My Comment"));
		Assert.assertEquals(1,match.getComments().size());
		Assert.assertEquals("My Comment",match.getComments().get(0));
		
		// Forces restart of actor since a NPE is thrown
		match.handleMatchEvent(null);
		
		// Verify contents after restart
		Assert.assertEquals("1-0",match.getScore());
		Assert.assertEquals(1,match.getComments().size());
		Assert.assertEquals("My Comment",match.getComments().get(0));
		
		// Create new instance of same game and verify score
		MatchActor match2 = (MatchActor)ctx.getBean("match");
		match2.init(uid);
		Assert.assertEquals("1-0",match2.getScore());
		
	}
	
	@Test(expected = NoSuchElementException.class)
	public void testInitiateNonExistingMatch() {
		// Create new instance of same game and verify score
		MatchActor match = (MatchActor)ctx.getBean("match");
		match.init(uid);
		
		// We now have an invalid match so this will throw exception
		// FIXME - Not sure how to deal with this scenario
		Assert.assertEquals("1-0",match.getScore());
	}
	
}
