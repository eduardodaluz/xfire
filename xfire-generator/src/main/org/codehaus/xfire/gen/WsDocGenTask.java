package org.codehaus.xfire.gen;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.codehaus.xfire.gen.documentation.DocumentationConfigGen;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class WsDocGenTask
    extends MatchingTask
{

    private String outputDirectory;

    private String filePath;

    private String files[];

    @Override
    public void execute()
        throws BuildException
    {

        DocumentationConfigGen gen = new DocumentationConfigGen();
        gen.setOutputFolder(outputDirectory);
        String srcFiles[] = null;
        if (filePath != null)
        {
            srcFiles = new String[] { filePath };
        }
        else
        {
            srcFiles = files;
        }
        gen.setSrcFiles(srcFiles);
        gen.generate();
    }

    public String getFilePath()
    {
        return filePath;
    }

    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }

    public String[] getFiles()
    {
        return files;
    }

    public void setFiles(String[] files)
    {
        this.files = files;
    }

    public String getOutputDirectory()
    {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory)
    {
        this.outputDirectory = outputDirectory;
    }
    
    
}
