package org.codehaus.xfire.xmlbeans;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.SoapConstants;

public class AbstractXmlBeansTest
    extends AbstractXFireAegisTest
{
    protected void setUp()
        throws Exception
    {
        super.setUp();
    
        ObjectServiceFactory osf = new ObjectServiceFactory(getXFire().getTransportManager(), 
                                                   new AegisBindingProvider(new XmlBeansTypeRegistry()));
        osf.setStyle(SoapConstants.STYLE_DOCUMENT);
        osf.setWsdlBuilderFactory(new XmlBeansWSDLBuilderFactory());
        
        setServiceFactory(osf);
    }
}
