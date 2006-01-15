package org.codehaus.xfire.security.exceptions;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class ConfigValidationException
    extends RuntimeException
{
    private String action;

    public ConfigValidationException(String ac, String msg)
    {
        super(msg);
        action = ac;
    }

    public String toString()
    {
        return "ConfigValidationException: Error configuring action [" + action + "]. Details : "
                + getMessage();
    }
}
