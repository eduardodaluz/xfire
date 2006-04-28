package org.codehaus.xfire.management.mbeans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import sun.misc.Unsafe;
import sun.reflect.ReflectionFactory;

/**
 * Instantiates a new object on the Sun JVM by bypassing the constructor (meaning code in the constructor
 * will never be executed and parameters do not have to be known). This is the same method used by the internals of
 * standard Java serialization, but relies on internal Sun code that may not be present on all JVMs.
 *
 * @author Joe Walnes
 * @author Brian Slesinsky
 */
public class Sun14ReflectionProvider {

    private final static Unsafe unsafe;
    private final static Exception exception;
    static {
        Unsafe u = null;
        Exception ex = null;
        try {
            Class objectStreamClass = Class.forName("java.io.ObjectStreamClass$FieldReflector");
            Field unsafeField = objectStreamClass.getDeclaredField("unsafe");
            unsafeField.setAccessible(true);
            u = (Unsafe) unsafeField.get(null);
        } catch (ClassNotFoundException e) {
            ex = e;
        } catch (SecurityException e) {
            ex = e;
        } catch (NoSuchFieldException e) {
            ex = e;
        } catch (IllegalArgumentException e) {
            ex = e;
        } catch (IllegalAccessException e) {
            ex = e;
        }
        exception = ex;
        unsafe = u;
    }
    
    private static transient ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();
    private static transient Map constructorCache = Collections.synchronizedMap(new HashMap());

    public static Object newInstance(Class type) throws Exception {
        Constructor customConstructor = getMungedConstructor(type);
        return customConstructor.newInstance(new Object[0]);
    }

    private static Constructor getMungedConstructor(Class type) throws NoSuchMethodException {
        if (!constructorCache.containsKey(type)) {
            Constructor javaLangObjectConstructor = Object.class.getDeclaredConstructor(new Class[0]);
            Constructor customConstructor = reflectionFactory.newConstructorForSerialization(type, javaLangObjectConstructor);
            constructorCache.put(type, customConstructor);
        }
        return (Constructor) constructorCache.get(type);
    }

}
