/**
 *	 文件名：Define.java
 * 	创建者:fanguangcheng
 * 	创建时间:2013.7.18
 * 	作用：保存一些基本的常用字符串
 */
package cn.wps.moffice.demo.util;

public class Define 
{
	public static final String PREFS_NAME = "MyPrefsFile";			//用于存取参数的文件名
	public static final String KEY = "PackageName";					//第三方包名
	public static final String READ_ONLY = "ReadOnly";				//只读模式
	public static final String NORMAL = "Normal";					//正常模式
	public static final String READ_MODE = "ReadMode";		//打开文件直接进入阅读器模式
	public static final String SAVE_ONLY = "SaveOnly";			//仅仅用来另存文件
	public static final String VIEW_SCALE = "ViewScale";			//视图比例
	public static final String VIEW_PROGRESS = "ViewProgress";		//查看进度百分比
	public static final String VIEW_SCROLL_X = "ViewScrollX";		//显示的x坐标
	public static final String VIEW_SCROLL_Y = "ViewScrollY";		//显示的y坐标
	public static final String CLOSE_FILE = "CloseFile";			//关闭的文件
	public static final String OPEN_FILE = "OpenFile";				//打开的文件
	public static final String THIRD_PACKAGE = "ThirdPackage";		//第三方的包名
	public static final String SAVE_PATH = "SavePath";				//文件保存的路径
	public static final String CLEAR_BUFFER = "ClearBuffer";		//清除缓冲区,默认为true
	public static final String CLEAR_TRACE = "ClearTrace";			//清除使用痕迹,默认为false
	public static final String CLEAR_FILE = "ClearFile";			//删除文件自身,默认为false
	public static final String CHECK_PACKAGE_NAME = "CheckPackageName";		//企业版华为不固定的应用包名
	public static final String JSON_DATA = "JsonData";		                //企业版华为需求，不连agent，直接连client打开文档
	
	
	//以下是自己重新定义的
	public static final String USER_NAME = "UserName";
	public static final String SEND_CLOSE_BROAD = "SendCloseBroad";	//关闭文件时是否发送广播,默认不发送
	public static final String SEND_SAVE_BROAD = "SendSaveBroad";		//关闭保存时是否发送广播,默认不发送
	public static final String IS_VIEW_SCALE = "IsViewScale";		//view scale
	public static final String OPEN_MODE = "OpenMode";				//阅读器模式
	public static final String AUTO_JUMP = "AutoJump";				//第三方打开文件时是否自动跳转
	public static final String IS_CLEAR_BUFFER = "IsClearBuffer";		//清除缓冲区,默认为true
	public static final String IS_CLEAR_TRACE = "IsClearTrace";			//清除使用痕迹,默认为false
	public static final String IS_CLEAR_FILE = "IsClearFile";			//删除文件自身,默认为false	
	public static final String HOME_KEY_DOWN = "HomeKeyDown";		//Home 按钮
	public static final String BACK_KEY_DOWN = "BackKeyDown";		//Back 按钮
	public static final String CACHE_FILE_INVISIBLE = "CacheFileInvisible";		//缓存文件是否可见，默认可见
	public static final String ENTER_REVISE_MODE = "EnterReviseMode";		//以修订模式打开文档
	
	
	public static final String PACKAGENAME_ENG = "cn.wps.moffice_eng";	//个人版的包名
	public static final String PACKAGENAME_ENT = "cn.wps.moffice_ent";	//企业版的包名
	public static final String PACKAGENAME = "cn.wps.moffice";			//测试用
	public static final String PACKAGENAME_KING_ENT= "com.kingsoft.moffice_ent";
	public static final String PACKAGENAME_KING_PRO = "com.kingsoft.moffice_pro";
	public static final String PACKAGENAME_KING_PRO_HW = "com.kingsoft.moffice_pro_hw";		//华为定制包名
	
	public static final String CLASSNAME = "cn.wps.moffice.documentmanager.PreStartActivity2";		//wps类名
	public static final String OFFICE_SERVICE_ACTION = "cn.wps.moffice.service.OfficeService";
	public static final String PRO_OFFICE_SERVICE_ACTION = "cn.wps.moffice.service.ProOfficeService";
	
	public static final String WPS_OPEN_MODE = "WPSOPENMODE";				
	public static final String WPS_OPEN_AIDL = "AIDL";
	public static final String WPS_OPEN_THIRD = "THIRD";
	
	public static final String FAIR_COPY = "FairCopy";		//清稿
	public static final String FAIR_COPY_PW = "FairCopyPw";		//清稿密码
	
	public static final String IS_SHOW_VIEW = "IsShowView";   //是否显示wps界面
	
	
	//编辑
	public static final int INVALID_EDITPARAM = -1;
	public static final String AT_SAVE = "AT_SAVE";                   //保存
	public static final String AT_SAVEAS = "AT_SAVEAS";               //另存为
	public static final String AT_COPY = "AT_COPY";                   //复制
	public static final String AT_CUT = "AT_CUT";                      //剪切
	public static final String AT_PASTE = "AT_PASTE";                  //粘贴
	public static final String AT_EDIT_TEXT = "AT_EDIT_TEXT";          //插入文字
	public static final String AT_EDIT_PICTURE = "AT_EDIT_PICTURE";      //插入图片
	public static final String AT_EDIT_SHAPE = "AT_EDIT_SHAPE";         //插入浮动图片
	public static final String AT_EDIT_CHART = "AT_EDIT_CHART";         //编辑图表
	public static final String AT_SHARE = "AT_SHARE";                    //分享
	public static final String AT_PRINT = "AT_PRINT";                    //输出
	public static final String AT_SPELLCHECK = "AT_SPELLCHECK";          //拼写检查
	public static final String AT_QUICK_CLOSE_REVISEMODE = "AT_QUICK_CLOSE_REVISEMODE";          //快速关闭修订
	public static final String AT_MULTIDOCCHANGE = "AT_MULTIDOCCHANGE";          //多文档切换
	public static final String AT_EDIT_REVISION = "AT_EDIT_REVISION";
	public static final String AT_PATH = "at_path";                             //编辑路径

}
