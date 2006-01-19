package org.codehaus.xfire.security.wssecurity;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.codehaus.xfire.security.InSecurityProcessor;
import org.codehaus.xfire.security.InSecurityProcessorBuilder;
import org.codehaus.xfire.security.SecurityActions;
import org.codehaus.xfire.security.WSPasswordCallback;
import org.codehaus.xfire.security.exceptions.ConfigValidationException;
import org.codehaus.xfire.security.impl.SecurityFileConfigurer;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class WSS4JInProcessorBuilder
    extends SecurityFileConfigurer
    implements InSecurityProcessorBuilder
{

    private static final String CFG_FILE = "META-INF/xfire/insecurity.properties";

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.xfire.security.InSecurityProcessorBuilder#build(org.codehaus.xfire.security.InSecurityProcessor)
     */
    public void build(InSecurityProcessor processor)
    {
        if (!(processor instanceof WSS4JInSecurityProcessor))
        {
            throw new RuntimeException(
                    "Processor is not an instance of WSS4JInSecurityProcessor.class");
        }
        WSS4JInSecurityProcessor wss4jProcessor = (WSS4JInSecurityProcessor) processor;

        Properties props = loadConfigFile(getConfigFile());

        String actionsStr = props.getProperty(PROP_ACTIONS);
        String actions[] = actionsToArray(actionsStr);
        for (int i = 0; i < actions.length; i++)
        {
            String action = actions[i];
            if (SecurityActions.AC_ENCRYPT.equals(action)
                    || SecurityActions.AC_SIGNATURE.equals(action))
            {
                wss4jProcessor.setCrypto(createCrypto(props));
                break;
            }
        }

        String callbackClassStr = props.getProperty(PROP_PASSWORD_CALLBACK);
        if (callbackClassStr != null)
        {
            try
            {
                Class callbackClass = Class.forName(callbackClassStr);

                wss4jProcessor.setCallback((WSPasswordCallback) callbackClass.newInstance());

            }
            catch (ClassNotFoundException e)
            {
                throw new ConfigValidationException("", "Can't load class : " + callbackClassStr);
            }
            catch (InstantiationException e)
            {
                throw new ConfigValidationException("", "Can't create instance of class : "
                        + callbackClassStr);
            }
            catch (IllegalAccessException e)
            {
                throw new ConfigValidationException("", "Can't create instance of class : "
                        + callbackClassStr);
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.xfire.security.impl.SecurityFileConfigurer#getDefaultConfigFile()
     */
    protected String getDefaultConfigFile()
    {
        return CFG_FILE;
    }

}
