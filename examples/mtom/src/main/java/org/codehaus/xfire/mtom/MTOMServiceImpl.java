package org.codehaus.xfire.mtom;

import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;


/**
 * <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class MTOMServiceImpl
    implements MTOMService
{

    /* (non-Javadoc)
     * @see org.codehaus.xfire.mtom.MTOMService#stringFromBytes(byte[])
     */
    public String stringFromBytes(byte[] data)
    {
        return new String(data);

    }

    /* (non-Javadoc)
     * @see org.codehaus.xfire.mtom.MTOMService#stringFromDataSource(javax.activation.DataSource)
     */
    public String stringFromDataSource(DataSource source)
    {
        
        try
        {
            InputStream inStr = source.getInputStream();
            int size = inStr.available();
            byte[] data = new byte[size];
            inStr.read(data);
            inStr.close();
            return new String(data);

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    /* (non-Javadoc)
     * @see org.codehaus.xfire.mtom.MTOMService#stringFromDataHandler(javax.activation.DataHandler)
     */
    public String stringFromDataHandler(DataHandler handler)
    {

        try
        {
            return (String) handler.getContent();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return "";

    }

}
