package org.codehaus.xfire.annotations.commons;

import org.codehaus.xfire.annotations.WebAnnotations;
import org.codehaus.xfire.annotations.WebAnnotationsTestBase;

public class CommonsWebAttributesTest
        extends WebAnnotationsTestBase
{


    protected WebAnnotations getWebAnnotations()
    {
        return new CommonsWebAttributes();
    }

    protected Class getEchoServiceClass()
    {
        return CommonsEchoService.class;
    }
}