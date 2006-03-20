package org.codehaus.xfire.client;

import java.net.MalformedURLException;
import java.util.Properties;

import org.apache.ws.security.handler.WSHandlerConstants;

/**
 * <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 * Timestamp example
 */
public class BookClientTS
    extends BookClient{

    protected void configureProperties(Properties properties)
    {
        properties.setProperty(WSHandlerConstants.ACTION,WSHandlerConstants.TIMESTAMP);
        properties.setProperty(WSHandlerConstants.TTL_TIMESTAMP,"15");
        
    }
    
    /**
     * @param args
     * @throws MalformedURLException
     */
    public static void main(String[] args)
        throws MalformedURLException
    {
        
        new BookClientTS().executeClient("BookServiceTS");

    }

    protected String getName()
    {
        
        return "TimeStamp Client";
    }

    
}
