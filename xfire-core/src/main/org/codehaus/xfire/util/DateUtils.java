package org.codehaus.xfire.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Helps reading and writing ISO8601 dates.
 * <p>
 * Contains code from Apache Axis.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 */
public class DateUtils
{
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat(
			"yyyy-MM-dd");

	private static SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private static Calendar calendar = Calendar.getInstance();
    
	static
	{
		dateTimeFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public static Date parseDate(String source)
	{
		Date result;
		boolean bc = false;

		// validate fixed portion of format
		if (source != null)
		{
			if (source.charAt(0) == '+') source = source.substring(1);

			if (source.charAt(0) == '-')
			{
				source = source.substring(1);
				bc = true;
			}

			if (source.length() < 10) 
                throw new NumberFormatException("Invalid date format.");

			if (source.charAt(4) != '-' || source.charAt(7) != '-')
                 throw new NumberFormatException("Invalid date format.");

		}

		synchronized (calendar)
		{
			// convert what we have validated so far
			try
			{
				result = dateFormatter.parse(source == null ? null : (source.substring(0, 10)));
			}
			catch (Exception e)
			{
				throw new NumberFormatException(e.toString());
			}

			// support dates before the Christian era
			if (bc)
			{
				calendar.setTime(result);
				calendar.set(Calendar.ERA, GregorianCalendar.BC);
				result = calendar.getTime();
			}
		}

		return result;
	}

    public static String formatDate( Date date )
    {
        synchronized (dateFormatter) 
        {
            // Sun JDK bug http://developer.java.sun.com/developer/bugParade/bugs/4229798.html
            return dateFormatter.format(date);
        }   
    }
    
    public static String formatDateTime( Date date )
    {
        synchronized (dateTimeFormatter) 
        {
            // Sun JDK bug http://developer.java.sun.com/developer/bugParade/bugs/4229798.html
            return dateTimeFormatter.format(date);
        }   
    }
    
	public static Date parseDateTime(String source)
	{
		Calendar calendar = Calendar.getInstance();
		Date date;
		boolean bc = false;

		// validate fixed portion of format
		if (source != null)
		{
			if (source.charAt(0) == '+') source = source.substring(1);

			if (source.charAt(0) == '-')
			{
				source = source.substring(1);
				bc = true;
			}

			if (source.length() < 19) 
                throw new NumberFormatException("Invalid date format.");

			if (source.charAt(4) != '-' || source.charAt(7) != '-'
					|| source.charAt(10) != 'T') 
                throw new NumberFormatException("Invalid date format.");

			if (source.charAt(13) != ':' || source.charAt(16) != ':') 
                throw new NumberFormatException("Invalid date format.");
		}

		// convert what we have validated so far
		try
		{
			synchronized (dateTimeFormatter)
			{
				date = dateTimeFormatter.parse(source == null ? null : (source.substring(0,19) + ".000Z"));
			}
		}
		catch (Exception e)
		{
			throw new NumberFormatException(e.toString());
		}

		int pos = 19;

		// parse optional milliseconds
		if (source != null)
		{
			if (pos < source.length() && source.charAt(pos) == '.')
			{
				int milliseconds = 0;
				int start = ++pos;
				while (pos < source.length()
						&& Character.isDigit(source.charAt(pos)))
					pos++;

				String decimal = source.substring(start, pos);
				if (decimal.length() == 3)
				{
					milliseconds = Integer.parseInt(decimal);
				}
				else if (decimal.length() < 3)
				{
					milliseconds = Integer.parseInt((decimal + "000")
							.substring(0, 3));
				}
				else
				{
					milliseconds = Integer.parseInt(decimal.substring(0, 3));
					if (decimal.charAt(3) >= '5') ++milliseconds;
				}

				// add milliseconds to the current date
				date.setTime(date.getTime() + milliseconds);
			}

			// parse optional timezone
			if (pos + 5 < source.length()
					&& (source.charAt(pos) == '+' || (source.charAt(pos) == '-')))
			{
				if (!Character.isDigit(source.charAt(pos + 1))
						|| !Character.isDigit(source.charAt(pos + 2))
						|| source.charAt(pos + 3) != ':'
						|| !Character.isDigit(source.charAt(pos + 4))
						|| !Character.isDigit(source.charAt(pos + 5))) 
                    throw new NumberFormatException("Invalid date format. Bad Timezone.");

				int hours = (source.charAt(pos + 1) - '0') * 10
						+ source.charAt(pos + 2) - '0';
				int mins = (source.charAt(pos + 4) - '0') * 10
						+ source.charAt(pos + 5) - '0';
				int milliseconds = (hours * 60 + mins) * 60 * 1000;

				// subtract milliseconds from current date to obtain GMT
				if (source.charAt(pos) == '+') milliseconds = -milliseconds;
				date.setTime(date.getTime() + milliseconds);
				pos += 6;
			}

			if (pos < source.length() && source.charAt(pos) == 'Z')
			{
				pos++;
				calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
			}

			if (pos < source.length()) 
                 throw new NumberFormatException("Invalid date format.");
		}

		calendar.setTime(date);

		// support dates before the Christian era
		if (bc)
		{
			calendar.set(Calendar.ERA, GregorianCalendar.BC);
		}

		return date;
	}
}