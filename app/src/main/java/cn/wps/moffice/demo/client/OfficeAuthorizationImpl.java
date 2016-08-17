package cn.wps.moffice.demo.client;


import android.os.RemoteException;

import cn.wps.moffice.client.OfficeAuthorization;


public class OfficeAuthorizationImpl extends OfficeAuthorization.Stub 
{
	
	protected MOfficeClientService service = null;
	
	public OfficeAuthorizationImpl( MOfficeClientService service ) 
	{
		this.service = service;
	}

	@Override
	public int getAuthorization( String[] auth_code ) throws RemoteException 
	{
		auth_code[0] = "abxxdsewrwsds3232ss";
		return 0;
	}
}
