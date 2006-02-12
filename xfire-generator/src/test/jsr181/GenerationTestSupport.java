package jsr181;

import java.io.File;

import junit.framework.TestCase;

public class GenerationTestSupport
    extends TestCase
{
    private static String basedirPath;
    
    public String getTestFilePath(String name)
    {
        return getTestFile(name).getAbsolutePath();
    }
    
    public File getTestFile(String name)
    {
        return new File(getBasedir(), name);
    }

    public static String getBasedir()
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
