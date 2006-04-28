package org.codehaus.xfire.management.mbeans;

public interface ServiceStatMBean {

	public Long getSuccessfulRequestCount();
	public Long getFailedRequestCount();
	public Long getTotalRequestCount();
	public Long getLastResponseTime();
	public Long getMaxResponseTime();
	public Long getMinResponseTime();

	public void setTotalRequestCount();
	public void setFailedRequestCount();
	public void setLastResponseTime(Long arg); 

}
