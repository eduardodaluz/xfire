package org.codehaus.xfire.soap;

import java.util.Iterator;

import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.ServiceInfo;

/**
 * Represents SOAP specific information related to an operation.
 * 
 * @author Dan
 */
public class SoapOperationInfo
{
    private String soapAction;
    private String use;
    
    public final static String SOAP_OPERATION_INFO_KEY = "soapOperationInfo";
    
    public SoapOperationInfo() {}
    
    public SoapOperationInfo(String action, OperationInfo op)
    {
        this(action, SoapConstants.USE_LITERAL, op);
    }

    public SoapOperationInfo(String action, String use, OperationInfo op)
    {
        this.soapAction = action;
        this.use = use;
        
        op.setProperty(SOAP_OPERATION_INFO_KEY, this);
    }

    public static SoapOperationInfo getSoapOperationInfo(OperationInfo op)
    {
        return (SoapOperationInfo) op.getProperty(SOAP_OPERATION_INFO_KEY);
    }

    public static String getSoapAction(OperationInfo op)
    {
        SoapOperationInfo soapOp = getSoapOperationInfo(op);
        
        if (soapOp == null) return null;
        
        return soapOp.getSoapAction();
    }
    
    public static OperationInfo getOperationByAction(ServiceInfo service, String name)
    {
        for (Iterator itr = service.getOperations().iterator(); itr.hasNext();)
        {
            OperationInfo op = (OperationInfo) itr.next();
            SoapOperationInfo sop = getSoapOperationInfo(op);
            if (sop.getSoapAction() != null && sop.getSoapAction().equals(name))
            {
                return op;
            }
        }
        
        if (!name.equals("*"))
        {
            return getOperationByAction(service, "*");
        }
        
        return null;
    }
    
    public String getSoapAction()
    {
        return soapAction;
    }
    public void setSoapAction(String soapAction)
    {
        this.soapAction = soapAction;
    }
    public String getUse()
    {
        return use;
    }
    public void setUse(String use)
    {
        this.use = use;
    }

}
