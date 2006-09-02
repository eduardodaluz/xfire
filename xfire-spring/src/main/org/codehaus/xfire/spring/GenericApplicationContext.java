package org.codehaus.xfire.spring;

import org.apache.xbean.spring.context.SpringApplicationContext;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

class GenericApplicationContext extends org.springframework.context.support.GenericApplicationContext
    implements SpringApplicationContext
{

    public GenericApplicationContext()
    {
        super();
    }

    public GenericApplicationContext(ApplicationContext arg0)
    {
        super(arg0);
    }

    public GenericApplicationContext(DefaultListableBeanFactory arg0, ApplicationContext arg1)
    {
        super(arg0, arg1);
    }

    public GenericApplicationContext(DefaultListableBeanFactory arg0)
    {
        super(arg0);
    }
}
