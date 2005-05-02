package org.codehaus.xfire.annotations;

/**
 * @author Arjen Poutsma
 */

import java.lang.reflect.Method;
import java.util.Collection;
import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.annotations.soap.SOAPBindingAnnotation;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.service.ServiceInfo;
import org.codehaus.xfire.service.binding.SOAPBinding;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilderInfo;
import org.easymock.MockControl;

public class AnnotationServiceFactoryTest
        extends AbstractXFireAegisTest
{
    private AnnotationServiceFactory annotationServiceFactory;
    private MockControl webAnnotationsControl;
    private WebAnnotations webAnnotations;

    public void setUp()
            throws Exception
    {
        super.setUp();

        webAnnotationsControl = MockControl.createControl(WebAnnotations.class);
        webAnnotations = (WebAnnotations) webAnnotationsControl.getMock();
        annotationServiceFactory = new AnnotationServiceFactory(webAnnotations,
                                                                getXFire().getTransportManager(),
                                                                null);
    }

    public void testCreate()
            throws Exception
    {
        webAnnotations.hasSOAPBindingAnnotation(EchoServiceImpl.class);
        webAnnotationsControl.setReturnValue(false);

        webAnnotations.hasWebServiceAnnotation(EchoServiceImpl.class);
        webAnnotationsControl.setReturnValue(true);

        WebServiceAnnotation annotation = new WebServiceAnnotation();
        annotation.setServiceName("EchoService");
        annotation.setTargetNamespace("http://xfire.codehaus.org/EchoService");

        webAnnotations.getWebServiceAnnotation(EchoServiceImpl.class);
        webAnnotationsControl.setReturnValue(annotation);

        Method echoMethod = EchoServiceImpl.class.getMethod("echo", new Class[]{String.class});
        webAnnotations.hasWebMethodAnnotation(echoMethod);
        webAnnotationsControl.setReturnValue(true);

        webAnnotations.hasOnewayAnnotation(echoMethod);
        webAnnotationsControl.setReturnValue(true);

        webAnnotations.hasWebParamAnnotation(echoMethod, 0);
        webAnnotationsControl.setReturnValue(false);

        webAnnotations.hasWebResultAnnotation(echoMethod);
        webAnnotationsControl.setReturnValue(false);

        Method asyncMethod = EchoServiceImpl.class.getMethod("async", new Class[0]);
        webAnnotations.hasWebMethodAnnotation(asyncMethod);
        webAnnotationsControl.setReturnValue(false);

        webAnnotationsControl.replay();

        annotationServiceFactory.create(EchoServiceImpl.class);

        webAnnotationsControl.verify();
    }

    public void testNoWebServiceAnnotation()
    {
        webAnnotations.hasSOAPBindingAnnotation(EchoServiceImpl.class);
        webAnnotationsControl.setReturnValue(false);

        webAnnotations.hasWebServiceAnnotation(EchoServiceImpl.class);
        webAnnotationsControl.setReturnValue(false);
        webAnnotationsControl.replay();

        try
        {
            annotationServiceFactory.create(EchoServiceImpl.class);
            fail("Not a AnnotationException thrown");
        }
        catch (AnnotationException e)
        {
            // expected behavior
        }
    }

    public void testEndpointInterface()
            throws SecurityException, NoSuchMethodException
    {
        webAnnotations.hasSOAPBindingAnnotation(EchoServiceImpl.class);
        webAnnotationsControl.setReturnValue(false);

        webAnnotations.hasWebServiceAnnotation(EchoServiceImpl.class);
        webAnnotationsControl.setReturnValue(true);

        WebServiceAnnotation implAnnotation = new WebServiceAnnotation();
        implAnnotation.setServiceName("EchoService");
        implAnnotation.setTargetNamespace("not used");
        implAnnotation.setEndpointInterface(EchoService.class.getName());

        webAnnotations.getWebServiceAnnotation(EchoServiceImpl.class);
        webAnnotationsControl.setReturnValue(implAnnotation);

        webAnnotations.hasWebServiceAnnotation(EchoService.class);
        webAnnotationsControl.setReturnValue(true);

        WebServiceAnnotation intfAnnotation = new WebServiceAnnotation();
        intfAnnotation.setName("Echo");
        intfAnnotation.setTargetNamespace("http://xfire.codehaus.org/EchoService");
        intfAnnotation.setEndpointInterface(EchoService.class.getName());

        webAnnotations.getWebServiceAnnotation(EchoService.class);
        webAnnotationsControl.setReturnValue(intfAnnotation);

        Method echoMethod = EchoService.class.getMethod("echo", new Class[]{String.class});

        webAnnotations.hasWebParamAnnotation(echoMethod, 0);
        webAnnotationsControl.setReturnValue(false);

        webAnnotations.hasOnewayAnnotation(echoMethod);
        webAnnotationsControl.setReturnValue(false);

        webAnnotations.hasWebResultAnnotation(echoMethod);
        webAnnotationsControl.setReturnValue(false);
        webAnnotationsControl.replay();

        ServiceEndpoint endpoint = annotationServiceFactory.create(EchoServiceImpl.class);
        ServiceInfo service = endpoint.getService();
        assertEquals(new QName("http://xfire.codehaus.org/EchoService", "EchoService"), service.getName());

        WSDLBuilderInfo info = (WSDLBuilderInfo) endpoint.getProperty(WSDLBuilderInfo.KEY);
        assertEquals("Echo", info.getPortType());
        assertEquals("EchoService", info.getServiceName());
        assertEquals("http://xfire.codehaus.org/EchoService", info.getTargetNamespace());

        webAnnotationsControl.verify();
    }

    public void testParameterNameAnnotation()
            throws SecurityException, NoSuchMethodException
    {
        webAnnotations.hasSOAPBindingAnnotation(EchoServiceImpl.class);
        webAnnotationsControl.setReturnValue(false);

        webAnnotations.hasWebServiceAnnotation(EchoServiceImpl.class);
        webAnnotationsControl.setReturnValue(true);

        WebServiceAnnotation implAnnotation = new WebServiceAnnotation();
        implAnnotation.setServiceName("EchoService");
        implAnnotation.setTargetNamespace("http://xfire.codehaus.org/EchoService");

        webAnnotations.getWebServiceAnnotation(EchoServiceImpl.class);
        webAnnotationsControl.setReturnValue(implAnnotation);

        Method echoMethod = EchoServiceImpl.class.getMethod("echo", new Class[]{String.class});
        webAnnotations.hasWebMethodAnnotation(echoMethod);
        webAnnotationsControl.setReturnValue(true);

        webAnnotations.hasOnewayAnnotation(echoMethod);
        webAnnotationsControl.setReturnValue(false);

        WebParamAnnotation paramAnnotation = new WebParamAnnotation();
        paramAnnotation.setName("input");
        webAnnotations.getWebParamAnnotation(echoMethod, 0);
        webAnnotationsControl.setReturnValue(paramAnnotation);

        webAnnotations.hasWebParamAnnotation(echoMethod, 0);
        webAnnotationsControl.setReturnValue(true);

        webAnnotations.hasWebResultAnnotation(echoMethod);
        webAnnotationsControl.setReturnValue(false);

        Method asyncMethod = EchoServiceImpl.class.getMethod("async", new Class[0]);
        webAnnotations.hasWebMethodAnnotation(asyncMethod);
        webAnnotationsControl.setReturnValue(false);

        webAnnotationsControl.replay();

        ServiceEndpoint endpoint = annotationServiceFactory.create(EchoServiceImpl.class);
        ServiceInfo service = endpoint.getService();
        assertEquals(new QName("http://xfire.codehaus.org/EchoService", "EchoService"), service.getName());

        final OperationInfo operation = service.getOperation("echo");
        assertNotNull(operation);

        Collection parts = operation.getInputMessage().getMessageParts();
        assertEquals(1, parts.size());
        assertEquals("input", ((MessagePartInfo) parts.iterator().next()).getName().getLocalPart());

        webAnnotationsControl.verify();
    }

    public void testSOAPBindingAnnotation()
            throws Exception
    {
        webAnnotations.hasSOAPBindingAnnotation(EchoServiceImpl.class);
        webAnnotationsControl.setReturnValue(true);

        SOAPBindingAnnotation soapBinding = new SOAPBindingAnnotation();
        soapBinding.setUse(SOAPBindingAnnotation.USE_LITERAL);

        webAnnotations.getSOAPBindingAnnotation(EchoServiceImpl.class);
        webAnnotationsControl.setReturnValue(soapBinding);

        webAnnotations.hasWebServiceAnnotation(EchoServiceImpl.class);
        webAnnotationsControl.setReturnValue(true);

        WebServiceAnnotation webservice = new WebServiceAnnotation();
        webservice.setServiceName("EchoService");
        webservice.setTargetNamespace("http://xfire.codehaus.org/EchoService");

        webAnnotations.getWebServiceAnnotation(EchoServiceImpl.class);
        webAnnotationsControl.setReturnValue(webservice);

        Method echoMethod = EchoServiceImpl.class.getMethod("echo", new Class[]{String.class});
        webAnnotations.hasWebMethodAnnotation(echoMethod);
        webAnnotationsControl.setReturnValue(false);

        Method asyncMethod = EchoServiceImpl.class.getMethod("async", new Class[0]);
        webAnnotations.hasWebMethodAnnotation(asyncMethod);
        webAnnotationsControl.setReturnValue(false);

        webAnnotationsControl.replay();

        ServiceEndpoint service = service = annotationServiceFactory.create(EchoServiceImpl.class);
        SOAPBinding binding = (SOAPBinding) service.getBinding();
        assertEquals(SoapConstants.USE_LITERAL, binding.getUse());

        webAnnotationsControl.verify();

    }
}