package org.codehaus.xfire.gen.jaxb;

import org.xml.sax.SAXParseException;
import com.sun.tools.xjc.api.ErrorListener;

public class DelegatingErrorReceiverImpl extends ErrorReceiverImpl
{    
    private ErrorListener delegate;
    
    public DelegatingErrorReceiverImpl(ErrorListener delegate) {
        this.delegate = delegate;
    }
    
    public void error(SAXParseException e)
    {
        if (delegate != null) {
            fatalErrors = true;
            delegate.error(e);
        } 
        else {
            super.error(e);
        }
    }

    public void fatalError(SAXParseException e)
    {
        if (delegate != null) 
        {
            fatalErrors = true;
            delegate.fatalError(e);
        } 
        else {
            super.fatalError(e);
        }
    }

    public void warning(SAXParseException e)
    {
        if (delegate != null)
        {
            delegate.warning(e);
        }
        else {
            super.warning(e);
        }
    }

    public void info(SAXParseException e)
    {
        if (delegate != null)
        {
            delegate.info(e);
        }
        else {
            super.info(e);
        }
    }
    
}
