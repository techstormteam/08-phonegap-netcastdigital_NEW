var loginFacebook = function () {
    if (!window.cordova) {
        var appId = prompt("Enter FB Application ID", "");
        facebookConnectPlugin.browserInit(appId);
    }
    
    facebookConnectPlugin.login( ["email"],
                                function (response) { 
    								//alert(JSON.stringify(response));
    								//apiTest();
    								var firstName = "";
    								var lastName = "";
    								var email = "";
    								var phone = "";
    								var password = response.authResponse.userID;
    								var prayerline = "";
    								global.api('register_user', {
    									fname: firstName, 
    									lname: lastName, 
    									email: email, 
    									telno: phone, 
    									password: password, 
    									prayerline: prayerline,
    									plugin: 'facebook'
    								}, onSuccessFacebookLogin);
    							},
                                function (response) { alert(JSON.stringify(response)) });
    apiTest();
}

var showDialog = function () {
    facebookConnectPlugin.showDialog( { method: "feed" },
                                     function (response) { alert(JSON.stringify(response)) },
                                     function (response) { alert(JSON.stringify(response)) });
}

var getSignUpInfo = function () {
    facebookConnectPlugin.api( "me/?fields=id,email", [],
                              function (response) {
    							  signUpPLUser(response);
    						  },
                              function (response) { alert(JSON.stringify(response)) });
}

function signUpPLUser(response) {
	
}

var apiTest = function () {
    facebookConnectPlugin.api( "me/?fields=id,email", [],
                              function (response) { alert(JSON.stringify(response)) },
                              function (response) { alert(JSON.stringify(response)) });
}

var logPurchase = function () {
    facebookConnectPlugin.logPurchase(1.99, "USD",
                                      function (response) { alert(JSON.stringify(response)) },
                                      function (response) { alert(JSON.stringify(response)) });
}

var logEvent = function () {
    // For more information on AppEvent param structure see
    // https://developers.facebook.com/docs/ios/app-events
    // https://developers.facebook.com/docs/android/app-events
    facebookConnectPlugin.logEvent("Purchased",
                                   {
                                   NumItems: 1,
                                   Currency: "USD",
                                   ContentType: "shoes",
                                   ContentID: "HDFU-8452"
                                   }, null,
                                   function (response) { alert(JSON.stringify(response)) },
                                   function (response) { alert(JSON.stringify(response)) });
}

var getAccessToken = function () {
    facebookConnectPlugin.getAccessToken(
                                         function (response) { alert(JSON.stringify(response)) },
                                         function (response) { alert(JSON.stringify(response)) });
}

var getStatus = function () {
    facebookConnectPlugin.getLoginStatus(
                                         function (response) { alert(JSON.stringify(response)) },
                                         function (response) { alert(JSON.stringify(response)) });
}

var logout = function () {
    facebookConnectPlugin.logout(
                                 function (response) { alert(JSON.stringify(response)) },
                                 function (response) { alert(JSON.stringify(response)) });
}
