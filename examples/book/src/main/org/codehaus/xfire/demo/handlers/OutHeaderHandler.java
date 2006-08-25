package org.codehaus.xfire.demo.handlers;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.AbstractHandler;
import org.jdom.Element;

/**
 *  @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 * Add version info to SOAP header.
 */
public class OutHeaderHandler extends AbstractHandler {
	
	private static final String VERSION_TAG = "version";

	private static final String VERSION_NS = "http://xfire.codehaus.org/Book";

	public void invoke(MessageContext ctx) throws Exception {
		
		Element header = ctx.getOutMessage().getOrCreateHeader();
		header.addContent(new Element(VERSION_TAG ,VERSION_NS).addContent("1.0"));
		
	}

}
