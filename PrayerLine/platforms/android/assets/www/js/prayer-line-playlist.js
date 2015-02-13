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
//alert(messageID);
titleNotification = Notification;
//alert(titleNotification);

          //  continue();
        window.isPlaying(messageID, titleNotification, function(message) {
//alert('in1');
          	//	alert(message);	
          			if (message.playing) {
  //        alert('in2');
                        $('.play_button div').css('background-image', 'url(img/player/pause.svg)');
          keepPlaying();
          //alert('in3');
	            						}
	//            		alert('in4');				
	            						 window.duration(messageID, titleNotification, function(message) {
       

	countdownTimeSecs = message.duration;
	playerCountdounResume();
	//alert('in5');
          			  					    });		
             });
     
    }
};