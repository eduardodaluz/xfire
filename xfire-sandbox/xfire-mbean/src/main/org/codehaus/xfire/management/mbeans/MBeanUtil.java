package org.codehaus.xfire.management.mbeans;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Hashtable;

import javax.management.Attribute;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.monitor.CounterMonitor;
import javax.management.monitor.GaugeMonitor;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.codehaus.xfire.management.mbeans.listeners.CounterListener;
import org.codehaus.xfire.management.mbeans.listeners.GaugeListener;



public class MBeanUtil {

	JMXConnectorServer jmxConnectorServer = null;
	JMXConnectorServer cs=null;
	MBeanServer mbs = null;
	Hashtable objectNames = new Hashtable();
	public MBeanUtil() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MBeanServer createMBeanServer(){
        try {
            System.out.println("Creating the MBean server");
            mbs = ManagementFactory.getPlatformMBeanServer();
			System.out.println("Done creating the MBean server");
        }catch(Exception ex){
			ex.printStackTrace();
		}
		return mbs;
	}
	
	public MBeanServer createRemoteMBeanServer(String host,int port) throws Exception{
		mbs = MBeanServerFactory.createMBeanServer();
		JMXServiceURL url = new JMXServiceURL(
	      "service:jmx:rmi:///jndi/rmi://"+host+":"+port+"/XFireMbeanServer");
		cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs);
		cs.start();
		return mbs;
	}
	public void tearDown() throws IOException{
		cs.stop();
	}

	public void createServiceMBean(String serviceName){
		ServiceStat service = new ServiceStat();

		try {
			ObjectName objectName = new ObjectName("XFire:name="+serviceName+",type=management");
			objectNames.put(serviceName,objectName);
			mbs.registerMBean(service, objectName);
			createCounterMonitor(objectName,serviceName,new Long(500),new Long(200));
			createGaugeMonitor(objectName,serviceName,new Long(500),new Long(200),new Long(100));
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	public void createCounterMonitor(ObjectName objectName,String serviceName,Long monitoringFrequency,Long threshold){
		CounterMonitor cm = new CounterMonitor();
		cm.addObservedObject(objectName);
		cm.setObservedAttribute("TotalRequestCount");
		cm.setGranularityPeriod(monitoringFrequency);
		cm.setNotify(true);
		cm.setInitThreshold(new Long(threshold));
		cm.setDifferenceMode(true);
		cm.setNotify(true);
		cm.addNotificationListener(new CounterListener(),null,null);
		try {
			ObjectName objectNameC = new ObjectName("XFire:name="+serviceName+"CounterMonitor,"+"type=management");
			cm.start();
			mbs.registerMBean(cm, objectNameC);
		
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	public void createGaugeMonitor(ObjectName objectName,String serviceName,Long monitoringFrequency,Long thresholdHigh,Long thresholdLow){
		GaugeMonitor gm = new GaugeMonitor();
		gm.addObservedObject(objectName);
		gm.setObservedAttribute("LastResponseTime");
		gm.setGranularityPeriod(monitoringFrequency);
		gm.setNotifyHigh(true);
		gm.setNotifyLow(true);
		gm.setThresholds(thresholdHigh,thresholdLow);
		gm.setDifferenceMode(true);
		gm.addNotificationListener(new GaugeListener(),null,null);
		try {
			ObjectName objectNameG = new ObjectName("XFire:name="+serviceName+"GaugeMonitor,"+"type=management");
			gm.start();	
			mbs.registerMBean(gm, objectNameG);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public MBeanServer getMBeanServer(){
		return mbs;
	}
	
	public void recordFailedRequest(String serviceName){
		ObjectName objName = (ObjectName)objectNames.get(serviceName);
		try {
			mbs.invoke(objName,"setFailedRequestCount",null,null);
		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReflectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void recordSuccessfulRequest(String serviceName){
		
		ObjectName objName = (ObjectName)objectNames.get(serviceName);
		try {
			mbs.invoke(objName,"setSuccessfulRequestCount",null,null);
		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReflectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setLastResponseTime(String serviceName,Long arg){
		ObjectName objName = (ObjectName)objectNames.get(serviceName);
		Attribute attrib = new Attribute("LastResponseTime",arg);
		try {
			mbs.setAttribute(objName,attrib);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	public static void main(String args[]){
		try {
			MBeanUtil mu = new MBeanUtil();
			MBeanServer mbs = mu.createRemoteMBeanServer("localhost",9999);
			mu.createServiceMBean("Contract");
			System.out.println("Server is up and Running press any key to exit");
			mu.recordFailedRequest("Contract");
			mu.recordSuccessfulRequest("Contract");
			mu.setLastResponseTime("Contract",new Long(300));
			System.in.read();
			mu.tearDown();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		

}
