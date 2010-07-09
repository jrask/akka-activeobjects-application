package se.scalablesolutions.akkasports.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import se.scalablesolutions.akka.camel.CamelContextManager;
import se.scalablesolutions.akkasports.MatchEvent;
import se.scalablesolutions.akkasports.MatchEventTracker;
import se.scalablesolutions.akkasports.NewMatchEvent;

public class MatchEventTrackerImpl implements MatchEventTracker,Serializable,BeanPostProcessor {

	private static final long serialVersionUID = 5398297175472273428L;

	private Map<MatchEvent,Long> events = new Hashtable<MatchEvent,Long>();
	
	private AtomicInteger counter = new AtomicInteger(0);
	private AtomicInteger failedEventsCounter = new AtomicInteger(0);
	private Map<String,MatchEvent> evicted = new Hashtable<String, MatchEvent>();
	private Map<String,MatchEvent> completed = new Hashtable<String, MatchEvent>();
	
	private AtomicLong maxTime = new AtomicLong(0);
	private AtomicLong minTime = new AtomicLong(10000);
	
	public MatchEventTrackerImpl() {
		new Thread( new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("Resending non completed events");
					
					MatchEvent[] eventArr = events.keySet().toArray(new MatchEvent[0]);
					for (MatchEvent event : eventArr) {
						Long start = events.get(event);
						if(start == null) {
							continue;
						}
						// Max allowed time in processing before eviction is 2 minute
						if((System.currentTimeMillis() - start) > 120000) {
							System.out.println("Event " + event.getUid() + " is beeing evicted due to unable to process");
							CamelContextManager.template().sendBody("direct:evict",event);
							events.remove(event);
							evicted.put(event.getTicket(),event);
						} else if((System.currentTimeMillis() - start) > 60000) {
							CamelContextManager.template().sendBody("direct:resend",event);
							failedEventsCounter.incrementAndGet();
						}
					}

				}
			}
		}).start();
	}
	
	@Override
	public void addEvent(MatchEvent event) {
		events.put(event,System.currentTimeMillis());
	}
	
	@Override
	public int countEventsInProgress() {	
			return events.size();
	}

	@Override
	public String getInfo() {
		return String.format("Current in progress: %s \n" +
				"Evicted %s \n" +
				"Processed %s \n" +
				"Resent %s \n" +
				"Max time %s \n" +
				"Min time %s \n" +
				"",events.size(),evicted.size(),counter.get(),failedEventsCounter.get(),maxTime.get(),minTime.get());
	}
	
	@Override
	public void handleCompletedEvent(MatchEvent event) {
		//failedEventsCounter.incrementAndGet();
		event.stop();
		maxTime.set(Math.max(maxTime.get(), event.getTime()));
		minTime.set(Math.min(minTime.get(), event.getTime()));
		int cnt = events.size();
		events.remove(event);
		completed.put(event.getTicket(), event);
		counter.incrementAndGet();
		System.out.println("Currently non completed events is " + events.size() + " (" + cnt +")");
	}

	@Override
	public Object postProcessAfterInitialization(final Object bean, String name) 
			throws BeansException {
//		if(bean instanceof MatchActor) {
//			System.out.println("Proxying " + bean);
//			return Proxy.newProxyInstance(getClass().getClassLoader(), 
//					bean.getClass().getInterfaces(), new InvocationHandler() {
//				
//				Object target = bean;
//				
//				@Override
//				public Object invoke(Object proxy, Method method, Object[] args)
//						throws Throwable {
//						
//					try {
//						return method.invoke(target, args);
//					} finally {
//						if(method.getName().equals("handleMatchEvent")) {
//							MatchEvent event = (MatchEvent)args[0];
//							handleCompletedEvent(event);
//						}
//					}
//					
//				}
//			});
//		}
		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(Object arg0, String arg1)
			throws BeansException {
		return arg0;
	}

	@Override
	public List<String> getNonCompletedEvents() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getStatus(String ticket) {
		if(events.containsKey(new NewMatchEvent(ticket, "", "", ""))) {
			return "INPROGRESS";
		} else if(completed.containsKey(ticket)) {
			return "COMPLETED";
		} else if(evicted.containsKey(ticket)) {
			return "FAILED";
		}
		throw new RuntimeException();
	}

	
}
