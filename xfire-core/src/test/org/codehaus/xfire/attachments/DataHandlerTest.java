package org.codehaus.xfire.attachments;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import org.codehaus.xfire.test.AbstractXFireTest;

/**
 * @author <a href="mailto:dan@envoisolutiosn.com">Dan Diephouse</a>
 */
public class DataHandlerTest
    extends AbstractXFireTest
{
    public void testText()
        throws Exception
    {
        DataSource ds = new FileDataSource(
            getTestFile("src/test/org/codehaus/xfire/attachments/test.txt"));

        DataHandler handler = new DataHandler(ds);

        Object content = handler.getContent();
        assertNotNull(content);
        assertTrue(content instanceof String);
        assertEquals("bleh", content);
    }
    
    public void testImages()
        throws Exception
    {
        DataSource ds = new FileDataSource(
            getTestFile("src/test/org/codehaus/xfire/attachments/xfire_logo.jpg"));
    
        DefaultDataContentHandlerFactory factory = new DefaultDataContentHandlerFactory();

        Object content = factory.createDataContentHandler("image/jpeg").getContent(ds);
        assertNotNull(content);
        assertTrue(content instanceof java.awt.Image);
        
        ds = new FileDataSource(getTestFile("src/test/org/codehaus/xfire/attachments/fax.tif"));
        assertNotNull(ds);
        
        content = factory.createDataContentHandler("image/tiff").getContent(ds);
        assertNotNull(content);
        assertTrue(content instanceof java.awt.Image);
    }
}
