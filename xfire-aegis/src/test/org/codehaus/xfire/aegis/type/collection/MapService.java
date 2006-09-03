package org.codehaus.xfire.aegis.type.collection;

import java.util.Map;

import org.codehaus.xfire.aegis.type.collection.bean.MapBean;

public interface MapService {
    public Map echoMap(Map map);
    public MapBean echoMapBean(MapBean map);
    public Map echoMapOfCollections(Map map);
}
