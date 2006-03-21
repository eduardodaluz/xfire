package org.codehaus.xfire.jaxb2;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.codehaus.xfire.mtom.EchoPicture;
import org.codehaus.xfire.mtom.EchoPictureResponse;
import org.codehaus.xfire.mtom.GetPicture;
import org.codehaus.xfire.mtom.GetPictureResponse;

public class PictureServiceImpl implements PictureService
{
    /* (non-Javadoc)
     * @see org.codehaus.xfire.jaxb2.IPictureService#GetPicture(org.codehaus.xfire.mtom.GetPicture)
     */
    public GetPictureResponse GetPicture(GetPicture req)
    {
        GetPictureResponse response = new GetPictureResponse();
        try
        {
            Image image = ImageIO.read(getTestFile("src/test-resources/xfire.jpg"));
            response.setImage(image);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return response;
    }
    
    /* (non-Javadoc)
     * @see org.codehaus.xfire.jaxb2.IPictureService#EchoPicture(org.codehaus.xfire.mtom.EchoPicture)
     */
    public EchoPictureResponse EchoPicture(EchoPicture req)
    {
        EchoPictureResponse response = new EchoPictureResponse();
        response.setImage(req.getImage());

        return response;
    }
    
    private String basedirPath;
    
    public String getTestFilePath(String name)
    {
        return getTestFile(name).getAbsolutePath();
    }
    
    public File getTestFile(String name)
    {
        return new File(getBasedir(), name);
    }

    public String getBasedir()
    {
        if (basedirPath != null)
        {
            return basedirPath;
        }

        basedirPath = System.getProperty("basedir");

        if (basedirPath == null)
        {
            basedirPath = new File("").getAbsolutePath();
        }

        return basedirPath;
    }
}
