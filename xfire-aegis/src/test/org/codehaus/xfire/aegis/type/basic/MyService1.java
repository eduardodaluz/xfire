package org.codehaus.xfire.aegis.type.basic;

import java.util.Collection;
import java.util.List;

/**
 * @author Hani Suleiman
 *         Date: Jun 14, 2005
 *         Time: 10:25:45 PM
 */
public interface MyService1
{
    String getFoo();
    String getUnmapped(List list);
    Collection getCollection();
    Collection getCollection(String id);
    String getCollection(String id, int value);
    Collection getCollectionForValues(int value, Collection c);
    Collection getCollectionForValues(String id, Collection c);
}
