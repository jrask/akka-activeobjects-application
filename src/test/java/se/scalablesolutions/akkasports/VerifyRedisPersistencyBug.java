package se.scalablesolutions.akkasports;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import se.scalablesolutions.akka.actor.ActiveObject;

public class VerifyRedisPersistencyBug {

	static ClassPathXmlApplicationContext ctx;
	
	
	@BeforeClass
	public static void setup() {
		ctx = new ClassPathXmlApplicationContext("myPersistentPojoCtx.xml");		
	}
	
	@AfterClass
	public static void shutdown() {
		ctx.close();
	}
	
	@Test
	public void testStrangePersistencyBehaviour() throws InterruptedException {
		
		MyPersistentPojo[] pojos = new MyPersistentPojo[100];
		
		for(int i = 0; i < 100; i++) {
			MyPersistentPojo actor = ActiveObject.newInstance(MyPersistentPojo.class, 10000, true);
			actor.create("uuid." + i, "value-" + i);
			pojos[i] = actor;
			
			// Remove this for the error to occur
			Thread.sleep(30);
		}
		
		// Make sure that it finishes
		Thread.sleep(2000);
		
		int i = 0;
		for (MyPersistentPojo myPersistentPojo : pojos) {
			Assert.assertTrue(myPersistentPojo.load("uuid." + i));
			i++;
		}
		
	}
}
