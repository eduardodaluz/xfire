package org.codehaus.xfire.management.mbeans;

import java.io.Serializable;

public class ServiceStat implements ServiceStatMBean, Serializable {

 
	private static final long serialVersionUID = 1L;
	private Long x_failedRequestCount= new Long(0);
	private Long x_totalRequestCount= new Long(0);
	private Long x_lastResponseTime= new Long(0);
	private Long x_maxResponseTime= new Long(0);
	private Long x_minResponseTime= new Long(Long.MAX_VALUE);

	public Long getSuccessfulRequestCount() {
		return x_totalRequestCount - x_failedRequestCount;
	}
	public Long getFailedRequestCount() {
		return x_failedRequestCount;
	}
	public Long getTotalRequestCount() {
		return x_totalRequestCount;
	}
	public Long getLastResponseTime() {
		return x_lastResponseTime;
	}
	public Long getMaxResponseTime() {
		return x_maxResponseTime;
	}
	public Long getMinResponseTime() {
		return x_minResponseTime;
	}
	public void setFailedRequestCount() {
        x_failedRequestCount++;
	}
	public void setLastResponseTime(Long arg) {
		x_lastResponseTime =arg; 
		setMaxResponseTime(arg);
		setMinResponseTime(arg); 
		
	}
	private void setMaxResponseTime(Long arg) {
		if(arg > x_maxResponseTime){
			x_maxResponseTime = arg;	
		}
	}
	private void setMinResponseTime(Long arg) {
		if(arg < x_minResponseTime){
			x_minResponseTime = arg;
		}	
	}

	public void setTotalRequestCount() {
		x_totalRequestCount++;
	}

}
