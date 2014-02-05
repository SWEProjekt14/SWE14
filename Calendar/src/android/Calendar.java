package de.rwth.swe.calendar;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract.Events;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class Calendar extends CordovaPlugin{

	public static final String ACTION_ADD_CALENDAR_ENTRY = "addCalendarEntry";
	public static final String ACTION_DELETE_CALENDAR_ENTRY = "deleteCalendarEntry";
	
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
		    } else if(ACTION_DELETE_CALENDAR_ENTRY.equals(action)){
		    	Uri deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, args.getLong(0));
		    	int rows = cordova.getActivity().getContentResolver().delete(deleteUri, null, null);
		    	System.out.println("[Calendar] Deleted Rows: " + rows);
		    	callback.success();
		    	return true;
		    } else{
			    callback.error("Invalid action");
			    return false;
		    }
		} catch(Exception e) {
		    System.err.println("[Calendar] Exception: " + e.getMessage());
		    callback.error(e.getMessage());
		    return false;
		} 
	}	
}
