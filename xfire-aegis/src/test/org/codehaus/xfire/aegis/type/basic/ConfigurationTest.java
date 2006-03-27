package org.codehaus.xfire.aegis.type.basic;

import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.aegis.type.Configuration;
import org.codehaus.xfire.aegis.type.CustomTypeMapping;
import org.codehaus.xfire.aegis.type.DefaultTypeCreator;
import org.codehaus.xfire.aegis.type.DefaultTypeMappingRegistry;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.XMLTypeCreator;

/**
 * Test cases to test the changing of the Configuration Object
 * 
 * @author adam
 * 
 */
public class ConfigurationTest
    extends AbstractXFireAegisTest
{

    CustomTypeMapping tm;
    Configuration config = null;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        DefaultTypeMappingRegistry reg = new DefaultTypeMappingRegistry();
        config = reg.getConfiguration();
        XMLTypeCreator creator = new XMLTypeCreator();
        creator.setConfiguration(reg.getConfiguration());
        DefaultTypeCreator next = new DefaultTypeCreator();
        next.setConfiguration(reg.getConfiguration());
        creator.setNextCreator(next);
        reg.createDefaultMappings();
        tm = (CustomTypeMapping) reg.getDefaultTypeMapping();
        tm.setTypeCreator(creator);
    }
    
    public void testNillableDefaultTrue() throws Exception
    {
        config.setDefaultNillable( true );
        tm.setEncodingStyleURI("urn:xfire:bean-nillable");

        Type type = tm.getTypeCreator().createType(MyBean.class);
        BeanTypeInfo info = ((BeanType) type).getTypeInfo();

        assertTrue(info.isNillable(new QName(info.getDefaultNamespace(), "prop2")));
    }
    
    public void testNillableDefaultFalse() throws Exception
    {
        config.setDefaultNillable( false );
        tm.setEncodingStyleURI("urn:xfire:bean-nillable");

        Type type = tm.getTypeCreator().createType(MyBean.class);
        BeanTypeInfo info = ((BeanType) type).getTypeInfo();

        assertFalse(info.isNillable(new QName(info.getDefaultNamespace(), "prop2")));
    }
    
    public void testMinOccursDefault0() throws Exception
    {
        config.setDefaultMinOccurs( 0 );
        tm.setEncodingStyleURI("urn:xfire:bean-minoccurs");

        Type type = tm.getTypeCreator().createType(MyBean.class);
        BeanTypeInfo info = ((BeanType) type).getTypeInfo();

        assertEquals(info.getMinOccurs(new QName(info.getDefaultNamespace(), "prop2")), 0);
    }
    
    public void testMinOccursDefault1() throws Exception
    {
        config.setDefaultMinOccurs( 1 );
        tm.setEncodingStyleURI("urn:xfire:bean-minoccurs");

        Type type = tm.getTypeCreator().createType(MyBean.class);
        BeanTypeInfo info = ((BeanType) type).getTypeInfo();

        assertEquals(info.getMinOccurs(new QName(info.getDefaultNamespace(), "prop2")), 1);
    }
    
    public void testExtensibleDefaultTrue() throws Exception
    {
        config.setDefaultExtensibleElements( true );
        config.setDefaultExtensibleAttributes( true );
        tm.setEncodingStyleURI("urn:xfire:bean-extensible");
        Type type = tm.getTypeCreator().createType(MyBean.class);
        BeanTypeInfo info = ((BeanType) type).getTypeInfo();
        assertTrue(info.isExtensibleElements());
        assertTrue(info.isExtensibleAttributes());
    }
    
    public void testExtensibleDefaultFalse() throws Exception
    {
        config.setDefaultExtensibleElements( false );
        config.setDefaultExtensibleAttributes( false );
        tm.setEncodingStyleURI("urn:xfire:bean-extensible");
        Type type = tm.getTypeCreator().createType(MyBean.class);
        BeanTypeInfo info = ((BeanType) type).getTypeInfo();
        assertFalse(info.isExtensibleElements());
        assertFalse(info.isExtensibleAttributes());
    }

}