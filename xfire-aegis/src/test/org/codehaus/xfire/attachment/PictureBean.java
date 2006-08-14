package org.codehaus.xfire.attachment;

import java.util.Date;

import javax.activation.DataSource;

public class PictureBean
{
    private DataSource data;
    private Date modified;
    
    public DataSource getData()
    {
        return data;
    }
    public void setData(DataSource data)
    {
        this.data = data;
    }
    public Date getModified()
    {
        return modified;
    }
    public void setModified(Date modified)
    {
        this.modified = modified;
    }
}
