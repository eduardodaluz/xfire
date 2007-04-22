package org.codehaus.xfire.aegis.type.java5;

import javax.xml.ws.Holder;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.fault.XFireFault;

public class HolderType extends org.codehaus.xfire.aegis.type.basic.HolderType {

	public HolderType(Type delegate) {
		super(delegate);
	}

	@Override
	protected void setValue(Object hObj, Object value) {
		Holder holder = (Holder) hObj;
		holder.value = value;
	}

	@Override
	public void writeObject(Object object, MessageWriter writer,
			MessageContext context) throws XFireFault {
		getDelegate().writeObject(((Holder) object).value, writer, context);
	}

}
