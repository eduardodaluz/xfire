package org.codehaus.xfire.aegis.type.collection;

import java.util.Map;

import org.codehaus.xfire.aegis.type.collection.bean.MapBean;

public class MapServiceImpl
    implements MapService
{
    public Map echoMap(Map map)
    {
        return map;
    }

    public MapBean echoMapBean(MapBean map)
    {
        return map;
    }
}
