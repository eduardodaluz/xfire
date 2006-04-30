package org.codehaus.xfire.jibx;

/**
 * <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class AccountInfo {

	private int amount;

    public AccountInfo() {
        
    }
    
	public AccountInfo(int i) {
		amount = i;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

}
