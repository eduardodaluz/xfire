package org.codehaus.xfire.spring.editors;

import java.beans.PropertyEditorSupport;

import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.soap.SoapVersion;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class SoapVersionPropertyEditor
    extends PropertyEditorSupport
{

    /* (non-Javadoc)
     * @see java.beans.PropertyEditor#setAsText(java.lang.String)
     */
    public void setAsText(String soapVersionValue)
        throws IllegalArgumentException
    {
        SoapVersion soapVersion;
        if (soapVersionValue.equals("1.2"))
        {
            soapVersion = Soap12.getInstance();
        }
        else
        {
            soapVersion = Soap11.getInstance();
        }
        setValue(soapVersion);
    }

}
