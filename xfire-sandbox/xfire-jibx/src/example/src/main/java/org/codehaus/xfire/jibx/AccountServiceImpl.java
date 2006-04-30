package org.codehaus.xfire.jibx;

/**
 * <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class AccountServiceImpl implements AccountService {

	public AccountInfo getAccountStatus(Account param) {
        System.out.println("ACCOUNT: " + param);
		return new AccountInfo(0);
	}
}
