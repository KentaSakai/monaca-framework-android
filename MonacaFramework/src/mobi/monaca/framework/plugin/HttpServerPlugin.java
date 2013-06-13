package mobi.monaca.framework.plugin;

import java.io.IOException;

import mobi.monaca.framework.util.MyLog;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpServerPlugin extends CordovaPlugin{
	
	private static final String TAG = HttpServerPlugin.class.getSimpleName();
	private MonacaLocalServer localServer;

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		MyLog.v(TAG, "HttpServerPlugin exec action:" + action + ", args:" + args);
		if(action.equalsIgnoreCase("start")){
			if(args.length() < 2){
				callbackContext.error("either documentRoot or params is not supplied");				
			}else{
				try{
					String rootDir = args.getString(0);
					JSONObject params = args.getJSONObject(1);
					int port = params.getInt("port");
					localServer = new MonacaLocalServer(cordova.getActivity(), rootDir, port);
					localServer.start();
					callbackContext.success("server started at port " + port);
				}catch (JSONException e) {
					callbackContext.error(e.getMessage());
					e.printStackTrace();
				} catch (Exception e) {
					callbackContext.error("Cannot start server. error: " + e.getMessage());
					e.printStackTrace();
				}
			}
			return true;
		}else if(action.equalsIgnoreCase("stop")){
			if(localServer != null){
				localServer.stop();
				callbackContext.success("stopped server");
			}
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public void onDestroy() {
		MyLog.i(TAG, "Monaca HttpServer plugin onDestroy");
		if(localServer != null){
			MyLog.i(TAG, "closing local server");
			localServer.stop();
		}
		super.onDestroy();
	}
}
