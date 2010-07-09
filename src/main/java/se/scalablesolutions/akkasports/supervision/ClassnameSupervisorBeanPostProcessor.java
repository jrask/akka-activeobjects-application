package se.scalablesolutions.akkasports.supervision;

import static se.scalablesolutions.akka.actor.ActiveObject.faultHandler;
import static se.scalablesolutions.akka.actor.ActiveObject.link;
import static se.scalablesolutions.akka.actor.ActiveObject.newInstance;
import static se.scalablesolutions.akka.actor.ActiveObject.trapExit;

import java.util.List;

import javax.annotation.PreDestroy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

import se.scalablesolutions.akka.actor.ActiveObject;
import se.scalablesolutions.akka.config.OneForOneStrategy;
import se.scalablesolutions.akka.spring.ActiveObjectFactoryBean;

/**
 * <p>This class links all beans assignable from a specific class or interface
 * with a supervisor</p>
 * 
 * 
 * @author johanrask
 *
 */
public class ClassnameSupervisorBeanPostProcessor implements BeanPostProcessor {

	
	private List<Class<?>> classesToBeSupervised;
	
	private Object supervisor;
	
	
	public ClassnameSupervisorBeanPostProcessor(List<Class<?>> classesToBeSupervised) {

		supervisor = newInstance(MySupervisor.class,2000);
		this.classesToBeSupervised = classesToBeSupervised;
		System.out.println("Adding parsers to supervise");
		trapExit(supervisor,  new Class[]{Throwable.class});
		faultHandler(supervisor, new OneForOneStrategy(3, 2000));
	}

	public static class MySupervisor {}

	@Override
	public Object postProcessAfterInitialization(Object actor, String name)
			throws BeansException {
		System.out.println(actor.getClass());
		for(Class<?> clazz: classesToBeSupervised) {
			linkWithSupervisor(clazz, actor);
		}
		return actor;
	}

	
	private void linkWithSupervisor(Class<?> clazz, Object actor) {
		if(clazz.isAssignableFrom(actor.getClass())) {
			if(actor instanceof ActiveObjectFactoryBean) {
//				try {
//					actor = ((FactoryBean<?>)actor).getObject();
//				} catch (Exception e) {
//					throw new RuntimeException(e);
//				}
			}
			System.out.println("**** Linking supervisor with actor " + actor.getClass());
			link(supervisor, actor);	
		}
	}
	
	@Override
	public Object postProcessBeforeInitialization(Object actor, String name)
			throws BeansException {
		return actor;
	}
	
}
