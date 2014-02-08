var calendar =  {
    createEvent: function(title, location, notes, startDate, endDate, successCallback, errorCallback) {
        cordova.exec(
            successCallback, // success callback function
            errorCallback, // error callback function
            'Calendar', // mapped to our native Java class called "Calendar"
            'addCalendarEntry', // with this action name
            [{                  // and this array of custom arguments to create our entry
                "title": title,
                "description": notes,
                "eventLocation": location,
                "startTimeMillis": startDate.getTime(),
                "endTimeMillis": endDate.getTime()
            }]
        );
    },
    deleteEvent: function(eventId, successCallback, errorCallback){
    	cordova.exec(
                successCallback,
                errorCallback, 
                'Calendar',
                'deleteCalendarEntry',
                [eventId] 
    	);
    },
    searchEvent: function(title, notes, location, startDate, endDate, successCallback, errorCallback){
    	cordova.exec(
                successCallback,
                errorCallback, 
                'Calendar',
                'searchCalendarEntry',
                [title, notes, location, startDate.getTime(), endDate.getTime()] 
        );
    }
}
module.exports = calendar;