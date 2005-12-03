package org.codehaus.xfire.gen.jaxb;

import org.xml.sax.SAXParseException;

import com.sun.tools.xjc.api.ErrorListener;

public class ErrorReceiverImpl
    implements ErrorListener
{

    public void error(SAXParseException e)
    {
        e.printStackTrace();
    }

    public void fatalError(SAXParseException e)
    {
        e.printStackTrace();
    }

    public void warning(SAXParseException e)
    {
        e.printStackTrace();
    }

    public void info(SAXParseException e)
    {
        e.printStackTrace();
    }
}
