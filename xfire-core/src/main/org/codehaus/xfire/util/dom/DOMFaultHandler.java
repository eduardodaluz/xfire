package org.codehaus.xfire.util.dom;

import org.codehaus.xfire.soap.handler.FaultSoapSerializerHandler;

/**
 * <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a> 
 *
 */
public class DOMFaultHandler extends DOMOutHandler {

	public DOMFaultHandler(){
		super();
		getBefore().add(FaultSoapSerializerHandler.class.getName());
		
	}

}
