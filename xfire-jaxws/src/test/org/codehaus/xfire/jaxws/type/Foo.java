package org.codehaus.xfire.jaxws.type;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace="urn:collection:test", name="FOO")
public class Foo {
	private String bar = "bar!";

	public String getBar() {
		return bar;
	}

	public void setBar(String bar) {
		this.bar = bar;
	}
	
}
