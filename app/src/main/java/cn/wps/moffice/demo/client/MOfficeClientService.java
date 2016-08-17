package cn.wps.moffice.demo.client;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import cn.wps.moffice.client.OfficeServiceClient;

public class MOfficeClientService extends Service
{
	protected static final String TAG = MOfficeClientService.class.getSimpleName();
	
	public static final String BROADCAST_ACTION = "cn.wps.moffice.broadcast.action.serviceevent";

	protected final Handler handler = new Handler();
	protected final Intent intent = new Intent(BROADCAST_ACTION);
	
	protected final OfficeServiceClient.Stub mBinder = new OfficeServiceClientImpl(this);
	
	public MOfficeClientService()
	{}
	
	@Override
	public void onCreate()
	{
		Log.i(TAG, "onCreate(): " + this.hashCode());
	}
	
	@Override
	public IBinder onBind(Intent intent) 
	{
		Log.i(TAG, "onBind(): " + this.hashCode() + ", " + intent.toString());
		return mBinder;
	}
	
	@Override
	public boolean onUnbind(Intent intent)
	{
		Log.i(TAG, "onUnbind(): " + this.hashCode() + ", " + intent.toString());
		return super.onUnbind(intent);
	}
	
	@Override
	public void onStart(Intent intent, int startId) 
	{
		handler.removeCallbacks(sendUpdatesToUI);
		handler.postDelayed(sendUpdatesToUI, 1000);
	}
	
    protected Runnable sendUpdatesToUI = new Runnable()
    {
    	public void run()
    	{
    		displayServiceStatus();
    		handler.postDelayed(sendUpdatesToUI, 1000);
    	}
    };  
	
    private void displayServiceStatus()
    {
    	// sendBroadcast( intent );
        // Intent intent = new Intent( this, MOfficeClientActivity.class );
        // intent.setAction( Intent.ACTION_VIEW );
        // intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        // intent.setFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
        // startActivity( intent );
    }

	@Override
	public void onDestroy()
	{
		Log.i(TAG, "onDestroy(): " + this.hashCode());
		handler.removeCallbacks(sendUpdatesToUI);
	}
}
