loginGooglePlus = function(){
	//Login with Google Api Client Plugin
	if(isAndroid() || isiOS()){
		navigator.googleConnectPlugin.googleLogin(function(userProfile){
			//alert('User Name'+JSON.stringify(userProfile));
			//localStorage.setItem('UserProfile',userProfile);
			getSignUpInfoGooglePlus(userProfile);
		},function(error){
			alert('Error :'+error);
		});
	}
	else {
		console.log('no feature');
		
	}
}
getProfile = function(){
	if(localStorage.getItem('UserProfile')!=null)
		alert('User Details'+localStorage.getItem('UserProfile'));
	else
		alert('User Details cannot be retrieved');
}

var getSignUpInfoGooglePlus = function (userProfile) {
	signUpPLUserGooglePlus(userProfile);
}

function signUpPLUserGooglePlus(response) {
	//alert(JSON.stringify(response));
	var firstName = response.GivenName;
	var lastName = response.FamilyName;
	var email = response.Email;
	var phone = ""; // need get
	var password = response.Id;
	var prayerline = "";
	global.set("try_email", email);
	global.set("try_password", password);
	
	global.api('register_user', {
		fname: firstName, 
		lname: lastName, 
		email: email, 
		telno: phone, 
		password: password, 
		prayerline: prayerline,
		plugin: 'google'
	}, onSuccessRegisterPLUserGooglePlus);
}

function onSuccessRegisterPLUserGooglePlus(response) {
	if (response.result === "error") {
		sweetAlert("Oops...", response.details, "error");
	} else {
		var tryEmail = global.get("try_email");
		var tryPassword = global.get("try_password");
		
		global.set('auto_login', false);
        global.api('login', {username: tryEmail, password: tryPassword}, login);
	}
}