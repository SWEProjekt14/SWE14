var calendar =  {
    createEvent: function(title, notes, location, startDate, endDate, successCallback, errorCallback) {
        cordova.exec(
	            successCallback,
	            errorCallback,
	            'Calendar', 
	            'addCalendarEntry', 
	            [title, notes, location, startDate.getTime(), endDate.getTime()]
        );
    },
    createEventInteractive: function(title, notes, location, startDate, endDate, successCallback, errorCallback) {
        cordova.exec(
	            successCallback,
	            errorCallback,
	            'Calendar', 
	            'addCalendarEntryInteractive', 
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
    searchEventId: function(title, notes, location, startDate, endDate, successCallback, errorCallback){
    	cordova.exec(
                successCallback,
                errorCallback, 
                'Calendar',
                'searchCalendarEntryId',
                [title, notes, location, startDate.getTime(), endDate.getTime()] 
        );
    },
    searchEvent: function(eventId, successCallback, errorCallback){
    	cordova.exec(
                successCallback,
                errorCallback, 
                'Calendar',
                'searchCalendarEntry',
                [eventId] 
        );
    },
    editEvent: function(id,title, notes, location, startDate, endDate, successCallback, errorCallback) {
        cordova.exec(
	            successCallback,
	            errorCallback,
	            'Calendar', 
	            'editCalendarEntry', 
	            [id,title, notes, location, startDate.getTime(), endDate.getTime()]
        );
    }
}
module.exports = calendar;