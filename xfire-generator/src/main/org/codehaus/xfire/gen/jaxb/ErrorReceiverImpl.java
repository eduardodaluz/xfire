package org.codehaus.xfire.gen.jaxb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXParseException;

import com.sun.tools.xjc.api.ErrorListener;

public class ErrorReceiverImpl
    implements ErrorListener
{
    private static final Log log = LogFactory.getLog(ErrorReceiverImpl.class);
    private boolean fatalErrors;
    
    public void error(SAXParseException e)
    {
        fatalErrors = true;
        log.error(e.getMessage() + " at " + e.getLineNumber() 
                  + "," + e.getColumnNumber() + " in " + e.getSystemId());
    }

    public void fatalError(SAXParseException e)
    {
        fatalErrors = true;
        log.fatal("Fatal error generating JAXB classes.", e);
    }

    public void warning(SAXParseException e)
    {
        log.warn(e.getMessage() + " at " + e.getLineNumber() 
                  + "," + e.getColumnNumber() + " in " + e.getSystemId());
    }

    public void info(SAXParseException e)
    {
        log.info(e.getMessage() + " at " + e.getLineNumber() 
                  + "," + e.getColumnNumber() + " in " + e.getSystemId());
    }

    public boolean hasFatalErrors()
    {
        return fatalErrors;
    }
    
}
