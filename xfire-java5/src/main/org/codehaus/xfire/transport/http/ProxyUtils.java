package org.codehaus.xfire.transport.http;

import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class ProxyUtils {

	public boolean isNonProxyHost(String strURI) {
		URI uri = null;
		try {
			uri = new URI(strURI);
		} catch (URISyntaxException use) { // this should actually not happen,
											// but just
			// in case.
			return false;
		}
		// ... get a system platform ProxySelector, and ...
		ProxySelector ps = ProxySelector.getDefault();
		// ... let this selector return a list of proxies.
		List proxies = ps.select(uri);
		// If that lists sole element is of type Proxy.NO_PROXY
		// then we need a direct connection, otherwise we need to connect
		// through a proxy.
		if (proxies.size() == 1 && proxies.get(0).equals(Proxy.NO_PROXY)) {
			return true;
		}

		return false;
	}

}
