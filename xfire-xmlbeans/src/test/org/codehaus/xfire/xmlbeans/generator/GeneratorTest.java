package org.codehaus.xfire.xmlbeans.generator;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.codehaus.xfire.test.AbstractXFireTest;


/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 27, 2004
 */
public class GeneratorTest
    extends AbstractXFireTest
{
    public void testGeneration() throws Exception
    {
        File weather = new File("src/test-schemas/WeatherForecast.wsdl");
        
        GeneratorTask task = new GeneratorTask();
        
        task.setWsdl(weather.toURL().toString());
        task.setOverwrite(true);
        File output = new File("target/generated-test");
        output.mkdir();
        
        task.setOutputDir( output.getAbsolutePath() );
        
        task.execute();
    }
    
    public void testServerGeneration() throws Exception
    {
        File weather = new File("src/test-schemas/WeatherForecast.wsdl");
        
        GeneratorTask task = new GeneratorTask();
        
        task.setWsdl(weather.toURL().toString());
        task.setOverwrite(true);
        task.setStrategy(ServerGenerationStrategy.class.getName());
        File output = new File("target/generated-test");
        output.mkdir();
        
        task.setOutputDir( output.getAbsolutePath() );
        
        task.execute();
    }
}
