package cn.wps.moffice.demo;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cn.wps.moffice.demo.floatingview.service.FloatingService;

public class MOfficeDemoActivity extends Activity {
	Button btnFloating;
	Button btnFileManager;
	TextView tv;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		btnFloating = (Button) findViewById(R.id.btnFloating);
		btnFloating.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent service = new Intent();
				service.setClass(MOfficeDemoActivity.this, FloatingService.class);
				if(btnFloating.getText().equals(MOfficeDemoActivity.this.getResources().getString(R.string.floatingview_on)))
				{
					startService(service);
					btnFloating.setText(R.string.floatingview_off);
				}
				else
				{
					service.setClass(MOfficeDemoActivity.this, FloatingService.class);
					stopService(service);
					btnFloating.setText(R.string.floatingview_on);
				}
			}
		});
//		
		
		btnFileManager = (Button) findViewById(R.id.btnFileManager);
		btnFileManager.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.setClassName("cn.wps.moffice.demo", "cn.wps.moffice.demo.fileManager.ListFile");	//以前的包名"com.huawei.svn.hiwork"
				startActivity(intent);
			}
			
		});

		registerReceiver(writerBackKeyDownListerner, filterBackKey);
//		sendBroadcast(new Intent(ENT_WRITER_KEY_BACK_ACTION));
		registerReceiver(writerHomeKeyDownListerner, filterHomeKey);
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.v("stop", "stop");
		// createView();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.v("restart", "restart");

	}
	
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(writerBackKeyDownListerner);
		unregisterReceiver(writerHomeKeyDownListerner);
		super.onDestroy();
	}


	private static final String ENT_WRITER_KEY_BACK_ACTION		= "com.kingsoft.writer.back.key.down";
	IntentFilter filterBackKey = new IntentFilter(ENT_WRITER_KEY_BACK_ACTION);
	private BroadcastReceiver writerBackKeyDownListerner = new BroadcastReceiver() {
		 
	       @Override
	       public void onReceive(Context context, Intent intent) 
	       {
	    	   Log.d("MOfficeDemoActivity", "writerBackKeyDownListerner");
	       }
	 
	  };
	    
	private static final String ENT_WRITER_KEY_HOME_ACTION		= "com.kingsoft.writer.home.key.down";
	IntentFilter filterHomeKey = new IntentFilter(ENT_WRITER_KEY_HOME_ACTION);
	private BroadcastReceiver writerHomeKeyDownListerner = new BroadcastReceiver() {
		 
	       @Override
	       public void onReceive(Context context, Intent intent)
	       {
	    	   Log.d("MOfficeDemoActivity", "writerHomeKeyDownListerner");
	       }
 
    };
}
