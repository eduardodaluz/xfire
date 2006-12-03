package org.codehaus.xfire.gen;

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.codehaus.xfire.gen.documentation.DocumentationConfigGen;
import org.codehaus.xfire.service.Service;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class WsDocGenTask
    extends BaseServicesGenTask
{

    private String outputDirectory;

    private String srcDirectory;

    private String filePath;

    private String files[];

    private List<String> services = new ArrayList<String>();

    @Override
    public void execute()
        throws BuildException
    {
        System.out.print("\n WSDocGenTask \n");
        System.out.print("OutputDirectory :" + getOutputDirectory() + "\n");
        System.out.print("srcDirectory :" + getSrcDirectory() + "\n");
        System.out.print("filePath :" + getFilePath() + "\n");
        System.out.print("configUrl :" + getConfigUrl() + "\n");

        if (!validateParams())
        {
            return;
        }

        String srcFiles[] = null;
        DocumentationConfigGen gen = new DocumentationConfigGen();
        gen.setOutputFolder(outputDirectory);

        if (getConfigUrl() != null)
        {
            iterateServices();
            srcFiles = (String[]) services.toArray(new String[services.size()]);
        }
        else
        {
            if (filePath != null)
            {
                srcFiles = new String[] { filePath };
            }
            else
            {
                srcFiles = files;
            }
        }

        
        gen.setSrcFiles(srcFiles);
        gen.generate();
    }

    private boolean validateParams()
    {
        if (getSrcDirectory() == null)
        {
            System.out.print("SrcDirectory not specified. \n");
            return false;
        }
        return true;
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

    @Override
    protected void processService(Service service)
    {
        String name = service.getServiceInfo().getServiceClass().getName();
        System.out.print(name + "\n");
        services.add(nameToPath(name));
    }

    private String nameToPath(String name)
    {
        String path = getSrcDirectory();
        if (!path.endsWith("/"))
        {
            path = path + "/";
        }

        name = name.replaceAll("\\.", "/");
        return path + name + ".java";
    }

    public String getSrcDirectory()
    {
        return srcDirectory;
    }

    public void setSrcDirectory(String srcDirectory)
    {
        this.srcDirectory = srcDirectory;
    }

}
