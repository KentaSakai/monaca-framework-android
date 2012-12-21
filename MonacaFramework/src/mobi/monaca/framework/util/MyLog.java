package mobi.monaca.framework.util;

import mobi.monaca.framework.psedo.BuildConfig;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyLog {
	public static final String PREFIX = "[MyLog] ";
	public static void i(String TAG, String msg) {
		Log.i(TAG, "debug flag = " + BuildConfig.DEBUG);
		if (BuildConfig.DEBUG) {
			Log.i(PREFIX + TAG, msg);
		}
	}

	public static void v(String TAG, String msg) {
		Log.i(TAG, "debug flag = " + BuildConfig.DEBUG);
		if (BuildConfig.DEBUG) {
			Log.v(PREFIX + TAG, msg);
		}
	}

	public static void w(String TAG, String msg) {
		Log.i(TAG, "debug flag = " + BuildConfig.DEBUG);
		if (BuildConfig.DEBUG) {
			Log.w(PREFIX + TAG, msg);
		}
	}

	public static void d(String TAG, String msg) {
		if (BuildConfig.DEBUG) {
			Log.d(PREFIX + TAG, msg);
		}
	}

	public static void e(String TAG, String msg) {
		if (BuildConfig.DEBUG) {
			Log.e(PREFIX + TAG, msg);
		}
	}

	// send debuglog to debbuger and server
	public static void sendBloadcastDebugLog(Context context, String broadcastMessage, String debugType, String logLevel) {
		Intent broadcastIntent = new Intent("log_message_action");
		broadcastIntent.putExtra("message", broadcastMessage);
		broadcastIntent.putExtra("logType", debugType);
		broadcastIntent.putExtra("logLevel", logLevel);
		context.sendBroadcast(broadcastIntent);
	}
}
