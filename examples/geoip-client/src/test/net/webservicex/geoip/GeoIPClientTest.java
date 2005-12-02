// START SNIPPET: client
package net.webservicex.geoip;

import net.webservicex.GetGeoIP;
import net.webservicex.geoip.jaxb.GeoIPServiceClient;
import net.webservicex.geoip.jaxb.GeoIPServiceSoap;
import junit.framework.TestCase;

public class GeoIPClientTest extends TestCase
{
    public void testClient()
    {
        GeoIPServiceClient service = new GeoIPServiceClient();
        GeoIPServiceSoap geoIPClient = service.getGeoIPServiceSoap();
        
        GetGeoIP getGeoIP = new GetGeoIP();
        getGeoIP.setIPAddress("216.73.126.120");
        System.out.println("The country is: " + 
                           geoIPClient.GetGeoIP(getGeoIP).getGetGeoIPResult().getCountryName());
    }
}
// END SNIPPET: client
