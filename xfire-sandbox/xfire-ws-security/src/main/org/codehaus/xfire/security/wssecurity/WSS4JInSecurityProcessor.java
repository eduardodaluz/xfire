package org.codehaus.xfire.security.wssecurity;

import java.io.IOException;
import java.util.Vector;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.WSSecurityEngine;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.components.crypto.Crypto;
import org.codehaus.xfire.security.InSecurityProcessor;
import org.codehaus.xfire.security.InSecurityResult;
import org.w3c.dom.Document;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class WSS4JInSecurityProcessor
    implements InSecurityProcessor
{

    protected static final WSSecurityEngine secEngine = WSSecurityEngine.getInstance();

    private Crypto crypto;

    private CallbackHandler cbHandler;

    private org.codehaus.xfire.security.WSPasswordCallback callback;

    public Crypto getCrypto()
    {
        return crypto;
    }

    public void setCrypto(Crypto crypto)
    {
        this.crypto = crypto;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.xfire.security.InSecurityProcessor#process(org.w3c.dom.Document)
     */
    public InSecurityResult process(Document document)
        throws org.codehaus.xfire.security.exceptions.WSSecurityException
    {
        Vector wsResult = null;

        try
        {
            wsResult = secEngine.processSecurityHeader(document, "", cbHandler, crypto, crypto);
        }
        catch (WSSecurityException ex)
        {

            throw ExceptionConverter.convert(ex);

        }

        InSecurityResult result = new InSecurityResult();
        if (wsResult != null)
        {
            SecurityResultHandler handler = new SecurityResultHandler(result);
            result = handler.process(wsResult);

        }

        result.setDocument(document);
        return result;
    }

    /**
     * @author Tomasz Sztelak
     * 
     */
    private static class CBPasswordHandler
        implements CallbackHandler
    {
        private org.codehaus.xfire.security.WSPasswordCallback passwords = null;

        public CBPasswordHandler(org.codehaus.xfire.security.WSPasswordCallback passwords)
        {
            this.passwords = passwords;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.security.auth.callback.CallbackHandler#handle(javax.security.auth.callback.Callback[])
         */
        public void handle(Callback[] callbacks)
            throws IOException, UnsupportedCallbackException
        {
            for (int i = 0; i < callbacks.length; i++)
            {
                WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
                String pass = (String) passwords.handle(pc.getIdentifer(),
                                                        pc.getPasswordType() != null,
                                                        pc.getUsage());
                if (pass != null)
                {
                    pc.setPassword(pass);
                }
            }
        }

    }

    public org.codehaus.xfire.security.WSPasswordCallback getCallback()
    {
        return callback;
    }

    public void setCallback(org.codehaus.xfire.security.WSPasswordCallback callback)
    {
        this.callback = callback;
        cbHandler = new CBPasswordHandler(callback);

    }
}
