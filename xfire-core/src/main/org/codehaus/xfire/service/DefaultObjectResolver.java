package org.codehaus.xfire.service;

import org.codehaus.xfire.util.ClassLoaderUtils;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class DefaultObjectResolver implements ObjectResolver {

	public Object resolve(String id) {

		Class clazz;
        try
        {
            clazz = ClassLoaderUtils.loadClass(id, this.getClass());
            return clazz.newInstance();
        }
        catch (ClassNotFoundException e)
        {
         throw new RuntimeException("Can't load class : "+ id,e);
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException("Can't create object of class :"+ id,e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Can't create object of class :"+ id,e);
        }
	}

}
