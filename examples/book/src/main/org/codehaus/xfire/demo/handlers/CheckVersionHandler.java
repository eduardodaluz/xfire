package org.codehaus.xfire.demo.handlers;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.handler.AbstractHandler;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 * Retrive service version from SOAP header.
 */
public class CheckVersionHandler extends AbstractHandler {

	private static final String VERSION_TAG = "version";

	private static final String VERSION_NS = "http://xfire.codehaus.org/Book";

	public void invoke(MessageContext ctx) throws Exception {
		// Check if header exists
		Element header = ctx.getInMessage().getHeader();
		if (header == null) {
			throw new XFireRuntimeException("Missing SOAP header");
		}
		// Does it have version tag
		Element version = header.getChild(VERSION_TAG, Namespace
				.getNamespace(VERSION_NS));
		if (version == null) {
			throw new XFireRuntimeException("Missing version header");
		}

		ctx.setProperty("ServiceVersion", version.getText());
	}

}
