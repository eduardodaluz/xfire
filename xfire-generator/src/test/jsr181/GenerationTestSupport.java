package jsr181;

import java.io.File;

import junit.framework.TestCase;

public class GenerationTestSupport
    extends TestCase
{
    public String getTestFilePath(String name)
    {
        return name;
    }
    
    public File getTestFile(String name)
    {
        return new File(getTestFilePath(name));
    }
}
