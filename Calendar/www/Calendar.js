var calendar =  {
    createEvent: function(title, notes, location, startDate, endDate, successCallback, errorCallback) {
        cordova.exec(
            successCallback, // success callback function
            errorCallback, // error callback function
            'Calendar', // mapped to our native Java class called "Calendar"
            'addCalendarEntry', // with this action name
            [title, notes, location, startDate.getTime(), endDate.getTime()]
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