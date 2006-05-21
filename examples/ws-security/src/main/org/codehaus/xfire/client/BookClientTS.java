package org.codehaus.xfire.client;

import java.net.MalformedURLException;
import java.util.Properties;

import org.apache.ws.security.handler.WSHandlerConstants;

/**
 * <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 * Timestamp example : Adds timestamp to message. If message will be received after specified period of time, the message 
 * will be rejected.
 */
public class BookClientTS
    extends BookClient{

	 //  START SNIPPET: timestamp
    protected void configureOutProperties(Properties properties)
    {
        properties.setProperty(WSHandlerConstants.ACTION,WSHandlerConstants.TIMESTAMP);
        // How long ( in seconds ) message is valid since send.
        properties.setProperty(WSHandlerConstants.TTL_TIMESTAMP,"15");
        // if you want to use millisecond precision use this
        //properties.setProperty(WSHandlerConstants.TIMESTAMP_PRECISION,"true");
    }
	 //  END SNIPPET: timestamp    
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
