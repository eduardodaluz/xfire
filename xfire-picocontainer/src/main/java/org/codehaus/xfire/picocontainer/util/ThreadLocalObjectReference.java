package org.codehaus.xfire.picocontainer.util;

import org.picocontainer.defaults.ObjectReference;

public class ThreadLocalObjectReference implements ObjectReference {

    public ThreadLocal reference = new ThreadLocal();

    public Object get() {
        return this.reference.get();
    }

    public void set(Object value) {
        this.reference.set(value);
    }

}
