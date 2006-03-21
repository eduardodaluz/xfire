package org.codehaus.xfire.jaxb2;

import org.codehaus.xfire.mtom.EchoPicture;
import org.codehaus.xfire.mtom.EchoPictureResponse;
import org.codehaus.xfire.mtom.GetPicture;
import org.codehaus.xfire.mtom.GetPictureResponse;

public interface PictureService
{

    public abstract GetPictureResponse GetPicture(GetPicture req);

    public abstract EchoPictureResponse EchoPicture(EchoPicture req);

}