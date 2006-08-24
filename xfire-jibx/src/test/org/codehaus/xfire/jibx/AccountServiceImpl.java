package org.codehaus.xfire.jibx;

/**
 * <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class AccountServiceImpl implements AccountService {

	public AccountInfo getAccountStatus(String user, Account param) {
        System.out.println("ACCOUNT: " + param + " User: " + user);
		return new AccountInfo(0);
	}
}
