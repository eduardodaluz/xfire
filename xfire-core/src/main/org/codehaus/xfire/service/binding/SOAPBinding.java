package org.codehaus.xfire.service.binding;


import javax.xml.namespace.QName;

import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.soap.SoapVersion;

/**
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class SOAPBinding
{
    private SoapVersion version;
    private String style = SoapConstants.STYLE_DOCUMENT;
    private String use = SoapConstants.USE_LITERAL;
    private QName name;

    public SOAPBinding(QName name)
    {
        this(name, Soap11.getInstance());
    }

    public SOAPBinding(QName name, SoapVersion version)
    {
        this.name = name;
        this.version = version;
    }

    public SoapVersion getSoapVersion()
    {
        return version;
    }

    public String getStyle()
    {
        return style;
    }

    public void setStyle(String style)
    {
        this.style = style;
    }

    public String getUse()
    {
        return use;
    }

    public void setUse(String use)
    {
        this.use = use;
    }

    public QName getName()
    {
        return name;
    }

    public void setName(QName name)
    {
        this.name = name;
    }

    public void setSoapVersion(SoapVersion soapVersion)
    {
        this.version = soapVersion;
    }
}

