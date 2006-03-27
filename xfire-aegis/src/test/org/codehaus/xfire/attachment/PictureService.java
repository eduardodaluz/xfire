package org.codehaus.xfire.attachment;

import javax.activation.DataSource;

public interface PictureService
{

    public abstract DataSource GetPicture();

    public abstract DataSource EchoPicture(DataSource pic);

}