/**
 *	 文件名：ListFile.java
 * 	创建者:fanguangcheng
 * 	创建时间:2013.7.18
 * 	作用：文件列表显示，并响应打开wps文件等一系列动作
 */
package cn.wps.moffice.demo.fileManager;
import java.io.File;
import java.io.IOException;

import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import cn.wps.moffice.demo.client.MOfficeClientService;
import cn.wps.moffice.demo.floatingview.service.FloatingService;
import cn.wps.moffice.demo.floatingview.service.FloatingServiceHideView;
import cn.wps.moffice.demo.menu.AIDLParamActivity;
import cn.wps.moffice.demo.menu.ATEditParamActivity;
import cn.wps.moffice.demo.menu.ListViewParamActivity;
import cn.wps.moffice.demo.menu.OpenWayParamActivity;
import cn.wps.moffice.demo.test.AutoTest;
import cn.wps.moffice.demo.util.Define;
import cn.wps.moffice.demo.util.MyGestureListener;
import cn.wps.moffice.demo.util.SettingPreference;

public class ListFileActivity extends ListActivity 
{
	
	private static final String JSON_DATA =
			"[" +
			"{ \"name\" : \"cn.wps.moffice.client.OfficeServiceClient\"," +
			" \"type\" : \"Package-ID\",\"id\" : \"cn.wps.moffice.client\", " +
			"\"Security-Level\" : \"Full-access\", \"Authorization\"  : \"abxxdsewrwsds3232ss\" }," +
			"]";
	
		File currentParent;					// 记录当前的父文件夹
		File[] currentFiles;				// 记录当前路径下的所有文件夹的文件数组
		private int position = 1;           //记录当前文件位置
		private final static String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		private final String LOG_TAG = ListFileActivity.class.getName();
		File root = new File(rootPath);
		SortByName sort = new SortByName();	//文件排序

		//存入preference的操作对象
		SettingPreference settingPreference;
	    @Override
	    public void onCreate( Bundle savedInstanceState ) 
	    {
	        super.onCreate( savedInstanceState );
			
	        currentParent = root;
			currentFiles = root.listFiles();
			if (sort.hideFileNum(currentFiles) == currentFiles.length)
			{//如果目录下都是隐藏文件就返回
				Toast.makeText(this,"当前路径下没有文件", Toast.LENGTH_LONG).show();
				return;
			}
			currentFiles = sort.sort(currentFiles);
			setListAdapter(new EfficientAdapter(this, currentFiles));

	        //启动service
	        Intent intent = new Intent( this, MOfficeClientService.class);
	        startService( intent );
	        
	        //实现将第三方包名写入文件，以便wps读取
	        settingPreference = new SettingPreference(this);
	        settingPreference.setSettingParam(Define.KEY, getPackageName());
	    }
	    
	    @Override
	    public void onResume() 
	    {
	    	super.onResume();
	    }
	    
	    @Override
	    public void onPause() 
	    {
	    	super.onPause();
	    }
	    
	    protected BroadcastReceiver broadcastReceiver = new BroadcastReceiver() 
	    {
	    	@Override
	    	public void onReceive( Context context, Intent intent )
	    	{
	    		updateUI( intent );
	    	}
	    };
	    
	    private void updateUI( Intent intent ) 
	    {
	    	Log.d( LOG_TAG, "Service ...... " );
	    }
	    
