package org.codehaus.xfire.util;

import java.util.Date;
import junit.framework.TestCase;

/**
 * DateTest
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class DateTest
	extends TestCase
{
	public void testDates() throws Exception
    {
        Date date0 = DateUtils.parseDate( "1999-10-20" );
        Date dateTime0 = DateUtils.parseDateTime( "1999-05-31T13:20:00.000-05:00" );
        Date dateTime1 = DateUtils.parseDateTime( "2000-03-04T23:00:00+03:00" );
        Date dateTime2 = DateUtils.parseDateTime( "2000-03-04T20:00:00Z" );
        
        Date dateTime3 = DateUtils.parseDateTime( "2000-01-15T12:00:00" );
        Date dateTime4 = DateUtils.parseDateTime( "2000-01-16T12:00:00Z" );
        assertTrue ( dateTime3.before( dateTime4 ) );
        
        Date dateTime5 = DateUtils.parseDateTime( "2000-01-15T00:00:00" );
        Date dateTime6 = DateUtils.parseDateTime( "2000-02-15T00:00:00" );
        assertTrue ( dateTime5.before( dateTime6 ) );
                        
        //Date time0 = DateUtils.parseDate( "13:20:00.000-05:00" );
    }
}
