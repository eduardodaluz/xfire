package org.codehaus.xfire.aegis.type.interfaceMapping;

public class InterfaceTestService implements InterfaceService {

	public BeanImpl getBean(){
		return new BeanImpl();
	}
}
