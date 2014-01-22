package com.example.hello;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

public class HelloWorldPlugin extends CordovaPlugin{
	
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException{
		
		if("actiontoexecute".equals(action)){
			callbackContext.success("Hallo " + args.getString(0));	
			return true;
		}
		callbackContext.success("Ist das ein Error?");
		return false;
	}
}
