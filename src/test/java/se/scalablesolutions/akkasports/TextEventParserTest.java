package se.scalablesolutions.akkasports;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import se.scalablesolutions.akka.persistence.redis.RedisStorageBackend;
import se.scalablesolutions.akkasports.internal.MatchRegistry;
import se.scalablesolutions.akkasports.internal.TextEventParser;

import static org.junit.Assert.*;

public class TextEventParserTest {
	
static ClassPathXmlApplicationContext ctx;
	
	private String uid;

	private static MatchRegistry registry;
	private static TextEventParser parser;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ctx = new ClassPathXmlApplicationContext("akkaSportsContext.xml");		
		registry = (MatchRegistry)ctx.getBean("matchRegistry");
		 parser = (TextEventParser)ctx.getBean("parser");
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
		registry.removeMatch(uid);
		assertEquals(0,registry.getMatches().size());
	}
	
	@Test
	public void testParseIllegalString() throws Exception{
		parser.parse("sdfsdf:asdf");
		Thread.sleep(100);
		assertEquals(0,registry.getMatches().size());
	}
	
	@Test
	public void testParseNewGame() throws Exception{
		parser.parse(String.format("NEW:%s:MFF:HIF",uid));
		Thread.sleep(100);
		assertEquals(1,registry.getMatches().size());
	}
	
	@Test
	public void testParseNewComment() throws Exception{
		parser.parse(String.format("NEW:%s:MFF:HIF",uid));
		parser.parse(String.format("COMMENT:%s:COMMENT",uid));
		Thread.sleep(100);
		assertEquals(1,registry.getMatches().size());
		MatchActor match = registry.getMatch(uid);
		assertEquals(1,match.getComments().size());
		assertEquals("COMMENT",match.getComments().get(0));
	}
	
	@Test
	public void testParseNewGoal() throws Exception{
		parser.parse(String.format("NEW:%s:MFF:HIF",uid));
		parser.parse(String.format("GOAL:%s:MFF:1-0",uid));
		Thread.sleep(100);
		assertEquals(1,registry.getMatches().size());
		MatchActor match = registry.getMatch(uid);
		assertEquals("1-0",match.getScore());
	}
	
	@Test(expected = RuntimeException.class)
	public void testParseNewMatchWithoutActor() throws Exception{
		TextEventParser parser = new TextEventParser();
		parser.parse("sdfsdf:asdf");
	}
	
	@Test(expected = RuntimeException.class)
	public void testParseIllegalStringWithoutActor() throws Exception{
		TextEventParser parser = new TextEventParser();
		parser.parse("sdfsdfasdf");
	}
	
	@Test(expected = NullPointerException.class)
	public void testParseNullWithoutActor() throws Exception{
		TextEventParser parser = new TextEventParser();
		parser.parse(null);
	}
}
