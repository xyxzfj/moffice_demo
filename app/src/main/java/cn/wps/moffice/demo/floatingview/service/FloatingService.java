package cn.wps.moffice.demo.floatingview.service;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import cn.wps.moffice.demo.R;
import cn.wps.moffice.demo.floatingview.FloatingFunc;
import cn.wps.moffice.demo.util.Define;
import cn.wps.moffice.demo.util.SettingPreference;
import cn.wps.moffice.service.OfficeService;
import cn.wps.moffice.service.doc.Document;
import cn.wps.moffice.service.doc.PictureFormat;
import cn.wps.moffice.service.doc.SaveFormat;
import cn.wps.moffice.service.doc.WrapType;
import cn.wps.moffice.service.doc.print.PrintOutItem;

public class FloatingService extends Service implements OnClickListener {

	private String picSavePath = "/sdcard/test.png";//截图保存路径
	
	final int QUALITY = 100;
	final int DPI = 64;
	final int PRINT_ITEM = PrintOutItem.wdPrintContent;
	
	private OfficeService mService;
	private static Document mDoc = null;
	private View view;
	
	private Button btnCloseWindow;
	private Button btnNew;
	private Button btnOpen;
	private Button btnSave;
	private Button btnSaveAs;
	private Button btnClose;
	private Button btnAddPic;
	private Button btnOpenHandComment;
	private Button btnCloseHandComment;
	private Button btnHiddenToolBar;
	private Button btnHiddenMenuBar;
	private Button btnCleanCopy;
	private Button btnCopy;
	private Button btnPaste;
	private Button btnCut;
	private Button btnShowOther;
	private Button btnAddPicture;
	private Button btnHide;	
	private Button btnIsModi;
	private Button btnTypeText;
	private Button btnGetText;
	private Button btnInsertParagraph;
	private Button btnEnterReviseMode;
	private Button btnExitReviseMode;
	private Button btnAcceptAllRevision;

	private static TextView txt_fileName;
	private int delaytime = 500;
	private static int show = 1; 
	private static int hide = 1;
	private static String docPath = "/sdcard/DCIM/文档9.doc";
	private static String picPath = "/sdcard/DCIM/ico.png";
	private LinearLayout lltCleanCopyRecord;
	private static Context mContext;     //上一级Context 为了关闭浮动窗口
	private static Context myContext;   //自身Context 为了关闭service
	public static boolean isBound = false; //是否绑定,为了在关闭wps接收到广播后解绑
	private static boolean isLoadOk = false;//判断文档是否加载完毕
	private ArrayList<String> cleanCopyRecord = new ArrayList<String>();

	private SettingPreference settingPreference;
	private static String tempSaveAsPath = "";
	private static String tempSaveAsFormat = "";  //

	@Override
	public void onCreate() {
		Log.d("FloatingService", "onCreate");
		super.onCreate();
		mContext = getApplicationContext();
		myContext = this;
		view = LayoutInflater.from(this).inflate(R.layout.floating, null);
		
		btnCloseWindow = (Button) view.findViewById(R.id.btnCloseWindow);
		btnNew = (Button) view.findViewById(R.id.btnNew);
		btnOpen = (Button) view.findViewById(R.id.btnOpen);
		btnSave = (Button) view.findViewById(R.id.btnSave);
		btnSaveAs = (Button) view.findViewById(R.id.btnSaveAs);
		btnClose = (Button) view.findViewById(R.id.btnClose);
		btnAddPic = (Button) view.findViewById(R.id.btnAddPic);
		btnCopy = (Button) view.findViewById(R.id.btnCopy);
		btnPaste = (Button) view.findViewById(R.id.btnPaste);
		btnCut = (Button) view.findViewById(R.id.btnCut);
		btnShowOther = (Button) view.findViewById(R.id.btnShowOther);
		btnOpenHandComment = (Button) view.findViewById(R.id.btnOpenHandWriter);
		btnCloseHandComment = (Button) view.findViewById(R.id.btnCloseHandWriter);
		btnHiddenMenuBar = (Button) view.findViewById(R.id.btnHiddenMenuBar);
		btnHiddenToolBar = (Button) view.findViewById(R.id.btnHiddenToolBar);
		btnCleanCopy = (Button) view.findViewById(R.id.btnCleanCopy);
		lltCleanCopyRecord = (LinearLayout) view.findViewById(R.id.cleanCopyRecordView);
		btnAddPicture = (Button) view.findViewById(R.id.btnAddPicture);
		btnHide = (Button) view.findViewById(R.id.btnHide);
		btnIsModi = (Button) view.findViewById(R.id.btnIsModi);
		btnGetText = (Button) view.findViewById(R.id.btnGetText);
		btnTypeText = (Button) view.findViewById(R.id.btnTypeText);
		btnInsertParagraph = (Button) view.findViewById(R.id.btnInsertParagraph);
		btnEnterReviseMode = (Button) view.findViewById(R.id.btnEnterReviseMode);
		btnExitReviseMode = (Button) view.findViewById(R.id.btnExitReviseMode);
		btnAcceptAllRevision = (Button) view.findViewById(R.id.btnAcceptAllRevision);
		txt_fileName = (TextView) view.findViewById(R.id.filename);
//		txt_fileName.setMovementMethod(ScrollingMovementMethod.getInstance()); 
		
		createView();
		//初始化的时候就刷新视图上按钮显示状态。
		handler.postDelayed(task, 0);

		this.settingPreference = new SettingPreference(this);
		bindOfficeService();
		

	}

