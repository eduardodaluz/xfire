package org.codehaus.xfire.wsdl;

import javax.wsdl.WSDLException;

import org.codehaus.xfire.service.Service;

/**
 * WSDLBuilder
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface WSDLBuilder
{
    public final static String ROLE = WSDLBuilder.class.getName();
   
    public WSDLWriter createWSDLWriter( Service service )
    	throws WSDLException;
}