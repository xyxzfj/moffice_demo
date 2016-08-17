package cn.wps.moffice.demo.client;


import cn.wps.moffice.client.ActionType;
import cn.wps.moffice.client.OfficeEventListener;
import cn.wps.moffice.client.OfficeInputStream;
import cn.wps.moffice.client.OfficeOutputStream;
import cn.wps.moffice.demo.util.Define;
import cn.wps.moffice.demo.util.EncryptClass;
import cn.wps.moffice.demo.util.SettingPreference;
import android.os.RemoteException;
import android.util.Log;

public class OfficeEventListenerImpl extends OfficeEventListener.Stub 
{
	protected MOfficeClientService service = null;
	
	private boolean mIsValidPackage = true;
	
	public OfficeEventListenerImpl( MOfficeClientService service )
	{
		this.service = service;
	}
	
	@Override
	public int onOpenFile( String path, OfficeOutputStream output )
			throws RemoteException 
	{
		Log.d("OfficeEventListener", "onOpenFile");
		
		if (!mIsValidPackage)
			return -1;
		
		return EncryptClass.openFile(path, output);
		
	}
	
	@Override
	public int onSaveFile(OfficeInputStream input, String path)throws RemoteException 
	{
		Log.d("OfficeEventListener", "onSaveFile");
		return EncryptClass.saveFile(input, path);
	}

	@Override
	public int onCloseFile() throws RemoteException 
	{
		Log.d("OfficeEventListener", "onCloseFile");
		return 0;
	}
	
	@Override
	public boolean isActionAllowed(String path, ActionType type) throws RemoteException 
	{
//		可以参考下面屏蔽的这段代码进行限制,以剪切为例
//		if (type.equals(ActionType.AT_CUT))
//		{
//			return false;//不允许剪切
//		}
		
		SettingPreference settingPreference;
		settingPreference 	= 	new SettingPreference(this.service.getApplicationContext());
		if (type.toString().equals(Define.AT_EDIT_REVISION))
		{//如果是接受或拒绝某条修订的事件,做特殊处理
			String docUserName         = settingPreference.getSettingParam(Define.USER_NAME, "");
			boolean	 typeAT 	= 	settingPreference.getSettingParam(type.toString(), true);
			boolean isSameOne = docUserName.equals(path);//在此事件中，path中存放是是作者批注名
			if (!typeAT && !isSameOne)
			{
				return false;
			}
			return true;
		}
		
		boolean	 typeAT 	= 	settingPreference.getSettingParam(type.toString(), true);
		String	 pathAT 	= 	settingPreference.getSettingParam(Define.AT_PATH, "/");
		boolean  isExist 	= 	path.startsWith(pathAT) || path.equals("");  //有部分事件传过来路径为"",
		if (!typeAT && isExist)
			return false;
		return true;
		
	}
	
	/**
	 * 实现多个可变包名的验证
	 * originalPackage是最原始的第三方包名，华为渠道为“com.huawei.svn.hiwork”
	 * thirdPackage为可变动的应用包名，具体有企业资金定制
	 */
	@Override
	public boolean isValidPackage(String originalPackage, String thirdPackage)
			throws RemoteException {
//此处是某些企业的特殊需求，可以忽略
//		mIsValidPackage = false;
//		if (originalPackage.equals(service.getPackageName()) && thirdPackage.equals("cn.wps.moffice"))
//		{
//			mIsValidPackage = true;
//			return true;
//		}
		return false;
	}
}
