package org.codehaus.xfire.xmlbeans.example;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.xfire.fault.XFireFault;

import com.acme.purchaseOrder.x2005.x08.PurchaseOrderDocument;
import com.acme.purchaseOrder.x2005.x08.PurchaseOrderIdDocument;
import com.acme.purchaseOrder.x2005.x08.PurchaseOrderType;

public class OrderServiceImpl
    implements OrderService
{
    Map purchaseOrders = new HashMap();

    long count = 0;

    public PurchaseOrderDocument getPurchaseOrder(PurchaseOrderIdDocument id)
        throws XFireFault
    {
        return (PurchaseOrderDocument) purchaseOrders.get(new Long(id.getPurchaseOrderId()));
    }

    public PurchaseOrderIdDocument receiveOrder(PurchaseOrderDocument purchaseOrder)
        throws XFireFault
    {
        PurchaseOrderType order = purchaseOrder.getPurchaseOrder();
        order.setPurchaseOrderId(count++);

        PurchaseOrderIdDocument pid = PurchaseOrderIdDocument.Factory.newInstance();
        pid.setPurchaseOrderId(order.getPurchaseOrderId());

        return pid;
    }
}
