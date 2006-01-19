package org.codehaus.xfire.security;

import java.util.Calendar;

import org.w3c.dom.Document;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class InSecurityResult {

	private Document document;

	private String user;

	private String password;
    
    private boolean isPasswordHashed;
    
    private Calendar tsCreated;
    
    private Calendar tsExpire;
    
    

	public Calendar getTsCreated()
    {
        return tsCreated;
    }

    public void setTsCreated(Calendar tsCreated)
    {
        this.tsCreated = tsCreated;
    }

    public Calendar getTsExpire()
    {
        return tsExpire;
    }

    public void setTsExpire(Calendar tsExpire)
    {
        this.tsExpire = tsExpire;
    }

    public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

    public boolean isPasswordHashed()
    {
        return isPasswordHashed;
    }

    public void setPasswordHashed(boolean isPasswordHashed)
    {
        this.isPasswordHashed = isPasswordHashed;
    }

}
