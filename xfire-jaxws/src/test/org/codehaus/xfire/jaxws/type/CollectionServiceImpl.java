package org.codehaus.xfire.jaxws.type;

import java.util.ArrayList;
import java.util.Collection;

import javax.jws.WebService;

@WebService(endpointInterface="org.codehaus.xfire.jaxws.type.CollectionService")
public class CollectionServiceImpl implements CollectionService {
	public Collection<Foo> getFoos() {
		ArrayList<Foo> name = new ArrayList<Foo>();
		Foo foo = new Foo();
		name.add(foo);
		
		return name;
	}
}
