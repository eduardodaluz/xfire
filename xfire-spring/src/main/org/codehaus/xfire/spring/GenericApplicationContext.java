package org.codehaus.xfire.spring;

import org.apache.xbean.spring.context.SpringApplicationContext;
import org.springframework.context.ApplicationContext;

class GenericApplicationContext 
    extends org.springframework.context.support.GenericApplicationContext 
    implements SpringApplicationContext
{

    public GenericApplicationContext(ApplicationContext parent)
    {
        super(parent);
    }

}
