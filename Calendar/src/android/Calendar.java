package de.rwth.swe.calendar;

import java.util.Date;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract.Events;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class Calendar extends CordovaPlugin{

	public static final String ACTION_ADD_CALENDAR_ENTRY = "addCalendarEntry";
	public static final String ACTION_DELETE_CALENDAR_ENTRY = "deleteCalendarEntry";
	public static final String ACTION_EDIT_CALENDAR_ENTRY = "editCalendarEntry";
	public static final String ACTION_SEARCH_CALENDAR_ENTRY = "searchCalendarEntry";
	
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
		    } else if(ACTION_SEARCH_CALENDAR_ENTRY.equals(action)){
		    	String title = args.getString(0);
		    	if(title == null || title.isEmpty()) title = "%";
		    	String notes = args.getString(1);
		    	if(notes == null || notes.isEmpty()) notes = "%";
		    	String location = args.getString(2);
		    	if(location == null || location.isEmpty()) location = "%";
		    	String start = args.getString(3);
		    	if(start == null || start.isEmpty()) start = "%";
		    	String end = args.getString(4);
		    	if(end == null || end.isEmpty()) end = "%";
		    	// TODO: Argumente beachten
		    	String search = "("+Events.TITLE+" LIKE ? AND "+
		    			Events.DESCRIPTION + " LIKE ? AND " +
		    			Events.EVENT_LOCATION + " LIKE ? AND "+
		    			Events.DTSTART + " LIKE ? AND " + 
		    			Events.DTEND + " LIKE ?)";
		    	Cursor cursor = cordova.getActivity().getContentResolver()
		    			.query(Events.CONTENT_URI, new String[]{Events._ID}, null, null, null);
//    			.query(Events.CONTENT_URI, new String[]{Events._ID}, search, new String[]{title, notes, location, start, end}, null);
		    	cursor.moveToFirst();

		    	JSONArray retArray = new JSONArray();
		    	if(cursor.getCount()>0 ){
		    		retArray.put(cursor.getLong(0));
		    		while(cursor.moveToNext()){
				    	retArray.put(cursor.getLong(0));
		    		}
		    	}
		    	callback.success(retArray);
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
