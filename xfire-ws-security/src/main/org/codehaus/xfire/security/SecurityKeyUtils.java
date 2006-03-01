package org.codehaus.xfire.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class SecurityKeyUtils
{

    /**
     * @param filename
     * @param algoirtm
     * @return
     * @throws Exception
     */
    public static SecretKey loadKeyFromFile(String filename, String algoritm)
        throws Exception
    {
        InputStream inStream = null;
        inStream = new FileInputStream(filename);
        return loadKeyFromFile(inStream, algoritm);
    }

    /**
     * @param inStream
     * @param alogirtm
     * @return
     * @throws Exception
     */
    public static SecretKey loadKeyFromFile(InputStream inStream, String algoritm)
        throws Exception
    {
        SecretKeySpec keySpec = null;
        try
        {

            byte[] keyData = new byte[inStream.available()];
            inStream.read(keyData);
            keySpec = new SecretKeySpec(keyData, algoritm);
        }
        finally
        {
            if (inStream != null)
            {
                inStream.close();
            }
        }
        return keySpec;
    }

    /**
     * @param algoritm
     * @param keySize
     * @return
     * @throws Exception
     */
    public static SecretKey generateSymmeticKey(String algoritm, String keySize)
        throws Exception
    {

        KeyGenerator keyGenerator = KeyGenerator.getInstance(algoritm);
        if (keySize != null && keySize.length() > 0)
        {
            int size = Integer.parseInt(keySize);
            keyGenerator.init(size);
        }
        return keyGenerator.generateKey();
    }

    /**
     * @param keyEncryptKey
     * @param filename
     * @throws IOException
     */
    public static void saveKeyToFile(Key keyEncryptKey, String filename)
        throws IOException
    {
        byte[] keyBytes = keyEncryptKey.getEncoded();
        FileOutputStream outStream = new FileOutputStream(new File(filename));
        outStream.write(keyBytes);
        outStream.close();

    }

}
