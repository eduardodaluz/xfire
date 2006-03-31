package org.codehaus.xfire.attachment;

import java.io.File;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

public class PictureServiceImpl implements PictureService
{
    public DataSource GetPicture()
    {
        return new FileDataSource("src/test-resources/xfire.jpg");
    }

    public DataHandler EchoPicture2(DataHandler handler)
    {
        return new DataHandler(GetPicture());
    }

    public DataSource EchoPicture(DataSource req)
    {
        return GetPicture();
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
