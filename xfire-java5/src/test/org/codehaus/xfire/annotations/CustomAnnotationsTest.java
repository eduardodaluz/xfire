package org.codehaus.xfire.annotations;

import java.util.List;
import java.util.Map;

import org.codehaus.xfire.annotations.jsr181.Jsr181WebAnnotations;
import org.codehaus.xfire.soap.SoapConstants;

import junit.framework.TestCase;

/**
 * @author tomeks
 *
 */
public class CustomAnnotationsTest extends TestCase {
	public void testAnnotation() throws Exception {
		Jsr181WebAnnotations annotations = new Jsr181WebAnnotations();
		Map properties = annotations
				.getServiceProperties(CustomAnnotations.class);
		
		assertTrue(properties.get(SoapConstants.MTOM_ENABLED)!=null);
		assertTrue(properties.get("key1")!=null);
		assertTrue(properties.get("singleKey")!=null);
		assertTrue(properties.get("listKey")!=null);
		assertTrue(properties.get("listKey") instanceof List);
	}
	
	
}
