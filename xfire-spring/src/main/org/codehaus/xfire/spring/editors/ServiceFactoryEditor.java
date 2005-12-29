package org.codehaus.xfire.spring.editors;

import java.beans.PropertyEditorSupport;

import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.spring.config.ServiceFactoryBean;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class ServiceFactoryEditor
    extends PropertyEditorSupport
{

    public void setAsText(String text)
        throws IllegalArgumentException
    {
        ServiceFactoryBean factoryBean = new ServiceFactoryBean(text);
        ServiceFactory factory;
        try
        {
            factory = (ServiceFactory) factoryBean.getObject();
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException(e);
        }
        setValue(factory);
    }

}
