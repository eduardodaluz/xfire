package org.codehaus.xfire.xmlbeans.example;

import org.codehaus.xfire.fault.XFireFault;

import com.acme.purchaseOrder.x2005.x08.PurchaseOrderDocument;
import com.acme.purchaseOrder.x2005.x08.PurchaseOrderIdDocument;

public interface OrderService {
    PurchaseOrderIdDocument receiveOrder(
            PurchaseOrderDocument purchaseOrder)
            throws XFireFault;

    PurchaseOrderDocument getPurchaseOrder(
            PurchaseOrderIdDocument id)
            throws XFireFault;
}