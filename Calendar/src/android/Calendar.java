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
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.renderscript.Sampler.Value;
import android.widget.Toast;

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
				// TODO: Testen
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
				if (title == null || title.isEmpty())
					title = "%";
				String notes = args.getString(1);
				if (notes == null || notes.isEmpty())
					notes = "%";
				String location = args.getString(2);
				if (location == null || location.isEmpty())
					location = "%";
				String start = args.getString(3);
				if (!(start == null || start.isEmpty()))
					dBegin = (" AND " + Events.DTSTART + " = " + start);
				String end = args.getString(4);
				if (!(end == null || end.isEmpty()))
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
				
				if(args.getString(1)!=null) {
					cv.put(Events.TITLE, args.getString(1));
				}
				if(args.getString(2)!=null) {
					cv.put(Events.DESCRIPTION, args.getString(2));
				}
				if(args.getString(3)!=null) {
					cv.put(Events.EVENT_LOCATION, args.getString(3));
				}
				if(args.getString(4)!=null) {
					cv.put(Events.DTSTART, args.getLong(4));
				}
				if(args.getString(5)!=null) {
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
}
