/**
 *	 文件名：SaveReceiver.java
 * 	创建者:fanguangcheng
 * 	创建时间:2013.7.18
 * 	作用：负责接收wps保存文件时发送的广播，解析并保存起来
 */

package cn.wps.moffice.demo.broad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import cn.wps.moffice.demo.util.Define;
import cn.wps.moffice.demo.util.SettingPreference;

public class SaveReceiver extends BroadcastReceiver
{
	SettingPreference settingPreference;
	/**
	 * 接收wps关闭时发送的广播，同时记录关闭时的参数，以便下一次打开
	 */
    public void onReceive(Context context, Intent intent) 
    {
    	settingPreference = new SettingPreference(context);

        String 	openfile = intent.getExtras().getString("OpenFile");
        String	packageName = intent.getExtras().getString("ThirdPackage");
        String 	savepath = intent.getExtras().getString("SavePath");

        settingPreference.setSettingParam(Define.OPEN_FILE, openfile);
        settingPreference.setSettingParam(Define.THIRD_PACKAGE, packageName);
        settingPreference.setSettingParam(Define.SAVE_PATH, savepath);

        Toast.makeText(context, "最初的文件路径: " + openfile + "\n第三方包名: " 
        		+ packageName + "\n另存为路径: " + savepath, Toast.LENGTH_LONG).show();
    }
}