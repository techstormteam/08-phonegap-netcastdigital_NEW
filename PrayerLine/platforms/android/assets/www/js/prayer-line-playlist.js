var app = {
    // Application Constructor
    initialize: function() {
        this.bindEvents();
    },
    // Bind Event Listeners
    //
    // Bind any events that are required on startup. Common events are:
    // 'load', 'deviceready', 'offline', and 'online'.
    bindEvents: function() {
        document.addEventListener('deviceready', this.onDeviceReady, false);
    },
    // deviceready Event Handler
    //
    // The scope of 'this' is the event. In order to call the 'receivedEvent'
    // function, we must explicity call 'app.receivedEvent(...);'
    onDeviceReady: function() {
        app.receivedEvent('deviceready');
    },
    
    sipRegister: function(response) {
    	var sipUsername = response;
    	global.set('telno', sipUsername);
    },
    
    // Update DOM on a Received Event
    receivedEvent: function(id) {
   var message = 'playbackPlayList-' + entity_id + '-' + user_id;
   var messageID = message;
          //  continue();
          window.isPlaying(messageID, function(message) {

          			if (message.playing) {
                        $('.play_button div').css('background-image', 'url(img/player/pause.svg)');
        				  keepPlaying();
	            						}
		            window.duration(messageID, function(message) {
						countdownTimeSecs = message.duration;
 						playerCountdounResume();          
 					  	});		
             });
     
    }
};
