package org.codehaus.xfire.security.wssecurity;

import java.util.Vector;

import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.security.SOAPConstants;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSEncryptionPart;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.message.WSAddTimestamp;
import org.apache.ws.security.message.WSEncryptBody;
import org.apache.ws.security.message.WSSAddUsernameToken;
import org.apache.ws.security.message.WSSignEnvelope;
import org.apache.ws.security.util.WSSecurityUtil;
import org.codehaus.xfire.security.OutSecurityProcessor;
import org.codehaus.xfire.security.OutSecurityProcessorBuilder;
import org.codehaus.xfire.security.SecurityActions;
import org.codehaus.xfire.util.DOMUtils;
import org.w3c.dom.Document;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class WSS4JOutSecurityProcessor

    implements OutSecurityProcessor
{
    private static final Log LOG = LogFactory.getLog(WSS4JOutSecurityProcessor.class);

    private Crypto crypto;

    private String alias;

    private boolean userPasswordUsePlain = false;

    private int ttl = -1;

    private String[] actions;

    private String privateAlias;

    private String privatePassword;

    private String username;

    private String userPassword;

    private OutSecurityProcessorBuilder builder;

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.xfire.security.OutSecurityProcessor#process(org.w3c.dom.Document)
     */
    public Document process(Document document)
    {

        // checkInitialized();
        for (int i = 0; i < actions.length; i++)
        {
            String action = actions[i];
            if (SecurityActions.AC_ENCRYPT.equals(action))
            {
                LOG.debug("Encrypting document");
                document = encryptDocument(document);
                continue;
            }
            if (SecurityActions.AC_SIGNATURE.equals(action))
            {
                LOG.debug("Signing document");
                document = signDocument(document);
                continue;
            }

            if (SecurityActions.AC_TIMESTAMP.equals(action))
            {
                LOG.debug("Adding timestamp");
                document = addTimestamp(document);
                continue;
            }

            if (SecurityActions.AC_USERTOKEN.equals(action))
            {
                LOG.debug("Adding usertoken");
                document = addUserToken(document);
                continue;
            }
        }

        return document;
    }

    /**
     * @param document
     * @return
     */
    private Document signDocument(Document document)
    {
        WSSignEnvelope signer = new WSSignEnvelope();
        try
        {
            SOAPConstants soapConstants = WSSecurityUtil.getSOAPConstants(document
                    .getDocumentElement());
            signer.setUserInfo(getPrivateAlias(), getPrivatePassword());
            signer.setSigCanonicalization(WSConstants.C14N_EXCL_OMIT_COMMENTS);
            signer.setSignatureAlgorithm(WSConstants.RSA);
            // signer.setUseSingleCertificate(true);
            signer.setKeyIdentifierType(WSConstants.ISSUER_SERIAL);

            Vector parts = new Vector();
            WSEncryptionPart part = new WSEncryptionPart("Body", soapConstants.getEnvelopeURI(),
                    "Content");

            parts.add(part);
            signer.setParts(parts);

            document = signer.build(document, crypto);

            try
            {
                DOMUtils.writeXml(document, System.out);
            }
            catch (TransformerException e)
            {
                e.printStackTrace();
            }
        }
        catch (WSSecurityException e)
        {
            throw ExceptionConverter.convert(e);
        }

        return document;
    }

    /**
     * @param document
     * @return
     */
    private Document encryptDocument(Document document)
    {
        SOAPConstants soapConstants = WSSecurityUtil
                .getSOAPConstants(document.getDocumentElement());
        WSEncryptBody encryptor = new WSEncryptBody();

        Vector parts = new Vector();
        WSEncryptionPart part = new WSEncryptionPart("Body", soapConstants.getEnvelopeURI(),
                "Content");

        parts.add(part);
        encryptor.setParts(parts);

        // if alias for public key is not provided, use private key intead
        if (alias != null)
        {
            encryptor.setUserInfo(alias);
        }
        else
        {
            encryptor.setUserInfo(getPrivateAlias());
        }
        try
        {
            document = encryptor.build(document, crypto);
        }
        catch (WSSecurityException e)
        {
            LOG.error(e);
            throw ExceptionConverter.convert(e);

        }
        return document;
    }

    /**
     * @param doc
     * @return
     */
    private Document addUserToken(Document doc)
    {
        WSSAddUsernameToken builder = new WSSAddUsernameToken();
        if (getUsername() != null)
        {
            if (usePlainUserPassword())
            {
                builder.setPasswordType(WSConstants.PASSWORD_TEXT);
            }
            Document signedDoc = builder.build(doc, getUsername(), getUserPassword());
            return signedDoc;
        }
        return doc;

    }

    /**
     * @param doc
     * @return
     */
    private Document addTimestamp(Document doc)
    {
        if (getTTL() == -1)
        {
            return doc;
        }
        WSAddTimestamp TTLBuilder = new WSAddTimestamp();
        return TTLBuilder.build(doc, getTTL());
    }

    public String getAlias()
    {
        return alias;
    }

    public void setAlias(String alias)
    {
        this.alias = alias;
    }

    public Crypto getCrypto()
    {
        return crypto;
    }

    public void setCrypto(Crypto crypto)
    {
        this.crypto = crypto;
    }

    public void setUsePlainUserPassword(boolean usePlain)
    {
        this.userPasswordUsePlain = usePlain;

    }

    public boolean usePlainUserPassword()
    {

        return this.userPasswordUsePlain;
    }

    public int getTTL()
    {
        return ttl;
    }

    public void setTTL(int ttl)
    {
        this.ttl = ttl;

    }

    public void setActions(String[] actionsArray)
    {
        actions = actionsArray;

    }

    public String[] getActions()
    {
        return actions;
    }

    public String getPrivateAlias()
    {
        return privateAlias;
    }

    public void setPrivateAlias(String signatureAlias)
    {
        this.privateAlias = signatureAlias;
    }

    public String getPrivatePassword()
    {
        return privatePassword;
    }

    public void setPrivatePassword(String signaturePassword)
    {
        this.privatePassword = signaturePassword;
    }

    public void setUsername(String user)
    {
        this.username = user;

    }

    public void setUserPassword(String password)
    {
        this.userPassword = password;

    }

    public void setBuilder(OutSecurityProcessorBuilder builder)
    {
        this.builder = builder;

    }

    public String getUsername()
    {

        return username;
    }

    public String getUserPassword()
    {

        return userPassword;
    }

}
