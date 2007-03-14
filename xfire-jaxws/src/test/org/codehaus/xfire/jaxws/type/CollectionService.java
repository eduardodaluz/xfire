package org.codehaus.xfire.jaxws.type;

import java.util.Collection;

import javax.jws.WebService;

@WebService
public interface CollectionService {
	Collection<Foo> getFoos();
}