package org.codehaus.xfire.jaxws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import org.oasis_open.docs.wsn._2004._06.wsn_ws_basenotification_1_2_draft_01.ResourceUnknownFaultType;
import org.oasis_open.docs.wsn._2004._06.wsn_ws_basenotification_1_2_draft_01.TopicExpressionType;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_basefaults_1_2_draft_01.BaseFaultType.ErrorCode;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.QueryExpressionType;
import org.xmlsoap.schemas.ws._2003._03.addressing.EndpointReferenceType;

@WebService
public class SubscribeServiceEx {

    @WebMethod(action = "http://servicemix.org/wspojo/notification/Subscribe", operationName = "Subscribe")
    @WebResult(name = "SubscriptionReference", targetNamespace = "http://docs.oasis-open.org/wsn/2004/06/wsn-WS-BaseNotification-1.2-draft-01.xsd")
    @RequestWrapper(className = "org.oasis_open.docs.wsn._2004._06.wsn_ws_basenotification_1_2_draft_01.Subscribe", localName = "Subscribe", targetNamespace = "http://docs.oasis-open.org/wsn/2004/06/wsn-WS-BaseNotification-1.2-draft-01.xsd")
    @ResponseWrapper(className = "org.oasis_open.docs.wsn._2004._06.wsn_ws_basenotification_1_2_draft_01.SubscribeResponse", localName = "SubscribeResponse", targetNamespace = "http://docs.oasis-open.org/wsn/2004/06/wsn-WS-BaseNotification-1.2-draft-01.xsd")
    public EndpointReferenceType subscribe(
            @WebParam(name = "ConsumerReference", targetNamespace = "http://docs.oasis-open.org/wsn/2004/06/wsn-WS-BaseNotification-1.2-draft-01.xsd")
            EndpointReferenceType consumerReference,
            @WebParam(name = "TopicExpression", targetNamespace = "http://docs.oasis-open.org/wsn/2004/06/wsn-WS-BaseNotification-1.2-draft-01.xsd")
            TopicExpressionType topicExpression,
            @WebParam(name = "UseNotify", targetNamespace = "http://docs.oasis-open.org/wsn/2004/06/wsn-WS-BaseNotification-1.2-draft-01.xsd")
            Boolean useNotify,
            @WebParam(name = "Precondition", targetNamespace = "http://docs.oasis-open.org/wsn/2004/06/wsn-WS-BaseNotification-1.2-draft-01.xsd")
            QueryExpressionType precondition,
            @WebParam(name = "Selector", targetNamespace = "http://docs.oasis-open.org/wsn/2004/06/wsn-WS-BaseNotification-1.2-draft-01.xsd")
            QueryExpressionType selector,
            @WebParam(name = "SubscriptionPolicy", targetNamespace = "http://docs.oasis-open.org/wsn/2004/06/wsn-WS-BaseNotification-1.2-draft-01.xsd")
            Object subscriptionPolicy,
            @WebParam(name = "InitialTerminationTime", targetNamespace = "http://docs.oasis-open.org/wsn/2004/06/wsn-WS-BaseNotification-1.2-draft-01.xsd")
            XMLGregorianCalendar terminationTime) throws ResourceUnknownFault, SubscribeCreationFailedFault {
        ResourceUnknownFaultType ru = new ResourceUnknownFaultType();
        ErrorCode code = new ErrorCode();
        code.setDialect("en");
        ru.setErrorCode(code);
        
        throw new ResourceUnknownFault("Foo", ru);
    }
}
