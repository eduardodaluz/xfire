package org.codehaus.xfire.aegis.type.java5;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.fault.XFireFault;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * HolderType based on the one from Jaxws module, except that all operations on jawax.xml.ws.Holder are
 * performed using reflection api. 
 */
public class HolderType extends org.codehaus.xfire.aegis.type.basic.HolderType {

	public HolderType(Type delegate) {
		super(delegate);
	}

	@Override
	protected void setValue(Object hObj, Object value) {
		try {
			hObj.getClass().getField("value").set(hObj, value);
		} catch (Throwable th) {
			throw new RuntimeException("Can't set 'value' field ", th);
		}
	}

	@Override
	public void writeObject(Object object, MessageWriter writer,
			MessageContext context) throws XFireFault {
		Object obj = null;
		try {
			obj = object.getClass().getField("value").get(object);
		} catch (Throwable th) {
			// / Probably used type problem, not much to do
			throw new RuntimeException("Can't get 'value' field ", th);
		}
		getDelegate().writeObject(obj, writer, context);
	}

}
