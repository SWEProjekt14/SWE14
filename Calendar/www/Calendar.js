cordova.define("Calendar.Calendar", function(require, exports, module) { var calendar =  {
    createEvent: function(title, notes, location, startDate, endDate, successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            'Calendar', 
            'addCalendarEntry', 
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



});
