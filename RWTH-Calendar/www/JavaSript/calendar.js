
var calendar = {
	createEvent: function(title, description, startTime, endTime, successCallback, errorCallback){
		cordoca exec(
			successCallback,
			errorCallback,
			'Calendar',
			'addCalendarEntry',
			[(
				"title": title,
				"description": description,
				"sartTimeMillis": startTime.getDate()
				"endTimeMillis": endTime.getDate()
			)]
		);
	}
}
module.exports = calendar;