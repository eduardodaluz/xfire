package org.codehaus.xfire.attachment;

import javax.activation.DataHandler;
import javax.activation.DataSource;

public interface PictureService
{

    public abstract DataSource GetPicture();

    public abstract DataSource EchoPicture(DataSource pic);

    public abstract DataHandler EchoPicture2(DataHandler handler);

    public abstract byte[] EchoPictureBytes(byte[] pic);

}