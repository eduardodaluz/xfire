package org.codehaus.xfire.demo;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class BookExceptionDetail {

	private String detailMessage;

	private String code;

	public String getCode() {
		return code;
	}

	public BookExceptionDetail() {
		
	}

	public BookExceptionDetail(String code, String msg) {
		this.code = code;
		detailMessage = msg;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDetailMessage() {
		return detailMessage;
	}

	public void setDetailMessage(String detailMessage) {
		this.detailMessage = detailMessage;
	}

}
