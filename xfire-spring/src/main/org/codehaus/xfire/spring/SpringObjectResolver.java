package org.codehaus.xfire.spring;

import org.codehaus.xfire.service.DefaultObjectResolver;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class SpringObjectResolver extends DefaultObjectResolver implements
		ApplicationContextAware {
	private ApplicationContext ctx;

	public Object resolve(String id) {
		if (id.startsWith("#")) {
			return springResolve(id);
		}

		return super.resolve(id);
	}

	protected Object springResolve(String id) {

		id = id.substring(1);
		return ctx.getBean(id);
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		ctx = applicationContext;

	}

}
