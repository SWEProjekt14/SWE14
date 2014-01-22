var sayhello = function(name, successfunction, failfunction) {
			cordova.exec(successfunction, failfunction}, 
			"helloworldservice", "actiontoexecute", 
			[name]);};
