package org.codehaus.xfire.aegis.type;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.codehaus.xfire.AbstractXFireComponent;
import org.codehaus.xfire.aegis.type.basic.Base64Type;
import org.codehaus.xfire.aegis.type.basic.BigDecimalType;
import org.codehaus.xfire.aegis.type.basic.BooleanType;
import org.codehaus.xfire.aegis.type.basic.CalendarType;
import org.codehaus.xfire.aegis.type.basic.DateTimeType;
import org.codehaus.xfire.aegis.type.basic.DoubleType;
import org.codehaus.xfire.aegis.type.basic.FloatType;
import org.codehaus.xfire.aegis.type.basic.IntType;
import org.codehaus.xfire.aegis.type.basic.LongType;
import org.codehaus.xfire.aegis.type.basic.ShortType;
import org.codehaus.xfire.aegis.type.basic.StringType;
import org.codehaus.xfire.aegis.type.basic.TimeType;
import org.codehaus.xfire.aegis.type.basic.TimestampType;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.util.ClassLoaderUtils;

/**
 * The default implementation of TypeMappingRegistry.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 22, 2004
 */
public class DefaultTypeMappingRegistry
        extends AbstractXFireComponent
        implements TypeMappingRegistry
{
    private static final QName XSD_STRING = new QName(SoapConstants.XSD, "string");
    private static final QName XSD_LONG = new QName(SoapConstants.XSD, "long");
    private static final QName XSD_FLOAT = new QName(SoapConstants.XSD, "float");
    private static final QName XSD_DOUBLE = new QName(SoapConstants.XSD, "double");
    private static final QName XSD_INT = new QName(SoapConstants.XSD, "int");
    private static final QName XSD_SHORT = new QName(SoapConstants.XSD, "short");
    private static final QName XSD_BOOLEAN = new QName(SoapConstants.XSD, "boolean");
    private static final QName XSD_DATETIME = new QName(SoapConstants.XSD, "dateTime");
    private static final QName XSD_TIME = new QName(SoapConstants.XSD, "dateTime");
    private static final QName XSD_BASE64 = new QName(SoapConstants.XSD, "base64Binary");
    private static final QName XSD_DECIMAL = new QName(SoapConstants.XSD, "decimal");

    private static final String ENCODED_NS = Soap11.getInstance().getSoapEncodingStyle();
    private static final QName ENCODED_STRING = new QName(ENCODED_NS, "string");
    private static final QName ENCODED_LONG = new QName(ENCODED_NS, "long");
    private static final QName ENCODED_FLOAT = new QName(ENCODED_NS, "float");
    private static final QName ENCODED_DOUBLE = new QName(ENCODED_NS, "double");
    private static final QName ENCODED_INT = new QName(ENCODED_NS, "int");
    private static final QName ENCODED_SHORT = new QName(ENCODED_NS, "short");
    private static final QName ENCODED_BOOLEAN = new QName(ENCODED_NS, "boolean");
    private static final QName ENCODED_DATETIME = new QName(ENCODED_NS, "dateTime");
    private static final QName ENCODED_BASE64 = new QName(ENCODED_NS, "base64Binary");
    private static final QName ENCODED_DECIMAL = new QName(ENCODED_NS, "decimal");

    private Hashtable registry;

    private TypeMapping defaultTM;

    public DefaultTypeMappingRegistry()
    {
        registry = new Hashtable();
    }

    public DefaultTypeMappingRegistry(boolean createDefault)
    {
        registry = new Hashtable();

        if (createDefault)
        {
            createDefaultMappings();
        }
    }

    public TypeMapping register(String encodingStyleURI, TypeMapping mapping)
    {
        TypeMapping previous = (TypeMapping) registry.get(encodingStyleURI);

        mapping.setEncodingStyleURI(encodingStyleURI);

        registry.put(encodingStyleURI, mapping);

        return previous;
    }

    public void registerDefault(TypeMapping mapping)
    {
        defaultTM = mapping;
    }

    /**
     * @see org.codehaus.xfire.aegis.type.TypeMappingRegistry#getDefaultTypeMapping()
     */
    public TypeMapping getDefaultTypeMapping()
    {
        return defaultTM;
    }

    /**
     * @see org.codehaus.xfire.aegis.type.TypeMappingRegistry#getRegisteredEncodingStyleURIs()
     */
    public String[] getRegisteredEncodingStyleURIs()
    {
        return (String[]) registry.keySet().toArray(new String[0]);
    }

    /**
     * @see org.codehaus.xfire.aegis.type.TypeMappingRegistry#getTypeMapping(java.lang.String)
     */
    public TypeMapping getTypeMapping(String encodingStyleURI)
    {
        return (TypeMapping) registry.get(encodingStyleURI);
    }

    /**
     * @see org.codehaus.xfire.aegis.type.TypeMappingRegistry#createTypeMapping(boolean)
     */
    public TypeMapping createTypeMapping(boolean autoTypes)
    {
        return createTypeMapping(getDefaultTypeMapping(), autoTypes);
    }

    /**
     * @see org.codehaus.xfire.aegis.type.TypeMappingRegistry#createTypeMapping(String, boolean)
     */
    public TypeMapping createTypeMapping(String parentNamespace, boolean autoTypes)
    {
        return createTypeMapping(getTypeMapping(parentNamespace), autoTypes);
    }

    protected TypeMapping createTypeMapping(TypeMapping parent, boolean autoTypes)
    {
        CustomTypeMapping tm = new CustomTypeMapping(parent);
        
        if (autoTypes) tm.setTypeCreator(createTypeCreator());
        
        return tm;
    }

    protected TypeCreator createTypeCreator()
    {
        AbstractTypeCreator xmlCreator = new XMLTypeCreator();
        xmlCreator.setNextCreator(new DefaultTypeCreator());
        try
        {
            String j5TC = "org.codehaus.xfire.aegis.type.java5.Java5TypeCreator";

            Class clazz = ClassLoaderUtils.loadClass(j5TC, getClass());
            
            AbstractTypeCreator j5Creator = (AbstractTypeCreator) clazz.newInstance();
            j5Creator.setNextCreator(xmlCreator);
            return j5Creator;
        }
        catch (Exception e)
        {
            return xmlCreator;
        }
    }

    /**
     * @see org.codehaus.xfire.aegis.type.TypeMappingRegistry#unregisterTypeMapping(java.lang.String)
     */
    public TypeMapping unregisterTypeMapping(String encodingStyleURI)
    {
        TypeMapping tm = (TypeMapping) registry.get(encodingStyleURI);
        registry.remove(encodingStyleURI);
        return tm;
    }

    public boolean removeTypeMapping(TypeMapping mapping)
    {
        int n = 0;

        for (Iterator itr = registry.values().iterator(); itr.hasNext();)
        {
            if (itr.next().equals(mapping))
            {
                itr.remove();
                n++;
            }
        }

        return (n > 0);
    }

    /**
     * @see org.codehaus.xfire.aegis.type.TypeMappingRegistry#clear()
     */
    public void clear()
    {
        registry.clear();
    }

    public TypeMapping createDefaultMappings()
    {
        TypeMapping tm = createTypeMapping(false);

        tm.register(boolean.class, XSD_BOOLEAN, new BooleanType());
        tm.register(int.class, XSD_INT, new IntType());
        tm.register(short.class, XSD_SHORT, new ShortType());
        tm.register(double.class, XSD_DOUBLE, new DoubleType());
        tm.register(float.class, XSD_FLOAT, new FloatType());
        tm.register(long.class, XSD_LONG, new LongType());
        tm.register(String.class, XSD_STRING, new StringType());
        tm.register(Boolean.class, XSD_BOOLEAN, new BooleanType());
        tm.register(Integer.class, XSD_INT, new IntType());
        tm.register(Short.class, XSD_SHORT, new ShortType());
        tm.register(Double.class, XSD_DOUBLE, new DoubleType());
        tm.register(Float.class, XSD_FLOAT, new FloatType());
        tm.register(Long.class, XSD_LONG, new LongType());
        tm.register(Date.class, XSD_DATETIME, new DateTimeType());
        tm.register(Time.class, XSD_TIME, new TimeType());
        tm.register(Timestamp.class, XSD_DATETIME, new TimestampType());
        tm.register(Calendar.class, XSD_DATETIME, new CalendarType());
        tm.register(byte[].class, XSD_BASE64, new Base64Type());
        tm.register(BigDecimal.class, XSD_DECIMAL, new BigDecimalType());

        register(SoapConstants.XSD, tm);
        registerDefault(tm);

        // Create a Type Mapping for SOAP 1.1 Encoding
        TypeMapping soapTM = createTypeMapping(tm, false);

        soapTM.register(boolean.class, ENCODED_BOOLEAN, new BooleanType());
        soapTM.register(int.class, ENCODED_INT, new IntType());
        soapTM.register(short.class, ENCODED_SHORT, new ShortType());
        soapTM.register(double.class, ENCODED_DOUBLE, new DoubleType());
        soapTM.register(float.class, ENCODED_FLOAT, new FloatType());
        soapTM.register(long.class, ENCODED_LONG, new LongType());
        soapTM.register(String.class, ENCODED_STRING, new StringType());
        soapTM.register(Boolean.class, ENCODED_BOOLEAN, new BooleanType());
        soapTM.register(Integer.class, ENCODED_INT, new IntType());
        soapTM.register(Short.class, ENCODED_SHORT, new ShortType());
        soapTM.register(Double.class, ENCODED_DOUBLE, new DoubleType());
        soapTM.register(Float.class, ENCODED_FLOAT, new FloatType());
        soapTM.register(Long.class, ENCODED_LONG, new LongType());
        soapTM.register(Date.class, ENCODED_DATETIME, new DateTimeType());
        soapTM.register(Calendar.class, ENCODED_DATETIME, new CalendarType());
        soapTM.register(byte[].class, ENCODED_BASE64, new Base64Type());
        soapTM.register(BigDecimal.class, ENCODED_DECIMAL, new BigDecimalType());

        soapTM.register(boolean.class, XSD_BOOLEAN, new BooleanType());
        soapTM.register(int.class, XSD_INT, new IntType());
        soapTM.register(short.class, XSD_SHORT, new ShortType());
        soapTM.register(double.class, XSD_DOUBLE, new DoubleType());
        soapTM.register(float.class, XSD_FLOAT, new FloatType());
        soapTM.register(long.class, XSD_LONG, new LongType());
        soapTM.register(String.class, XSD_STRING, new StringType());
        soapTM.register(Boolean.class, XSD_BOOLEAN, new BooleanType());
        soapTM.register(Integer.class, XSD_INT, new IntType());
        soapTM.register(Short.class, XSD_SHORT, new ShortType());
        soapTM.register(Double.class, XSD_DOUBLE, new DoubleType());
        soapTM.register(Float.class, XSD_FLOAT, new FloatType());
        soapTM.register(Long.class, XSD_LONG, new LongType());
        soapTM.register(Date.class, XSD_DATETIME, new DateTimeType());
        soapTM.register(Time.class, XSD_TIME, new TimeType());
        soapTM.register(Timestamp.class, XSD_DATETIME, new TimestampType());
        soapTM.register(Calendar.class, XSD_DATETIME, new CalendarType());
        soapTM.register(byte[].class, XSD_BASE64, new Base64Type());
        soapTM.register(BigDecimal.class, XSD_DECIMAL, new BigDecimalType());

        register(ENCODED_NS, tm);

        return tm;
    }
}
