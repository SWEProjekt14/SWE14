var sayhello = {
		talk: function(name, successfunction, failfunction) {
			cordova.exec(successfunction, failfunction, 
			"HelloWorld", "actiontoexecute", 
			[name]);}
}
module.exports=sayhello;