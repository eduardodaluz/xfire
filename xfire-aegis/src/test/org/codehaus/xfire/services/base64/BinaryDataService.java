package org.codehaus.xfire.services.base64;
import java.util.zip.CRC32;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BinaryDataService {
	private final Log log = LogFactory.getLog(getClass());
	
	public String verifyDataIntegrity(byte[] data, int length, long crc32) {
		log.debug("verifyDataIntegrity([" + data.length + " bytes of data], " + length + ", " + crc32 + ") called.");
		
		String status  = getStatusForData(data, length, crc32);
		
		log.debug("verifyDataIntegrity status: " + status);
		return status;
	}

	
	/**
	 * @param data
	 * @param length
	 * @param crc32
	 * @return
	 */
	private String getStatusForData(byte[] data, int length, long crc32) {
		
		String status;
		if (data.length != length) {
			status = "data.length == " + data.length + ", should be " + length;
		} else {
			CRC32 computedCrc32 = new CRC32();
			computedCrc32.update(data);
			if (computedCrc32.getValue() != crc32) {
				status = "Computed crc32 == " + computedCrc32.getValue() + ", should be " + crc32;
			} else {
				status = "OK";
			}
		}
		return status;
	}

}
