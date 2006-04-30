package org.codehaus.xfire.jibx;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.stax.ElementReader;
import org.codehaus.xfire.aegis.stax.ElementWriter;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.util.stax.FragmentStreamReader;
import org.jdom.Element;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.StAXReaderWrapper;
import org.jibx.runtime.impl.StAXWriter;
import org.jibx.runtime.impl.UnmarshallingContext;

/**
 * <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class JibxType
    extends Type
{
    
    private IBindingFactory bfact;

    public boolean isComplex()
    {
        return true;
    }

    /**
     * @param clazz
     */
    public JibxType(Class clazz)
    {
        setTypeClass(clazz);
        setWriteOuter(false);
        setAbstract(false);

        try
        {
            bfact = BindingDirectory.getFactory(getTypeClass());
            String classes[] = bfact.getMappedClasses();
            String names[] = bfact.getElementNames();
            String ns[] = bfact.getElementNamespaces();
            int index = 0;
            String name = clazz.getName();
            while (!name.equals(classes[index]))
            {
                index++;
            }
            if (ns[index] == null)
            {
                setSchemaType(new QName("http://" + name, names[index]));
            }
            else
            {
                setSchemaType(new QName(ns[index], names[index]));
            }

        }
        catch (JiBXException e)
        {
            throw new XFireRuntimeException(e.getMessage());
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.xfire.aegis.type.Type#readObject(org.codehaus.xfire.aegis.MessageReader,
     *      org.codehaus.xfire.MessageContext)
     */
    public Object readObject(MessageReader reader, MessageContext context)
        throws XFireFault
    {
        try
        {
            IUnmarshallingContext mctx = bfact.createUnmarshallingContext();
            ElementReader r = (ElementReader) reader;
            FragmentStreamReader fsr = new FragmentStreamReader(r.getXMLStreamReader());
            fsr.next();
            StAXReaderWrapper wrapper = new StAXReaderWrapper(
                    fsr,
                    getSchemaType().getLocalPart(), 
                    true);

            UnmarshallingContext ctx = (UnmarshallingContext) mctx;
            ctx.setDocument(wrapper);
            Object obj = mctx.unmarshalElement();
            return obj;
        }
        catch (JiBXException e)
        {
            throw new XFireRuntimeException("Could not read Jibx type.", e);
        }
        catch (XMLStreamException e)
        {
            throw new XFireRuntimeException("Could not read Jibx type.", e);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.xfire.aegis.type.Type#writeObject(java.lang.Object,
     *      org.codehaus.xfire.aegis.MessageWriter,
     *      org.codehaus.xfire.MessageContext)
     */
    public void writeObject(Object object, MessageWriter writer, MessageContext context)
        throws XFireFault
    {
        try
        {
            IMarshallingContext mctx = bfact.createMarshallingContext();

            XMLStreamWriter noCloseWriter = new NoCloseXMLStreamWriter(((ElementWriter) writer)
                    .getXMLStreamWriter());
            mctx.setXmlWriter(new StAXWriter(bfact.getNamespaces(), noCloseWriter));

            mctx.marshalDocument(object);
        }
        catch (JiBXException e)
        {
            throw new XFireRuntimeException("Could not write Jibx type.", e);
        }
    }

    public void writeSchema(Element arg0)
    {
        
    }

    
}
