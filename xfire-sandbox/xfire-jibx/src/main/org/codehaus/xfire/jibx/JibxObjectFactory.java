package org.codehaus.xfire.jibx;

import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.transport.TransportManager;

/**
 * <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class JibxObjectFactory extends ObjectServiceFactory {

	public JibxObjectFactory() {
		this(XFireFactory.newInstance().getXFire().getTransportManager());
	}

	public JibxObjectFactory(TransportManager manager) {
		super(manager,new AegisBindingProvider(new JibxTypeRegistry()));
	}

}
