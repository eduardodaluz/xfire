package org.codehaus.xfire.handler;

/**
 * @author Arjen Poutsma
 */

import java.util.Iterator;

import junit.framework.TestCase;

public class HandlerPipelineTest
        extends TestCase
{
    private HandlerPipeline handlerPipeline;
    private Handler handler;

    protected void setUp()
            throws Exception
    {
        handlerPipeline = new HandlerPipeline();
        handler = new AsyncHandler();
        handlerPipeline.addHandler(handler);
    }


    public void testIterator()
            throws Exception
    {
        Iterator iterator = handlerPipeline.iterator();
        assertNotNull(iterator);
        Object o = iterator.next();
        assertNotNull(o);
    }

    public void testRemove()
            throws Exception
    {
        handlerPipeline.remove(handler);
    }
}