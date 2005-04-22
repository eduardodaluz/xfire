package org.codehaus.xfire.util;

/**
 * Helps when constructing, or using services.
 *
 * @author Arjen Poutsma
 */
public class ServiceUtils
{
    private ServiceUtils()
    {

    }

    /**
     * Generates a suitable service name from a given class. The returned name is the simple name of the class, i.e.
     * without the package name.
     *
     * @param clazz the class.
     * @return the name.
     */
    public static String makeServiceNameFromClassName(Class clazz)
    {
        String name = clazz.getName();
        int last = name.lastIndexOf(".");
        if (last != -1)
        {
            name = name.substring(last + 1);
        }
        return name;
    }

}
