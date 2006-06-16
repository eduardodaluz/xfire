package org.codehaus.xfire.mtom;

import javax.activation.DataHandler;
import javax.activation.DataSource;

/**
 * <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public interface MTOMService {

	public String stringFromBytes(byte[] data);

	public String stringFromDataSource(DataSource source);
    
    public String stringFromDataHandler(DataHandler handler);

}
