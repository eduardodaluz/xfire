package org.codehaus.xfire.client;

import java.io.IOException;

import org.codehaus.xfire.security.wss4j.crypto.AbstractDecrypter;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class XORDecrypter extends AbstractDecrypter {

	private static final char[] XOR_KEY = "VwCHleQODAwMDQxNDUIXQcAcQtgBFtdAgBCCR5ZW1hVd0MNOQVcWWDUgAAAEBBQgFIgCRHQH9iDQEADEYHbAkDAAAMF2BWA3"
			.toCharArray();

	private static final BASE64Decoder DECODER64 = new BASE64Decoder();

	private static final BASE64Encoder ENCODER64 = new BASE64Encoder();

	@Override
	public String decryptString(String value) {

		try {
			value = new String(DECODER64.decodeBuffer(value));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return new String(xor(value));

	}

	private char[] xor(String value) {

		char[] valueChars = value.toCharArray();

		for (int i = 0; i < valueChars.length; i++) {
			valueChars[i] = (char) (valueChars[i] ^ XOR_KEY[i % XOR_KEY.length]);
		}
		return valueChars;

	}

	public String encryptString(String value) {

		return new String(ENCODER64.encodeBuffer(new String(xor(value))
				.getBytes()));

	}

	/*
	  // Do not leave decryption code in prod environment
	   *  
	  public static void main(String[] args) {
		XORDecrypter dec = new XORDecrypter();
		String val = dec.encryptString("client344Password");

		System.out.print(val + "\n");

		val = dec.decryptString(val);

		System.out.print(val + "\n");

	}*/

}
