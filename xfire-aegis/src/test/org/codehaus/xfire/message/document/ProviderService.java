package org.codehaus.xfire.message.document;

import javax.xml.transform.Source;

import org.codehaus.xfire.fault.XFireFault;

public class ProviderService
{
    public Source invoke(Source source) throws XFireFault
    {
        if (source == null) 
            throw new XFireFault("Invalid source.", XFireFault.SENDER);
        
        return source;
    }
}
