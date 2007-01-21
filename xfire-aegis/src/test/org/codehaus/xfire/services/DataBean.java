package org.codehaus.xfire.services;

public class DataBean
{
    private byte[] data = new byte[0];
    private byte[] moreData = new byte[0];
    
    public byte[] getData()
    {
        return data;
    }

    public void setData(byte[] data)
    {
        this.data = data;
    }

	public byte[] getMoreData() {
		return moreData;
	}

	public void setMoreData(byte[] moreData) {
		this.moreData = moreData;
	}
    
}
