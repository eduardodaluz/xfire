package org.codehaus.xfire.jaxb2;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;

import junit.framework.Assert;

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
        Assert.assertNotNull(req.getImage());
        
        try
        {
            InputStream is = req.getImage().getInputStream();
            
            int i = 0;
            while (is.read() != -1)
                i++;
            
            Assert.assertEquals(27364, i);
        }
        catch (IOException e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
        
        FileDataSource source = new FileDataSource(getTestFile("src/test-resources/xfire.jpg"));
        response.setImage(new DataHandler(source));
        
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