		private long exitTime = 0;						//实现“再按一次退出程序”的时间检测
		private long waitTime = 2000; 
		@Override
		public void onBackPressed() 
		{
			try 
			{
				if (!currentParent.getCanonicalPath().equals(rootPath)) 
				{
					currentParent = currentParent.getParentFile();
					currentFiles = currentParent.listFiles();
					currentFiles = sort.sort(currentFiles);
				    setListAdapter(new EfficientAdapter(this, currentFiles));
				}
				else
				{
					if((System.currentTimeMillis() - exitTime) > waitTime)
					{  
					    Toast.makeText(getApplicationContext(), "再按一次退出程序", 
					    		Toast.LENGTH_SHORT).show();                                
					    exitTime = System.currentTimeMillis();   
					} 
					else 
					{
					    finish();
					    System.exit(0);
					}
				}
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) 
    {
		super.onListItemClick(l, v, position, id);
		if (currentFiles[position].isFile()) 
		{
			
			if (IsWPSFile(currentFiles[position]))
			{	
				//如果是wps能打开的文件，则打开
				if (Define.WPS_OPEN_AIDL.equals(settingPreference.getSettingParam(Define.WPS_OPEN_MODE, "")))
				{
					this.position = position;
					if (settingPreference.getSettingParam(Define.IS_SHOW_VIEW, true))
					{
						//显示wps界面操作
						FloatingService.setDocPath(currentFiles[position].getAbsolutePath());
		        		Intent service = new Intent();
						service.setClass(ListFileActivity.this, FloatingService.class);
						startService(service);
					}
					else
					{
						//不显示wps界面操作
						FloatingServiceHideView.setDocPath(currentFiles[position].getAbsolutePath());
		        		Intent service2 = new Intent();
						service2.setClass(ListFileActivity.this, FloatingServiceHideView.class);
						startService(service2);
					}
				}
				else
				{
					//以第三方方式打开
					openFile(currentFiles[position].getAbsolutePath());
				}
				
			}
			else
			{//不是wps文件则让用户选择
				Intent intent = new Intent();  
		        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		        intent.setAction(android.content.Intent.ACTION_VIEW);  
		        String type = getMIMEType(currentFiles[position]);  
		        intent.setDataAndType(Uri.fromFile(currentFiles[position]), type); 
		        startActivity(intent);  
			}
			return;
		}
		// 如果是目录，获取用户点击的文件夹 下的所有文件
		File[] tem = currentFiles[position].listFiles();
		if (tem == null || tem.length == 0) 
		{
			Toast.makeText(this,"当前路径下没有文件", Toast.LENGTH_LONG).show();
			return;
		} 
		else 
		{
			if (sort.hideFileNum(tem) == tem.length)
			{//如果目录下都是隐藏文件就返回
				Toast.makeText(this,"当前路径下没有文件", Toast.LENGTH_LONG).show();
				return;
			}
			currentParent = currentFiles[position];
			currentFiles = tem;
			currentFiles = sort.sort(currentFiles);
	        setListAdapter(new EfficientAdapter(this, currentFiles));
		}
	}
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) 
    {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, "        设置常用参数");
		menu.add(0, 1, 1, "        设置打开方式参数");
		menu.add(0, 2, 2, "        设置AIDL调用参数");
		menu.add(0, 3, 3, "        设置编辑参数");
		menu.add(0, 4, 4, "        加解密自动化测试");
	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
		case 0:
			Intent intent = new Intent();
			intent.setClass(this, ListViewParamActivity.class);
			startActivity(intent);//无返回值的调用,启动一个明确的activity
			break;
		case 1:
			Intent intent1 = new Intent();
			intent1.setClass(this, OpenWayParamActivity.class);
			startActivity(intent1);//无返回值的调用,启动一个明确的activity
			break;
		case 2:
			Intent intent2 = new Intent();
			intent2.setClass(this, AIDLParamActivity.class);
			startActivity(intent2);//无返回值的调用,启动一个明确的activity
			break;
		case 3:
			Intent intent3 = new Intent();
			intent3.setClass(this, ATEditParamActivity.class);
			startActivity(intent3);//无返回值的调用,启动一个明确的activity
			break;
		case 4:
			Intent autotestIntent = new Intent();
			autotestIntent.setClass(this, AutoTest.class);
            startActivity(autotestIntent);
            break;
        default:
	    	return super.onOptionsItemSelected(item);
	    }
		return	true;

	}
    
    
    /**
     * 识别文件的类型
     * @param f
     * @return
     */
    private String getMIMEType(File f)
    {  
        String end = f.getName().substring(f.getName().lastIndexOf(".") + 1,  
                		f.getName().length()).toLowerCase();  
        String type = "";  
        if (end.equals("mp3") || end.equals("aac") || end.equals("aac") 
        		|| end.equals("amr") || end.equals("mpeg") || end.equals("mp4"))  
        {  
          type = "audio";  
        }
        else if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("jpeg"))  
        {  
          type = "image";  
        } 
        else if (end.equals("doc") || end.equals("docx") || end.equals("pdf")
        		|| end.equals("txt"))
        {  
          type = "application/msword";  
          return type;
        }  
        else
        {
        	type = "*";
        }
        type += "/*";  
        return type;  
    } 
    
  /**
   * 如果是wps文件，则用wps打开，并对其设置一下参数
   * @param path
   * @return
   */
  private boolean openFile(String path)
  {
	  String wps_open_mode = settingPreference.getSettingParam(Define.WPS_OPEN_MODE, "null");
	  if (wps_open_mode.equals(Define.WPS_OPEN_AIDL))
	  {
		  //AIDL打开方式
		  FloatingService.setDocPath(path);
		  Intent service = new Intent();
		  service.setClass(ListFileActivity.this, FloatingService.class);
		  startService(service);
		  Log.d("sort", "AIDL方式启动office");
		  return true;
	  }
	  //获得上次打开的文件信息
	  String 	closeFilePath 	= settingPreference.getSettingParam(Define.CLOSE_FILE, "null");  
	  String 	packageName   	= settingPreference.getSettingParam(Define.THIRD_PACKAGE, getPackageName());
	  float 	ViewProgress 	= settingPreference.getSettingParam(Define.VIEW_PROGRESS, (float)0.0);
      float 	ViewScale 		= settingPreference.getSettingParam(Define.VIEW_SCALE, (float)1.0);
      int 		ViewScrollX 	= settingPreference.getSettingParam(Define.VIEW_SCROLL_X, 0);
      int 		ViewScrollY 	= settingPreference.getSettingParam(Define.VIEW_SCROLL_Y ,0);
      String savePath           = settingPreference.getSettingParam(Define.SAVE_PATH, "");
//	  String userName           = settingPreference.getSettingParam(Define.USER_NAME, "");
//      String 	packageName 	= getPackageName();
	    //获取用户设置的参数信息
	    String	OpenMode		= settingPreference.getSettingParam(Define.OPEN_MODE, null);
	    boolean   SendSaveBroad   = settingPreference.getSettingParam(Define.SEND_SAVE_BROAD, false);
	    boolean   SendCloseBroad  = settingPreference.getSettingParam(Define.SEND_CLOSE_BROAD, false);
	    boolean   IsIsClearBuffer = settingPreference.getSettingParam(Define.IS_CLEAR_BUFFER, false);
	    boolean   IsClearTrace 	= settingPreference.getSettingParam(Define.IS_CLEAR_TRACE, false);
	    boolean   IsClearFile 	= settingPreference.getSettingParam(Define.IS_CLEAR_FILE, false);
	    boolean   IsViewScale     = settingPreference.getSettingParam(Define.IS_VIEW_SCALE ,false);
	    boolean   AutoJump		= settingPreference.getSettingParam(Define.AUTO_JUMP, false);
	    boolean   EnterReviseMode = settingPreference.getSettingParam(Define.ENTER_REVISE_MODE, false);
	    boolean   CacheFileInvisible = settingPreference.getSettingParam(Define.CACHE_FILE_INVISIBLE, false);

		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString(Define.OPEN_MODE, OpenMode);			     //打开模式
		bundle.putBoolean(Define.SEND_SAVE_BROAD, SendSaveBroad);    //保存文件的广播
		bundle.putBoolean(Define.SEND_CLOSE_BROAD, SendCloseBroad);	 //关闭文件的广播
		bundle.putBoolean(Define.CLEAR_BUFFER, IsIsClearBuffer);	 //清除临时文件
		bundle.putBoolean(Define.CLEAR_TRACE, IsClearTrace);		 //清除使用记录	
		bundle.putBoolean(Define.CLEAR_FILE, IsClearFile);           //删除打开文件
		bundle.putBoolean(Define.AUTO_JUMP, AutoJump);				//自动跳转，包括页数和xy坐标
		bundle.putString(Define.THIRD_PACKAGE, packageName);
		bundle.putString(Define.SAVE_PATH, savePath);               //保存路径
		bundle.putBoolean(Define.CACHE_FILE_INVISIBLE, CacheFileInvisible);    //
		bundle.putBoolean(Define.ENTER_REVISE_MODE, EnterReviseMode);    //
//	    bundle.putString(Define.USER_NAME, userName);	
//		bundle.putString(Define.CHECK_PACKAGE_NAME, "cn.wps.moffice");
//		bundle.putString(Define.JSON_DATA, JSON_DATA);              //特殊需求，直接跳过agent，连接client参数
		if (path.equals(closeFilePath))						       //如果打开的文档时上次关闭的
		{
			if (IsViewScale)
				bundle.putFloat(Define.VIEW_SCALE, ViewScale);				//视图比例
			if (AutoJump)
			{
				bundle.putFloat(Define.VIEW_PROGRESS, ViewProgress);		//阅读进度
				bundle.putInt(Define.VIEW_SCROLL_X, ViewScrollX);			//x
				bundle.putInt(Define.VIEW_SCROLL_Y, ViewScrollY);			//y
			}
		}
		
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		if (checkPackage(Define.PACKAGENAME_ENT)){
			intent.setClassName(Define.PACKAGENAME_ENT, Define.CLASSNAME);
		}else if (checkPackage(Define.PACKAGENAME)){
			intent.setClassName(Define.PACKAGENAME, Define.CLASSNAME);
		}else if (checkPackage(Define.PACKAGENAME_ENG)){
			intent.setClassName(Define.PACKAGENAME_ENG, Define.CLASSNAME);
		}else if (checkPackage(Define.PACKAGENAME_KING_ENT)){
			intent.setClassName(Define.PACKAGENAME_KING_ENT, Define.CLASSNAME);
		}else if (checkPackage(Define.PACKAGENAME_KING_PRO)){
			intent.setClassName(Define.PACKAGENAME_KING_PRO, Define.CLASSNAME);
		}else if (checkPackage(Define.PACKAGENAME_KING_PRO_HW)){
			intent.setClassName(Define.PACKAGENAME_KING_PRO_HW, Define.CLASSNAME);
		}
		else
		{
			Toast.makeText(this,"文件打开失败，移动wps可能未安装", Toast.LENGTH_LONG).show();
			return false;
		}
		
		File file = new File(path);
		if (file == null || !file.exists())
		{			return false;
		}
		
		Uri uri = Uri.fromFile(file);
		intent.setData(uri);
		intent.putExtras(bundle);
		
		try 
		{
			startActivity(intent);
		}
		catch (ActivityNotFoundException e) 
		{
			e.printStackTrace();
			
			return false;
		}
		Log.d("sort", "第三方打开wps");
		return true;
  	}
   
  	/**
  	 * 判断是否是wps能打开的文件
  	 * @param file
  	 * @return
  	 */
  	private boolean IsWPSFile(File file)
  	{
  		String end = file.getName().substring(file.getName().lastIndexOf(".") + 1,  
        		file.getName().length()).toLowerCase();  
  		if (end.equals("doc") || end.equals("docx") || end.equals("wps") 
			|| end.equals("dot") || end.equals("wpt") 
			|| end.equals("xls") || end.equals("xlsx") || end.equals("et") 
			|| end.equals("ppt") || end.equals("pptx") || end.equals("dps") 
			|| end.equals("txt") || end.equals("pdf"))
  			return true;
  		
  		return false;
  	}

	/**
  	 * 检测该包名所对应的应用是否存在
  	 * @param packageName
  	 * @return
  	 */
  	public boolean checkPackage(String packageName) 
  	{  
	    if (packageName == null || "".equals(packageName))  
	        return false;  
	    try 
	    {  
	        getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);  
	        return true;  
	    } 
	    catch (NameNotFoundException e) 
	    {  
	        return false;  
	    }  
	}  
	@Override
	protected void onStop() 
	{
		super.onStop();
		Log.v("stop", "stop");
		// createView();
	}

	@Override
	protected void onRestart() 
	{
		super.onRestart();
		Log.v("restart", "restart");

	}
	
	
	@Override
	protected void onDestroy() 
	{
		//unregisterReceiver(writerBackKeyDownListerner);
		//unregisterReceiver(writerHomeKeyDownListerner);
		super.onDestroy();
	}
  	
  	
  	
	private static final String ENT_WRITER_KEY_BACK_ACTION		= "com.kingsoft.writer.back.key.down";
	IntentFilter filterBackKey = new IntentFilter(ENT_WRITER_KEY_BACK_ACTION);
	private BroadcastReceiver writerBackKeyDownListerner = new BroadcastReceiver() 
	{
		 
	       @Override
	       public void onReceive(Context context, Intent intent) 
	       {
	    	   Log.d("MOfficeDemoActivity", "writerBackKeyDownListerner");
	       }
	 
	  };
	    
	private static final String ENT_WRITER_KEY_HOME_ACTION		= "com.kingsoft.writer.home.key.down";
	IntentFilter filterHomeKey = new IntentFilter(ENT_WRITER_KEY_HOME_ACTION);
	private BroadcastReceiver writerHomeKeyDownListerner = new BroadcastReceiver()
	{
		 
	       @Override
	       public void onReceive(Context context, Intent intent)
	       {
	    	   Log.d("MOfficeDemoActivity", "writerHomeKeyDownListerner");
	       }
 
    };

}
