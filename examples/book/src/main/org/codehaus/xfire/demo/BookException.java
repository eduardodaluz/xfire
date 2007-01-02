package org.codehaus.xfire.demo;

import javax.xml.namespace.QName;

import org.codehaus.xfire.fault.FaultInfoException;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class BookException
    extends FaultInfoException
{
	private BookExceptionDetail faultDetail ;
	
	public BookException(String msg, BookExceptionDetail details){
		super(msg);
		faultDetail=details;
	}
	public BookExceptionDetail getFaultInfo() {
		return faultDetail;
	}

	public static QName getFaultName() {
		return new QName("http://demo.xfire.codehaus.org", "BookFault");
	}
}
