package org.codehaus.xfire.addressing;

import java.util.Iterator;

import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.ServiceInfo;

public class AddressingOperationInfo
{
    private String inAction;
    private String outAction;
    private OperationInfo operationInfo;
    
    public final static String ADDRESSING_OPERATION_KEY = "addressingOperationInfo";
    
    public AddressingOperationInfo(String inAction, OperationInfo op)
    {
        this(inAction, inAction + "Ack", op);
    }

    public AddressingOperationInfo(String inAction, String outAction, OperationInfo op)
    {
        this.inAction = inAction;
        this.outAction = outAction;
        
        op.setProperty(ADDRESSING_OPERATION_KEY, this);
        this.operationInfo = op;
    }

    public static AddressingOperationInfo getAddressingOperationInfo(OperationInfo op)
    {
        return (AddressingOperationInfo) op.getProperty(ADDRESSING_OPERATION_KEY);
    }
    
    public static String getInAction(OperationInfo op)
    {
        AddressingOperationInfo aoi = getAddressingOperationInfo(op);
        if (aoi == null) return null;
        
        return aoi.getInAction();
    }
    
    public static String getOutAction(OperationInfo op)
    {
        AddressingOperationInfo aoi = getAddressingOperationInfo(op);
        if (aoi == null) return null;
        
        return aoi.getOutAction();
    }
    
    public static AddressingOperationInfo getOperationByInAction(ServiceInfo service, String name)
    {
        for (Iterator itr = service.getOperations().iterator(); itr.hasNext();)
        {
            OperationInfo op = (OperationInfo) itr.next();
            AddressingOperationInfo aoi = getAddressingOperationInfo(op);
            
            if (aoi == null) continue;
            
            if (aoi.getInAction() != null && aoi.getInAction().equals(name))
            {
                return aoi;
            }
        }
        
        if (!name.equals("*"))
        {
            return getOperationByInAction(service, "*");
        }
        
        return null;
    }
    
    public String getInAction()
    {
        return inAction;
    }
    public void setInAction(String inAction)
    {
        this.inAction = inAction;
    }
    public String getOutAction()
    {
        return outAction;
    }
    public void setOutAction(String outAction)
    {
        this.outAction = outAction;
    }

    public OperationInfo getOperationInfo()
    {
        return operationInfo;
    }

    public void setOperationInfo(OperationInfo operationInfo)
    {
        this.operationInfo = operationInfo;
    }

}
