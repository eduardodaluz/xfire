/** 
 * 
 * Copyright 2004 Protique Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 * 
 **/
package org.codehaus.xfire.xmlbeans;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.xmlbeans.XmlObject;
import org.codehaus.xfire.AbstractXFireTest;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.service.SimpleService;

/**
 * @version $Revision$
 */
public class XMLBeansTest 
    extends AbstractXFireTest
{
    public void testRequestResponse() throws Exception
    {}/*
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        MessageContext context = 
            new MessageContext( "Test",
                                null,
                                out,
                                null,
                                null );
        
        XMLBeansSoapHandler handler = getXMLBeansHandler();
        
        handler.invoke(context, 
            createXMLStreamReader(getClass().getResource("sampleRequest.xml" )));
        
        XmlObject xmlObject = handler.getObject();

        assertTrue( "should have received an object: " + xmlObject,
                    xmlObject != null );
        
        System.out.println( "Parsed: " + xmlObject );
        System.out.println( "Response: " + out.toString() );
        
        // is there an easy way to test if the 
        // response is equal to the request, ignoring whitespace et al?
        assertTrue( out.toString().length() > 0 );
    }*/

    /**
     * Test the handler using the XFire service mechanism.
     * /
    public void testService() throws Exception
    {
        SimpleService service = new SimpleService();
        service.setName("Test");
        
        getServiceRegistry().register( service );

        XMLBeansSoapHandler handler = getXMLBeansHandler();
        service.setServiceHandler( handler );
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessageContext context = 
            new MessageContext( "Test",
                                null,
                                out,
                                null,
                                null );
        getXFire().invoke( 
            getClass().getResourceAsStream("sampleRequest.xml" ),
            context );
        
        XmlObject xmlObject = handler.getObject();
        
        assertTrue( "should have received an object: " + xmlObject,
                    xmlObject != null );

        // is there an easy way to test if the 
        // response is equal to the request, ignoring whitespace et al?
        assertTrue( out.toString().length() > 0 );
    }
    
    public XMLBeansSoapHandler getXMLBeansHandler()
    {
        XMLBeansSoapHandler handler = new XMLBeansSoapHandler()
        {
            protected void handleBody( MessageContext context, XmlObject body ) 
                throws Exception
            {
                reply( context, body );
            }
        };
        
        return handler;
    }
    
    protected XMLStreamReader createXMLStreamReader( URL resource )
            throws XMLStreamException, IOException
    {
        assertTrue( "Found resource", resource != null );
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader in = inputFactory.createXMLStreamReader( resource
                .openStream() );
        
        return in;
    }*/
}