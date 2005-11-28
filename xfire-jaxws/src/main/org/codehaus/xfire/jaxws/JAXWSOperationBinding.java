package org.codehaus.xfire.jaxws;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlType;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.aegis.stax.ElementReader;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.MessageSerializer;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.util.ClassLoaderUtils;

public class JAXWSOperationBinding
    implements MessageSerializer
{
    private MessageSerializer delegate;

    private boolean processInput = false;
    private List inputPDs = new ArrayList();
    private Class inputClass;
    
    private boolean processOutput = false;
    private List outputPDs = new ArrayList();
    private Class outputClass;
    
    public JAXWSOperationBinding(OperationInfo op, MessageSerializer delegate)
    {
        this.delegate = delegate;
        
        if (op.getMethod().isAnnotationPresent(RequestWrapper.class))
        {
            this.processInput = true;
            RequestWrapper wrapper = op.getMethod().getAnnotation(RequestWrapper.class);
            
            try
            {
                inputClass = ClassLoaderUtils.loadClass(wrapper.className(), getClass());
                String[] inputOrder = ((XmlType) inputClass.getAnnotation(XmlType.class)).propOrder();
                BeanInfo inputBeanInfo = Introspector.getBeanInfo(inputClass);
                
                PropertyDescriptor[] pds = inputBeanInfo.getPropertyDescriptors();
                for (int i = 0; i < inputOrder.length; i++)
                {
                    inputPDs.add(getPropertyDescriptor(pds, inputOrder[i]));
                }
            }
            catch (ClassNotFoundException e)
            {
                throw new XFireRuntimeException("Could not load request class for operation " + op.getName(), e);
            }
            catch (IntrospectionException e)
            {
                throw new XFireRuntimeException("Could introspect request class for operation " + op.getName(), e);
            }
        }
        
        if (op.getMethod().isAnnotationPresent(ResponseWrapper.class))
        {
            this.processOutput = true;
            ResponseWrapper wrapper = op.getMethod().getAnnotation(ResponseWrapper.class);
            
            try
            {
                outputClass = ClassLoaderUtils.loadClass(wrapper.className(), getClass());
                String[] outputOrder = ((XmlType) outputClass.getAnnotation(XmlType.class)).propOrder();
                BeanInfo outputBeanInfo = Introspector.getBeanInfo(outputClass);
                
                PropertyDescriptor[] pds = outputBeanInfo.getPropertyDescriptors();
                for (int i = 0; i < outputOrder.length; i++)
                {
                    outputPDs.add(getPropertyDescriptor(pds, outputOrder[i]));
                }
            }
            catch (ClassNotFoundException e)
            {
                throw new XFireRuntimeException("Could not load response class for operation " + op.getName(), e);
            }
            catch (IntrospectionException e)
            {
                throw new XFireRuntimeException("Could introspect response class for operation " + op.getName(), e);
            }
        }
    }

    protected PropertyDescriptor getPropertyDescriptor(PropertyDescriptor[] descriptors, String name)
    {
        for (int i = 0; i < descriptors.length; i++)
        {
            if (descriptors[i].getName().equals(name))
                return descriptors[i];
        }
        
        return null;
    }
    
    public void readMessage(InMessage message, MessageContext context)
        throws XFireFault
    {
        if (processInput)
        {
            Service service = context.getService();
            AegisBindingProvider provider = (AegisBindingProvider) service.getBindingProvider();
            
            Type type = provider.getType(service, inputClass);

            Object in = type.readObject(new ElementReader(message.getXMLStreamReader()), context);
            
            List<Object> parameters = new ArrayList<Object>();
            
            for (Iterator itr = inputPDs.iterator(); itr.hasNext();)
            {
                PropertyDescriptor pd = (PropertyDescriptor) itr.next();
                
                try
                {
                    Object val = pd.getReadMethod().invoke(in, new Object[] {});
                    parameters.add(val);
                }
                catch (Exception e)
                {
                    throw new XFireRuntimeException("Couldn't read property " + pd.getName(), e);
                }
            }
            
            message.setBody(parameters);
        }
        else
        {
            delegate.readMessage(message, context);
        }
    }

    public void writeMessage(OutMessage message, XMLStreamWriter writer, MessageContext context)
        throws XFireFault
    {
        if (processOutput)
        {
            List<Object> params = (List<Object>) message.getBody();
            
            Service service = context.getService();
            AegisBindingProvider provider = (AegisBindingProvider) service.getBindingProvider();
            
            Type type = provider.getType(service, inputClass);

            Object in;
            try
            {
                in = outputClass.newInstance();
            }
            catch (Exception e)
            {
                throw new XFireRuntimeException("Could not instantiate resposne class " + outputClass.getName(), e);
            }
            
            for (int i = 0; i < outputPDs.size(); i++)
            {
                PropertyDescriptor pd = (PropertyDescriptor) outputPDs.get(i);
                Object val = params.get(i);
                
                if (val == null) continue;
                
                try
                {
                    pd.getWriteMethod().invoke(in, new Object[] {val});
                }
                catch (Exception e)
                {
                    throw new XFireRuntimeException("Couldn't read property " + pd.getName(), e);
                }
            }
        }
        else
        {
            delegate.writeMessage(message, writer, context);
        }
    }
}
