package org.codehaus.xfire.xmlbeans.example;

import java.util.Calendar;

import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.xmlbeans.XmlBeansServiceFactory;

import com.acme.purchaseOrder.x2005.x08.Address;
import com.acme.purchaseOrder.x2005.x08.PurchaseOrderDocument;
import com.acme.purchaseOrder.x2005.x08.PurchaseOrderType;

public class ClientExample
{
    public static void main(String args[]) throws Exception
    {
        String url = "http://localhost:8080/purchase-order/services/OrderService";
        if (args.length == 0)
        {
            System.out.println("URL was not supplied. Using default URL - " + url);
        }
        else
        {
            url = args[0];
        }
        
        XmlBeansServiceFactory xsf = new XmlBeansServiceFactory();
        Service serviceModel = xsf.create(OrderService.class);
        
        OrderService client = 
            (OrderService) new XFireProxyFactory().create(serviceModel, url);
        
        PurchaseOrderDocument poDoc = PurchaseOrderDocument.Factory.newInstance();
        PurchaseOrderType po = poDoc.addNewPurchaseOrder();
        
        Address add = Address.Factory.newInstance();
        add.setName("Jane Doe");
        add.setStreet("123 Main St");
        add.setCountry("USA");
        
        po.setBillTo(add);
        po.setShipTo(add);
        
        po.setOrderDate(Calendar.getInstance());
        
        System.out.println("Purchase Order ID: " + client.receiveOrder(poDoc).getPurchaseOrderId());
    }
}
