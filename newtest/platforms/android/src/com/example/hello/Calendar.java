package com.example.hello;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;

public class Calendar extends CordovaPlugin{

	public static final String ACTION_ADD_CALENDAR_ENTRY = "addCalendarEntry";
	
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callback){
		try {
		    if (ACTION_ADD_CALENDAR_ENTRY.equals(action)) {
		             JSONObject arg_object = args.getJSONObject(0);
		             Intent calIntent = new Intent(Intent.ACTION_EDIT)
		        .setType("vnd.android.cursor.item/event")
		        .putExtra("beginTime", arg_object.getLong("startTimeMillis"))
		        .putExtra("endTime", arg_object.getLong("endTimeMillis"))
		        .putExtra("title", arg_object.getString("title"))
		        .putExtra("description", arg_object.getString("description"))
		        .putExtra("eventLocation", arg_object.getString("eventLocation"));
		 
		       this.cordova.getActivity().startActivity(calIntent);
		       callback.success();
		       return true;
		    } else{
			    callback.error("Invalid action");
			    return false;
		    }
		} catch(Exception e) {
		    System.err.println("Exception: " + e.getMessage());
		    callback.error(e.getMessage());
		    return false;
		} 
	}	
}
