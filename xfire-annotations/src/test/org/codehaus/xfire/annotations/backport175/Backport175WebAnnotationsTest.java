package org.codehaus.xfire.annotations.backport175;

import org.codehaus.xfire.annotations.WebAnnotations;
import org.codehaus.xfire.annotations.WebAnnotationsTestBase;

public class Backport175WebAnnotationsTest
        extends WebAnnotationsTestBase
{

    protected WebAnnotations getWebAnnotations()
    {
        return new Backport175WebAnnotations();
    }

    protected Class getEchoServiceClass()
    {
        return Backport175EchoService.class;
    }

    public void testHasWebParamAnnotation() throws Exception
    {
        // overriden to make sure the test succeeds
    }

    public void testGetWebParamAnnotation() throws Exception
    {
        // overriden to make sure the test succeeds
    }
}