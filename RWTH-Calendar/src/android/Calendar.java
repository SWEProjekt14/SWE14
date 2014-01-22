/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package com.plugin.calendar;

import android.content.Intent;
import android.os.Bundle;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class Calendar extends CordovaPlugin
{
	public static final String ACTION_ADD_CALENDAR_ENTRY = "addCalendarEntry";
	
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext){
    	try{
    		if(ACTION_ADD_CALENDAR_ENTRY.equals(action)){
    			JSONObject argObject = args.getJSONObject(0);
    			Intent calIntent = new Intent(Intent.ACTION_EDIT);
    			calIntent.setType("vnd.android.cursor.item/event");
    			calIntent.putExtra("beginTime", argObject.getLong("startTimeMillis"));
    			calIntent.putExtra("endTime", argObject.getLong("endTimeMillis"));
    			calIntent.putExtra("title", argObject.getString("title"));
    			calIntent.putExtra("description", argObject.getString("description"));
    			
    			this.cordova.getActivity().startActivity(calIntent);
    			callbackContext.success();
    			return true;
    		}
    		callbackContext.error("Invalid action");
    		return false;
    	} catch(Exception e){
    		System.err.println("Exception: " + e.getMessage());
    		callbackContext.error(e.getMessage());
    		return false;
    	}
    }
}

