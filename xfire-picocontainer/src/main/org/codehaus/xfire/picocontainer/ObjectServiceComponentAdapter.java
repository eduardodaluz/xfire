package org.codehaus.xfire.picocontainer;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.defaults.DecoratingComponentAdapter;

public class ObjectServiceComponentAdapter extends DecoratingComponentAdapter {

    public ObjectServiceComponentAdapter(ComponentAdapter ca) {
        super(ca);
    }

}
