package de.rwth.swe.calendar;

import java.util.ArrayList;
import java.util.List;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class Calendar extends CordovaPlugin {

	public static final String ACTION_ADD_CALENDAR_ENTRY = "addCalendarEntry";
	public static final String ACTION_ADD_CALENDAR_ENTRY_INTERACTIVE = "addCalendarEntryInteractive";
	public static final String ACTION_DELETE_CALENDAR_ENTRY = "deleteCalendarEntry";
	public static final String ACTION_EDIT_CALENDAR_ENTRY = "editCalendarEntry";
	public static final String ACTION_SEARCH_CALENDAR_ENTRY = "searchCalendarEntry";
	public static final String ACTION_SEARCH_CALENDAR_ENTRY_ID = "searchCalendarEntryId";
	
	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callback) {
		try {
			if (ACTION_ADD_CALENDAR_ENTRY_INTERACTIVE.equals(action)) {
				Intent calIntent = new Intent(Intent.ACTION_EDIT)
						.setType("vnd.android.cursor.item/event")
						.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, args.getLong(3))
						.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, args.getLong(4))
						.putExtra(Events.TITLE, args.getString(0))
						.putExtra(Events.DESCRIPTION, args.getString(1))
						.putExtra(Events.EVENT_LOCATION, args.getString(2));

				this.cordova.getActivity().startActivity(calIntent);
				
				callback.success();
				return true;
			} else if (ACTION_ADD_CALENDAR_ENTRY.equals(action)){
				ContentResolver cr = this.cordova.getActivity().getContentResolver();
				ContentValues values = new ContentValues();
				values.put(Events.CALENDAR_ID, 1);
				values.put(Events.EVENT_TIMEZONE, java.util.TimeZone.getDefault().getID());
				values.put(Events.TITLE, args.getString(0));
				values.put(Events.DESCRIPTION, args.getString(1));
				values.put(Events.EVENT_LOCATION, args.getString(2));
				values.put(Events.DTSTART, args.getLong(3));
				values.put(Events.DTEND, args.getLong(4));
				cr.insert(Events.CONTENT_URI, values);
				
				callback.success();
				return true;
			} else if (ACTION_DELETE_CALENDAR_ENTRY.equals(action)) {
				Uri deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, args.getLong(0));
				cordova.getActivity().getContentResolver().delete(deleteUri, null, null);
				callback.success();
				return true;
			} else if (ACTION_SEARCH_CALENDAR_ENTRY_ID.equals(action)) {
				String dBegin = "";
				String dEnd = "";
				List<String> params = new ArrayList<String>();
				String title = args.getString(0);
				if (isNull(title))
					title = "%";
				String notes = args.getString(1);
				if (isNull(notes))
					notes = "%";
				String location = args.getString(2);
				if (isNull(location))
					location = "%";
				String start = args.getString(3);
				if (!(start == null || start.isEmpty() || start.equals("0")))
					dBegin = (" AND " + Events.DTSTART + " = " + start);
				String end = args.getString(4);
				if (!(end == null || end.isEmpty() || end.equals("0")))
					dEnd = (" AND " + Events.DTEND + " = " + end);

				params.add(title);
				params.add(notes);
				params.add(location);
				String search = "(" + Events.TITLE + " LIKE ? AND "
						+ Events.DESCRIPTION + " LIKE ? AND "
						+ Events.EVENT_LOCATION + " LIKE ?" 
						+ dBegin + dEnd + ")";

				Cursor cursor = cordova.getActivity().getContentResolver()
						.query(Events.CONTENT_URI, new String[] { Events._ID },
								search, params.toArray(new String[0]), null);
				cursor.moveToFirst();

				JSONArray retArray = new JSONArray();
				if (cursor.getCount() > 0) {
					retArray.put(cursor.getLong(0));
					while (cursor.moveToNext()) {
						retArray.put(cursor.getLong(0));
					}
				}
				callback.success(retArray);
				return true;
			} else if(ACTION_SEARCH_CALENDAR_ENTRY.equals(action)){
				// TODO:
				callback.error("Function not implemented");
				return false;
			} else if(ACTION_EDIT_CALENDAR_ENTRY.equals(action)){
				
				Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, args.getLong(0));
				ContentResolver cr = this.cordova.getActivity().getContentResolver();
				ContentValues cv = new ContentValues();

				if(!isNull(args.getString(1))) {
					cv.put(Events.TITLE, args.getString(1));
				}
				if(!isNull(args.getString(2))) {
					cv.put(Events.DESCRIPTION, args.getString(2));
				}
				if(!isNull(args.getString(3))) {
					cv.put(Events.EVENT_LOCATION, args.getString(3));
				}
				if(!isNull(args.getString(4)) && !args.getString(4).equals("0")) {
					cv.put(Events.DTSTART, args.getLong(4));
				}
				if(!isNull(args.getString(5)) && !args.getString(5).equals("0")) {
					cv.put(Events.DTEND, args.getLong(5));
				}
				cr.update(uri, cv, null, null);
				callback.success();
				return true;
			}
			else {
				callback.error("Invalid action");
				return false;
			}
		} catch (Exception e) {
			System.err.println("[Calendar] Exception: " + e.getMessage());
			callback.error(e.getMessage());
			return false;
		}
	}
	
	private boolean isNull(String test){
		return test == null || test.equals("null");
	}
}
