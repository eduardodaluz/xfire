package org.codehaus.xfire.type;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.codehaus.xfire.aegis.type.CustomTypeMapping;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.basic.StringType;


/**
 * CustomTypeMappingTest
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class CustomTypeMappingTest
    extends TestCase
{
    public void testTM() throws Exception
    {
        CustomTypeMapping mapping = new CustomTypeMapping();
        
        QName qname = new QName( "urn:soap", "String" );
        
        mapping.register( String.class, qname, new StringType() );
        
        assertTrue( mapping.isRegistered( String.class ) );
        assertTrue( mapping.isRegistered( qname ) );
        
        Type type = mapping.getType( String.class );
        assertNotNull( type );
        assertNotNull( mapping.getType( qname ) );

        CustomTypeMapping child = new CustomTypeMapping( mapping );
        
        assertTrue( child.isRegistered( String.class ) );
        assertTrue( child.isRegistered( qname ) );

        assertNotNull( child.getType( String.class ) );
        
        child.removeType(type);
        assertFalse( mapping.isRegistered( String.class ) );
        assertFalse( mapping.isRegistered( qname ) );
    }
}
