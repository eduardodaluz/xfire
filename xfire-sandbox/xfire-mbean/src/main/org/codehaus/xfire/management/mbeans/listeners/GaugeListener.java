package org.codehaus.xfire.management.mbeans.listeners;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.monitor.MonitorNotification;

public class GaugeListener implements NotificationListener {

	public GaugeListener() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void handleNotification(Notification arg0, Object arg1) {
		System.out.println("Attribute Observed "+((MonitorNotification)arg0).getObservedAttribute());
		System.out.println("Notification "+((MonitorNotification)arg0).getType());
		System.out.println("TimeStamp "+((MonitorNotification)arg0).getTimeStamp());
		System.out.println("Threshold "+((MonitorNotification)arg0).getTrigger().toString());
		System.out.println("Derived Gauge "+((MonitorNotification)arg0).getDerivedGauge());
	
	}

}
