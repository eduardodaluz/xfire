// START SNIPPET: client
package net.webservicex.geoip;

import junit.framework.TestCase;
import net.webservicex.GetGeoIPDocument;
import net.webservicex.GetGeoIPDocument.GetGeoIP;
import net.webservicex.weather.GeoIPServiceClient;
import net.webservicex.weather.GeoIPServiceSoap;

public class GeoIPClientTest extends TestCase
{
    public void testClient()
    {
        GeoIPServiceClient service = new GeoIPServiceClient();
        GeoIPServiceSoap geoIPClient = service.getGeoIPServiceSoap();
        
        GetGeoIPDocument getGeoIPDocument = GetGeoIPDocument.Factory.newInstance();
        GetGeoIP geoIP = getGeoIPDocument.addNewGetGeoIP();
        geoIP.setIPAddress("216.73.126.120");
        System.out.println("The country is: " + 
                           geoIPClient.GetGeoIP(getGeoIPDocument).getGetGeoIPResponse().getGetGeoIPResult().getCountryName());
    }
}
// END SNIPPET: client