	private void createView() {
		File file = new File(docPath);
		txt_fileName.setText(file.getName());
		
		btnNew.setOnClickListener(this);
		btnOpen.setOnClickListener(this);
		btnAddPic.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		btnSaveAs.setOnClickListener(this);
		btnClose.setOnClickListener(this);
		btnOpenHandComment.setOnClickListener(this);
		btnCloseHandComment.setOnClickListener(this);
		btnHiddenMenuBar.setOnClickListener(this);
		btnHiddenToolBar.setOnClickListener(this);
		btnCleanCopy.setOnClickListener(this);
		btnCloseWindow.setOnClickListener(this);
		btnCopy.setOnClickListener(this);
		btnPaste.setOnClickListener(this);
		btnCut.setOnClickListener(this);
		btnShowOther.setOnClickListener(this);
		btnAddPicture.setOnClickListener(this);
		btnHide.setOnClickListener(this);
		btnIsModi.setOnClickListener(this);
		btnTypeText.setOnClickListener(this);
		btnGetText.setOnClickListener(this);
		btnInsertParagraph.setOnClickListener(this);
		btnEnterReviseMode.setOnClickListener(this);
		btnExitReviseMode.setOnClickListener(this);
		btnAcceptAllRevision.setOnClickListener(this);
		view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				FloatingFunc.onTouchEvent(arg1, view);
				return true;
			}
		});
	}
	 int xh_count = 0;
	// 声明进度条对话框
	 ProgressDialog xh_pDialog;
	@Override
	public void onClick(View v) {
		// 如果按钮不是打开，新建文档，mDoc 为 null的话，不处理
		if (!(v.getId() == R.id.btnNew || v.getId() == R.id.btnOpen || v
				.getId() == R.id.btnCloseWindow ) && mDoc == null ) {
			Toast.makeText(getApplicationContext(), "请先打开文件", Toast.LENGTH_LONG)
					.show();
			return;
		}
		try {
			switch (v.getId()) {
			case R.id.btnCloseWindow:
				this.stopSelf();
				break;

			case R.id.btnNew:
				// 新建文档
//				newDocment();
//				this.stopSelf();
				String newDocPath = "/sdcard/newDocTest.doc";
				if (mService == null) {
					if (!bindOfficeService())
						return;
				}
				LoadNewDocThread mythreadNew = new LoadNewDocThread(newDocPath);
				mythreadNew.start();
				cleanCopyRecord = new ArrayList<String>();
				cleanCopyRecord.add(newDocPath);
				break;

			case R.id.btnOpen:
				// 打开文档
				if (mService == null) {
					if (!bindOfficeService())
						return;
				}
				LoadDocThread mythread = new LoadDocThread(docPath);
				mythread.start();
				cleanCopyRecord = new ArrayList<String>();
				cleanCopyRecord.add(docPath);
//				tx.setText(docPath);
				
				break;

			case R.id.btnAddPic:
				File file = new File(picPath);
				if (!file.exists()){
					Toast.makeText(getApplicationContext(), "图片不存在！图片路径应为:/storage/sdcard0/DCIM/ico.png",
							Toast.LENGTH_LONG).show();
					return ;
				}

				inputShapesDialog();
//				Bitmap bitmap = BitmapFactory.decodeFile(picPath);
//				mDoc.getShapes().addPicture(picPath, false, false, 10, 10, bitmap.getWidth(), bitmap.getHeight(), 0, WrapType.fromValue(7));
				break;

			case R.id.btnClose:
				closeDoc();
				break;

			case R.id.btnCloseHandWriter:
		
				mDoc.closeHandWriteComment();
				break;

			case R.id.btnOpenHandWriter:
				mDoc.showHandWriteComment();
				break;
			case R.id.btnHiddenMenuBar:
				mDoc.hiddenMenuBar();
				break;
			case R.id.btnHiddenToolBar:
				mDoc.hiddenToolBar();
				break;
			case R.id.btnSave:
				if (mDoc.isModified()){
					mDoc.save(false);
					Toast.makeText(getApplicationContext(), "保存成功！",
							Toast.LENGTH_LONG).show();
				}
		
				break;

			case R.id.btnSaveAs:
				//另存功能实现
				saveAsDocment();

				break;
			case R.id.btnInsertParagraph:
				//用新段落替换选区内容
				if ("".equals(mDoc.getSelection().getText())){
					Toast.makeText(getApplicationContext(), "没有选中段落！",
							Toast.LENGTH_LONG).show();
					return ;
				}
				mDoc.getSelection().insertParagraph();
				break;
			case R.id.btnCopy:
				//复制
				if ("".equals(mDoc.getSelection().getText())){
					Toast.makeText(getApplicationContext(), "没有选中内容！",
							Toast.LENGTH_LONG).show();
					return ;
				}
				mDoc.getSelection().copy();
				Toast.makeText(getApplicationContext(), "复制成功！",
						Toast.LENGTH_LONG).show();
				break;
			case R.id.btnPaste:
				//粘贴
				mDoc.getSelection().paste();
				break;
			case R.id.btnCut:
				//剪切
				if ("".equals(mDoc.getSelection().getText())){
					Toast.makeText(getApplicationContext(), "没有选中内容！",
							Toast.LENGTH_LONG).show();
					return ;
				}
				mDoc.getSelection().cut();
				Toast.makeText(getApplicationContext(), "剪切成功！",
						Toast.LENGTH_LONG).show();
				break;
			case R.id.btnAddPicture:
				File file2 = new File(picPath);
				if (!file2.exists()){
					Toast.makeText(getApplicationContext(), "图片不存在！图片路径应为:/storage/sdcard0/DCIM/ico.png",
							Toast.LENGTH_LONG).show();
					return ;
				}
				//添加嵌入式的图片带选区
				mDoc.getSelection().getInlineShapes().addPicture(picPath);
				break;
			case R.id.btnCleanCopy:
				//清稿
				if(settingPreference.getSettingParam(Define.FAIR_COPY, true)){
					//弹出设置清稿密码输入框
					inputPWDialog();
				}else{
					Toast.makeText(this, "没有开启清稿功能,请到菜单设置开启该功能!", Toast.LENGTH_SHORT).show();
				}
				
				break;
			case R.id.btnShowOther:
				//显示一半功能
				if (show == 1){
					hideShow();
					show = 2;
				}else{
					showOther();
					show = 1;
				}
				break;
			case R.id.btnHide:
				//隐藏和展开
				if (hide == 1){
					hideAll();
					hide = 2;
				}else{
					showOther();
					hide = 1;
					show = 1;
				}
				break;
			case R.id.btnIsModi:
				
				int start = mDoc.getSelection().getStart();
				deleteCharBefore(start);
				
//				inputSelectionInfo(mDoc.getLength());			//跳转到文档中的任意位置
//				Toast.makeText(mContext, "当前文档长度是： " + mDoc.getLength(), Toast.LENGTH_SHORT).show();
//				mDoc.getSelection().setSelection(10, 20, true);
				
				
				
//				Toast.makeText(mContext, "当前是第 " + mDoc.getCurrentPageNum(0) + " 、" + mDoc.getCurrentPageNum(1) + "、" + mDoc.getCurrentPageNum(2), Toast.LENGTH_SHORT).show();
//				mDoc.saveCurrentPageToImage(picSavePath, PictureFormat.PNG, 0,0, 
//						PrintOutItem.wdPrintContent, Color.WHITE, 595, 841);
//				Toast.makeText(mContext, "截图成功", Toast.LENGTH_SHORT).show();
//				Log.d("sort", "页码 上：" +  mDoc.getCurrentPageNum(0));
//				Log.d("sort", "页码 中：" +  mDoc.getCurrentPageNum(1));
//				Log.d("sort", "页码 下：" +  mDoc.getCurrentPageNum(2));
				
//				mDoc.clearAllComments();
//				Toast.makeText(mContext, "左面坐标：" + mDoc.getSelection().getLeft() + "/n 上面左右：" + mDoc.getSelection().getTop() , Toast.LENGTH_SHORT).show();
//				if (mDoc.isModified()){
//					Toast.makeText(mContext, "文档已修改", Toast.LENGTH_SHORT).show();
//				}else{
//					Toast.makeText(mContext, "文档未修改", Toast.LENGTH_SHORT).show();
//				}
				break;
			case R.id.btnGetText:
				//获取选区所有字符
				mDoc.undo();
//				Toast.makeText(mContext, "选区字符:  "+mDoc.getSelection().getText(), Toast.LENGTH_SHORT).show();
				break;
			case R.id.btnTypeText:
				//插入文字到选区
				inputTextDialog();
				break;
				
			case R.id.btnEnterReviseMode:
				mDoc.enterReviseMode();
				break;
				
			case R.id.btnExitReviseMode:
				mDoc.exitReviseMode();
				break;
				
			case R.id.btnAcceptAllRevision:
				mDoc.acceptAllRevision();
				break;
			}
		} catch (Exception e) {
			Toast.makeText(this, "操作失败", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}


	/**
	 * 关闭文档
	 */
	private void closeDoc() {
		try {
			mDoc.close();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 另存为文件
	 */
	private void saveAsDocment() {
		Log.i("FloatingService", "btnSaveAs");
		//打开输入框
		inputPathDialog();
	
	}
	private String createCleanCopyPath() {
		String base = docPath.substring(0, docPath.lastIndexOf("."));
		base += cleanCopyRecord.size();
		base += docPath.substring(docPath.lastIndexOf("."));
		return base;
	}
	/**
	 * 更新清稿记录
	 */
	private void updateCleanCopyRecord() {
		lltCleanCopyRecord.removeAllViews();
		for (int i = 0; i < cleanCopyRecord.size(); i++) {
			Button item = new Button(view.getContext());
			item.setText("第" + (i + 1) + "稿");
			item.setTag(i);
			item.setOnClickListener(itemListener);
			lltCleanCopyRecord.addView(item);
		}
	}

	private OnClickListener itemListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// closeDoc();
			int index = (Integer) v.getTag();
			new LoadDocThread(cleanCopyRecord.get(index)).start();
		}
	};
	/**
	 * 新建文档
	 */
	private void newDocment() {
		Intent intent = new Intent();
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
			Toast.makeText(this,"打开失败，移动wps可能未安装", Toast.LENGTH_LONG).show();
			return ;
		}
		Bundle bundle = new Bundle();
		bundle.putString("NEWDOCUMENT", "doc");// 这里第一个参数固定不变，第二个参数指定doc,ppt,xls这三个类别
		intent.putExtras(bundle);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
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


	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		Log.d("FloatingService", "onStart");
		mDoc = null;
		//setForeground(true);
		FloatingFunc.show(this.getApplicationContext(), view);
		File file = new File(docPath);
		txt_fileName.setText(file.getName());
	
	
		super.onStart(intent, startId);
	}
	/**
	 * 停止服务调用
	 */
	public static  void stopService(){
		Log.i("FloatingService", "btnCloseWindow");
		FloatingFunc.close(mContext);
		isBound = false;
		mDoc = null;
		((Service) myContext).stopSelf();//关闭自身service
	}
	@Override
	public void onDestroy() {
		//关闭线程
		handler.removeCallbacks(task);
		Log.d("FloatingService", "onDestroy");
		
		FloatingFunc.close(this.getApplicationContext());
		if (mService != null)
			unbindService(connection);
		isLoadOk = false;
		mDoc = null;
		this.stopSelf();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * connection of binding
	 */
	private  ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = OfficeService.Stub.asInterface(service);
			isBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
			isBound = false;
		}
	};

	private boolean bindOfficeService() {
		// bind service
		final Intent intent = new Intent(Define.PRO_OFFICE_SERVICE_ACTION);
		intent.putExtra("DisplayView", true);
		intent.setPackage(this.getPackageName());
		if (!bindService(intent, connection, Service.BIND_AUTO_CREATE)) {
			// bind failed, maybe wps office is not installd yet.
			unbindService(connection);
			
			return false;
		}
		return true;
	}



	/**
	 * 设置文档路径
	 * @param path
	 */
	public static void setDocPath(String path) {
		docPath = path;

	}

	class LoadDocThread extends Thread// 内部类
	{
		String path;

		public LoadDocThread(String path) {
			this.path = path;
		}

		public void run() {
			// 打开文档
			if (mService == null) {
				if (!bindOfficeService())
					return;
			}

			try {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
 
			    String 	packageName   	= settingPreference.getSettingParam(Define.THIRD_PACKAGE, getPackageName());
			    boolean FairCopy        = settingPreference.getSettingParam(Define.FAIR_COPY, true);
			    String userName         = settingPreference.getSettingParam(Define.USER_NAME, "");
			
			    bundle.putString(Define.USER_NAME, userName);	
				bundle.putBoolean(Define.SEND_CLOSE_BROAD, true);       //关闭文件的广播,由于demo的浮窗需要根据关闭广播来关闭，请设置该值为true
				bundle.putBoolean(Define.FAIR_COPY, FairCopy);
				bundle.putString(Define.USER_NAME,userName);
				bundle.putString(Define.THIRD_PACKAGE, packageName);
				bundle.putBoolean(Define.BACK_KEY_DOWN, settingPreference.getSettingParam(Define.BACK_KEY_DOWN, true));
				bundle.putBoolean(Define.HOME_KEY_DOWN, settingPreference.getSettingParam(Define.HOME_KEY_DOWN, true));
				bundle.putBoolean(Define.CACHE_FILE_INVISIBLE, false);    //

				intent.putExtras(bundle);
				mDoc = mService.openDocument(path, "", intent);
			} catch (RemoteException e) {
				e.printStackTrace();
				mDoc = null;
			}
		}
	}

	class LoadNewDocThread extends Thread// 内部类
	{
		String path;

		public LoadNewDocThread(String path) {
			this.path = path;
		}

		public void run() {
			// 打开文档
			if (mService == null) 
			{
				if (!bindOfficeService())
					return;
			}

			Intent intent = new Intent();
			Bundle bundle = new Bundle();

		    String 	packageName   	= settingPreference.getSettingParam(Define.THIRD_PACKAGE, getPackageName());
		    boolean FairCopy        = settingPreference.getSettingParam(Define.FAIR_COPY, true);
		    String userName         = settingPreference.getSettingParam(Define.USER_NAME, "");
		
		    bundle.putString(Define.USER_NAME, userName);	
			bundle.putBoolean(Define.SEND_CLOSE_BROAD, true);       //关闭文件的广播,由于demo的浮窗需要根据关闭广播来关闭，请设置该值为true
			bundle.putBoolean(Define.CACHE_FILE_INVISIBLE, false);    //

			intent.putExtras(bundle);
			try 
			{
				mDoc = mService.newDocument(path, intent);
			}
			catch (RemoteException e) 
			{
				e.printStackTrace();
				mDoc = null;
			}
		}
	}
	
	private  Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			//接受子线程另存为后的消息
			switch (msg.what) {
				case 0:
					Toast.makeText(FloatingService.this, "另存为失败",     Toast.LENGTH_SHORT).show();
					break;
				case 1:
					Toast.makeText(FloatingService.this, "另存为成功",     Toast.LENGTH_SHORT).show();
					break;
				
			}
			super.handleMessage(msg);
		}
	
	};

	private Runnable task = new Runnable() {
		
		public void run() {
			
			try 
			{
				isLoadOk = refreshView();
			}
			catch (NullPointerException ee)
			{
				ee.printStackTrace();
				mDoc = null;
			}
			catch(RemoteException e)
			{
				e.printStackTrace();
				mDoc = null;
			}
			if (!isLoadOk){
				handler.postDelayed(this, delaytime);
			}
		}
	};

	public boolean refreshView() throws RemoteException {
		
		if (mDoc != null) {
			if (mDoc.isLoadOK()) {
				btnOpen.setVisibility(View.GONE);
				
				btnAddPic.setVisibility(View.VISIBLE);
				btnSave.setVisibility(View.VISIBLE);
				btnSaveAs.setVisibility(View.VISIBLE);
				btnClose.setVisibility(View.VISIBLE);
				btnOpenHandComment.setVisibility(View.VISIBLE);
				btnCloseHandComment.setVisibility(View.VISIBLE);
				btnShowOther.setVisibility(View.VISIBLE);
				btnHide.setVisibility(View.VISIBLE);
				btnIsModi.setVisibility(View.VISIBLE);
				btnGetText.setVisibility(View.VISIBLE);
				return true; 
			} else {
				btnNew.setVisibility(View.VISIBLE);
				btnOpen.setVisibility(View.VISIBLE);
				btnNew.setClickable(false);
				btnOpen.setClickable(false);
				btnAddPic.setVisibility(View.GONE);
				btnSave.setVisibility(View.GONE);
				btnSaveAs.setVisibility(View.GONE);
				btnClose.setVisibility(View.GONE);
				btnOpenHandComment.setVisibility(View.GONE);
				btnCloseHandComment.setVisibility(View.GONE);
				btnHiddenMenuBar.setVisibility(View.GONE);
				btnHiddenToolBar.setVisibility(View.GONE);
				btnCleanCopy.setVisibility(View.GONE);
				btnCloseWindow.setVisibility(View.VISIBLE);
				btnCopy.setVisibility(View.GONE);
				btnPaste.setVisibility(View.GONE);
				btnCut.setVisibility(View.GONE);
				btnShowOther.setVisibility(View.GONE);
				btnIsModi.setVisibility(View.GONE);
				btnTypeText.setVisibility(View.GONE);
				btnGetText.setVisibility(View.GONE);
				btnInsertParagraph.setVisibility(View.GONE);
				btnEnterReviseMode.setVisibility(View.GONE);
				btnExitReviseMode.setVisibility(View.GONE);
				btnAcceptAllRevision.setVisibility(View.GONE);
				return false;
			}
			
		} else {
			btnCloseWindow.setVisibility(View.VISIBLE);
			btnNew.setVisibility(View.VISIBLE);
			btnOpen.setVisibility(View.VISIBLE);
			btnNew.setClickable(true);
			btnOpen.setClickable(true);

			btnAddPic.setVisibility(View.GONE);
			btnSave.setVisibility(View.GONE);
			btnSaveAs.setVisibility(View.GONE);
			btnClose.setVisibility(View.GONE);
			btnOpenHandComment.setVisibility(View.GONE);
			btnCloseHandComment.setVisibility(View.GONE);
			btnHiddenMenuBar.setVisibility(View.GONE);
			btnHiddenToolBar.setVisibility(View.GONE);
			btnCleanCopy.setVisibility(View.GONE);
			btnCopy.setVisibility(View.GONE);
			btnPaste.setVisibility(View.GONE);
			btnCut.setVisibility(View.GONE);
			btnShowOther.setVisibility(View.GONE);
			btnAddPicture.setVisibility(View.GONE);
			btnHide.setVisibility(View.GONE);
			btnIsModi.setVisibility(View.GONE);
			btnTypeText.setVisibility(View.GONE);
			btnGetText.setVisibility(View.GONE);
			btnInsertParagraph.setVisibility(View.GONE);
			btnEnterReviseMode.setVisibility(View.GONE);
			btnExitReviseMode.setVisibility(View.GONE);
			btnAcceptAllRevision.setVisibility(View.GONE);
			return false;
		}

	}
	/**
	 * 隐藏所有按钮
	 */
	private void hideAll(){
		btnNew.setVisibility(View.GONE);
		btnOpen.setVisibility(View.GONE);
		btnAddPic.setVisibility(View.GONE);
		btnSave.setVisibility(View.GONE);
		btnSaveAs.setVisibility(View.GONE);
		btnClose.setVisibility(View.GONE);
		btnOpenHandComment.setVisibility(View.GONE);
		btnCloseHandComment.setVisibility(View.GONE);
		btnHiddenMenuBar.setVisibility(View.GONE);
		btnHiddenToolBar.setVisibility(View.GONE);
		btnCleanCopy.setVisibility(View.GONE);
		btnEnterReviseMode.setVisibility(View.GONE);
		btnExitReviseMode.setVisibility(View.GONE);
		btnAcceptAllRevision.setVisibility(View.GONE);
		btnCloseWindow.setVisibility(View.GONE);
		btnCopy.setVisibility(View.GONE);
		btnPaste.setVisibility(View.GONE);
		btnCut.setVisibility(View.GONE);
		btnAddPicture.setVisibility(View.GONE);
		btnIsModi.setVisibility(View.GONE);
		btnShowOther.setText("");
		btnShowOther.setVisibility(View.GONE);
		btnInsertParagraph.setVisibility(View.GONE);
		btnTypeText.setVisibility(View.GONE);
		btnGetText.setVisibility(View.GONE);
		btnHide.setText("展开");
		btnHide.setVisibility(View.VISIBLE);
	}
	/**
	 * 隐藏一部分按钮
	 * @throws RemoteException
	 */
	private void hideShow() throws RemoteException {
		
		btnNew.setVisibility(View.GONE);
		btnOpen.setVisibility(View.GONE);
		btnAddPic.setVisibility(View.GONE);
		btnSave.setVisibility(View.GONE);
		btnSaveAs.setVisibility(View.GONE);
		btnOpenHandComment.setVisibility(View.GONE);
		btnCloseHandComment.setVisibility(View.GONE);
		
		btnHiddenMenuBar.setVisibility(View.VISIBLE);
		btnHiddenToolBar.setVisibility(View.VISIBLE);
		
		btnCloseWindow.setVisibility(View.GONE);
		btnClose.setVisibility(View.GONE);
		btnCleanCopy.setVisibility(View.VISIBLE);
		btnEnterReviseMode.setVisibility(View.VISIBLE);
		btnExitReviseMode.setVisibility(View.VISIBLE);
		btnAcceptAllRevision.setVisibility(View.VISIBLE);
		btnCopy.setVisibility(View.VISIBLE);
		btnPaste.setVisibility(View.VISIBLE);
		btnCut.setVisibility(View.VISIBLE);
		btnAddPicture.setVisibility(View.VISIBLE);
		btnInsertParagraph.setVisibility(View.VISIBLE);
		btnTypeText.setVisibility(View.VISIBLE);
		btnGetText.setVisibility(View.GONE);
		btnIsModi.setVisibility(View.GONE);
		btnHide.setText("隐藏所有");
		btnShowOther.setText("上页功能");
		btnShowOther.setVisibility(View.VISIBLE);
		
	}
	/**
	 * 显示一部分按钮
	 * @throws RemoteException
	 */
	private void showOther() throws RemoteException {
		btnCloseWindow.setVisibility(View.VISIBLE);
		btnAddPic.setVisibility(View.VISIBLE);
		btnSave.setVisibility(View.VISIBLE);
		btnSaveAs.setVisibility(View.VISIBLE);
		btnClose.setVisibility(View.VISIBLE);
		btnNew.setVisibility(View.VISIBLE);
		btnOpen.setVisibility(View.GONE);
		btnOpenHandComment.setVisibility(View.VISIBLE);
		btnCloseHandComment.setVisibility(View.VISIBLE);
		btnHiddenMenuBar.setVisibility(View.GONE);
		btnHiddenToolBar.setVisibility(View.GONE);
		btnCleanCopy.setVisibility(View.GONE);
		btnEnterReviseMode.setVisibility(View.GONE);
		btnExitReviseMode.setVisibility(View.GONE);
		btnAcceptAllRevision.setVisibility(View.GONE);
		btnCopy.setVisibility(View.GONE);
		btnPaste.setVisibility(View.GONE);
		btnCut.setVisibility(View.GONE);
		btnAddPicture.setVisibility(View.GONE);
		btnInsertParagraph.setVisibility(View.GONE);
		btnTypeText.setVisibility(View.GONE);
		btnGetText.setVisibility(View.VISIBLE);
		btnIsModi.setVisibility(View.VISIBLE);
		btnHide.setText("隐藏所有");
		btnShowOther.setText("下页功能");
		btnShowOther.setVisibility(View.VISIBLE);
		
	}
	
	
	/**
	 * 弹出输入另存为文件的路径对话框
	 */
	private void inputPathDialog() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		final View ParamDialogView = inflater.inflate(R.layout.path_dialog,null);
		EditText saveAsPath = (EditText) ParamDialogView.findViewById(R.id.SaveAsPath);
		String fileName = docPath.substring(0,
				docPath.lastIndexOf("."));
		saveAsPath.setText(fileName + "-副本");
		AlertDialog paramAlertDialog = new AlertDialog.Builder(mContext)
				.setTitle("设置保存路径")
				.setView(ParamDialogView)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						EditText SavePath = (EditText) ParamDialogView
								.findViewById(R.id.SaveAsPath);
						//另存为文件
						//根据ID找到RadioGroup实例
						RadioGroup group = (RadioGroup)ParamDialogView.findViewById(R.id.saveAsFormat);
						RadioButton rb = (RadioButton)ParamDialogView.findViewById(group.getCheckedRadioButtonId());
						Log.i("SaveAsFormat",rb.getText().toString());
					
						
						if (SavePath.getText().toString().length() == 0) {
							Toast.makeText(mContext, "请输入参数",
									Toast.LENGTH_SHORT).show();
							return ;
						} 
						tempSaveAsPath = SavePath.getText().toString();
						tempSaveAsFormat = rb.getText().toString();
						//开启线程--另存为
						new SaveAsThread().start();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();
		paramAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		paramAlertDialog.show();
		
	}
	
	class SaveAsThread extends Thread {
		Message msg = null;
		public void run() {
			// 开启线程--另存为
			try {
				SaveFormat saveFormat = SaveFormat.valueOf(tempSaveAsFormat);
				mDoc.saveAs(tempSaveAsPath + "." + tempSaveAsFormat.toLowerCase(), saveFormat, "", "");
				msg  = new Message();
				msg.what = 1;
				handler.sendMessage(msg);
			} catch (RemoteException e) {
				msg.what = 0;
				handler.sendMessage(msg);
				e.printStackTrace();
			}
		}
		 
	}
	
	/**
	 * 弹出输入清稿密码
	 */
	private void inputPWDialog() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		final View ParamDialogView = inflater.inflate(R.layout.param_faircopy_dialog,null);
		EditText fairCopyPath = (EditText) ParamDialogView
				.findViewById(R.id.fairCopyPath);
		fairCopyPath.setText(createCleanCopyPath());
		
		AlertDialog paramAlertDialog = new AlertDialog.Builder(mContext)
				.setTitle("设置清稿另存为路径")
				.setView(ParamDialogView)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						EditText fairCopyPath = (EditText) ParamDialogView
								.findViewById(R.id.fairCopyPath);
						try {
							
							
							mDoc.fairCopy(fairCopyPath.getText().toString(), "");
							cleanCopyRecord.add(fairCopyPath.getText().toString());
							updateCleanCopyRecord();
							
							
						} catch (RemoteException e) {
							Toast.makeText(mContext, "清稿失败", Toast.LENGTH_SHORT)
							.show();
							e.printStackTrace();
						}

						Toast.makeText(mContext, "清稿成功!!路径为: "+fairCopyPath.getText().toString(), Toast.LENGTH_SHORT)
								.show();

					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();
		paramAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		paramAlertDialog.show();
	}
	
	/**
	 * 弹出输入插入文字
	 */
	private void inputTextDialog() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		final View ParamDialogView = inflater.inflate(R.layout.param_text_dialog,null);
		
		AlertDialog paramAlertDialog = new AlertDialog.Builder(mContext)
				.setTitle("插入文本内容")
				.setView(ParamDialogView)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						EditText editAddText = (EditText) ParamDialogView
								.findViewById(R.id.editAddText);
						if ("".equals(editAddText.getText().toString()) || editAddText.getText().toString().length() == 0){
							Toast.makeText(mContext, "请输入文本", Toast.LENGTH_SHORT)
							.show();
							return ;
						}
						try {
							mDoc.getSelection().typeText(editAddText.getText().toString());
						} catch (RemoteException e) {
							e.printStackTrace();
						}
						Toast.makeText(mContext, "插入成功!!文本为: "+editAddText.getText().toString(), Toast.LENGTH_SHORT)
								.show();

					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();
		paramAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		paramAlertDialog.show();
	}
	
	/**
	 * selection的位置
	 */
	private void inputSelectionInfo(int length) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		final View ParamDialogView = inflater.inflate(R.layout.param_text_dialog,null);
		
		AlertDialog paramAlertDialog = new AlertDialog.Builder(mContext)
				.setTitle("请输入光标的位置(不大于" + (length - 1) + ")")
				.setView(ParamDialogView)
				.setPositiveButton("确定", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						EditText editAddText = (EditText) ParamDialogView.findViewById(R.id.editAddText);
						int pos = -1;
						String str = editAddText.getText().toString();
						if ("".equals(str) || str.length() == 0)
						{
							Toast.makeText(mContext, "请输入数值", Toast.LENGTH_SHORT).show();
							return ;
						}
						else 
						{
							try
							{
								pos = Integer.valueOf(str);
							} catch (NumberFormatException e) 
							{
								Toast.makeText(mContext, "输入不是数字，请重新操作", Toast.LENGTH_SHORT).show();
								e.printStackTrace();
								return;
							}
						}
						
						try 
						{
							mDoc.getSelection().setSelection(pos, pos, true);
						} 
						catch (RemoteException e)
						{
							e.printStackTrace();
						}

					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();
		paramAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		paramAlertDialog.show();
	}
	
	/**
	 * 删除光标前几个字符
	 */
	private void deleteCharBefore(final int length) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		final View ParamDialogView = inflater.inflate(R.layout.param_text_dialog,null);
		
		AlertDialog paramAlertDialog = new AlertDialog.Builder(mContext)
				.setTitle("请输入要删除的个数（当前光标前有" + length + "个字符）")
				.setView(ParamDialogView)
				.setPositiveButton("确定", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						EditText editAddText = (EditText) ParamDialogView.findViewById(R.id.editAddText);
						int pos = -1;
						String str = editAddText.getText().toString();
						if ("".equals(str) || str.length() == 0)
						{
							Toast.makeText(mContext, "请输入数值", Toast.LENGTH_SHORT).show();
							return ;
						}
						else 
						{
							try
							{
								pos = Integer.valueOf(str);
							} catch (NumberFormatException e) 
							{
								Toast.makeText(mContext, "输入不是数字，请重新操作", Toast.LENGTH_SHORT).show();
								e.printStackTrace();
								return;
							}
						}
						
						try 
						{
							
							if (pos > length)
								Toast.makeText(mContext, "删除字符超出范围", Toast.LENGTH_SHORT).show();
							
							mDoc.getSelection().setSelection((length - pos), length, true);
							mDoc.getSelection().typeText("");
						} 
						catch (RemoteException e)
						{
							e.printStackTrace();
						}

					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();
		paramAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		paramAlertDialog.show();
	}
	
	/**
	 * 弹出插入图片
	 */
	private void inputShapesDialog() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		final View ParamDialogView = inflater.inflate(R.layout.shapes_dialog,null);
		EditText editFilePath = (EditText) ParamDialogView
				.findViewById(R.id.filePath);
		editFilePath.setText(picPath);
		
		EditText editLeft = (EditText) ParamDialogView
				.findViewById(R.id.left);
		editLeft.setText(10+"");
		
		EditText editTop = (EditText) ParamDialogView
				.findViewById(R.id.top);
		editTop.setText(10+"");
		
		Bitmap bitmap = BitmapFactory.decodeFile(picPath);
		
		EditText editWidth = (EditText) ParamDialogView
				.findViewById(R.id.width);
		editWidth.setText(bitmap.getWidth()+"");
		
		EditText editHeight = (EditText) ParamDialogView
				.findViewById(R.id.height);
		editHeight.setText(bitmap.getHeight()+"");
		
		EditText editCp = (EditText) ParamDialogView
				.findViewById(R.id.cp);
		editCp.setText(0+"");
		
		
		AlertDialog paramAlertDialog = new AlertDialog.Builder(mContext)
				.setTitle("设置图片属性")
				.setView(ParamDialogView)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						EditText editFilePath = (EditText) ParamDialogView
								.findViewById(R.id.filePath);
						EditText editLeft = (EditText) ParamDialogView
								.findViewById(R.id.left);
						EditText editTop = (EditText) ParamDialogView
								.findViewById(R.id.top);
						EditText editWidth = (EditText) ParamDialogView
								.findViewById(R.id.width);
						EditText editHeight = (EditText) ParamDialogView
								.findViewById(R.id.height);
						EditText editCp = (EditText) ParamDialogView
								.findViewById(R.id.cp);
						
						RadioGroup raInkToFileGroup = (RadioGroup)ParamDialogView.findViewById(R.id.raInkToFileGroup);
						RadioButton raInkToFile = (RadioButton)ParamDialogView.findViewById(raInkToFileGroup.getCheckedRadioButtonId());
						
						RadioGroup raSaveWithDocumentGroup = (RadioGroup)ParamDialogView.findViewById(R.id.raSaveWithDocumentGroup);
						RadioButton raSaveWithDocument = (RadioButton)ParamDialogView.findViewById(raSaveWithDocumentGroup.getCheckedRadioButtonId());
						
						RadioGroup raWrapTypeGroup = (RadioGroup)ParamDialogView.findViewById(R.id.raWrapTypeGroup);
						RadioButton raWrapType = (RadioButton)ParamDialogView.findViewById(raWrapTypeGroup.getCheckedRadioButtonId());
						
						String filePath = editFilePath.getText().toString();
						String leftString = editLeft.getText().toString();
						String topString = editTop.getText().toString();
						String widthsString = editWidth.getText().toString();
						String heightsString = editHeight.getText().toString();
						String cpString = editCp.getText().toString();
						
						try {
							Toast.makeText(mContext, "页数：" + mDoc.getPageCount(), Toast.LENGTH_SHORT).show();
							mDoc.isModified();
						} catch (RemoteException e1) {
							e1.printStackTrace();
						}
						
						File file = new File(filePath);
						if (!file.exists()){
							Toast.makeText(mContext, "图片不存在!!", Toast.LENGTH_SHORT)
							.show();
							return ;
						}
						if ("".equals(leftString) || "".equals(topString) || "".equals(widthsString) || 
								"".equals(heightsString) || "".equals(cpString)){
							Toast.makeText(mContext, "请填写正确的参数!!", Toast.LENGTH_SHORT)
							.show();
							return ;
						}
							
						
						float left = Float.valueOf(leftString);
						float top = Float.valueOf(topString);
						float width = Float.valueOf(widthsString);
						float height = Float.valueOf(heightsString);
						int cp = Integer.valueOf(cpString);
						
						boolean inkToFile = Boolean.valueOf(raInkToFile.getText().toString());
						boolean saveWithDocument = Boolean.valueOf(raSaveWithDocument.getText().toString());
						
						String wrapType = raWrapType.getText().toString();
						
						
						
						System.out.println(filePath+"="+left+"="+top+"="+width+"="+height+"="+cp+"="+inkToFile+"="+saveWithDocument+"="+wrapType);
						
						try {
							float mLeaf = mDoc.getSelection().getLeft();
							float mTop = mDoc.getSelection().getTop();
							int mCp = mDoc.getSelection().getStart(); //获取选区的开始位置，始终在选区的开始插入图片
							
							mDoc.getShapes().addPicture(filePath, inkToFile, saveWithDocument, mLeaf, mTop, width, height, mCp, WrapType.valueOf(wrapType));
//							mDoc.getShapes().addPicture(filePath, inkToFile, saveWithDocument, left, top, width, height, cp, WrapType.valueOf(wrapType));
						} catch (RemoteException e) {
							Toast.makeText(mContext, "插入失败!!", Toast.LENGTH_SHORT)
							.show();
							e.printStackTrace();
						}
						Toast.makeText(mContext, "插入图片成功!!", Toast.LENGTH_SHORT)
								.show();

					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();
		paramAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		paramAlertDialog.show();
	}
	
}
