#import <GooglePlus/GooglePlus.h>
#import <GoogleOpenSource/GoogleOpenSource.h>
#import "Cordova/CDVViewController.h"

@interface GoogleConnectPlugin : CDVPlugin<GPPSignInDelegate>

//Method to handle the google plus login
- (void) cordovaGooglePlusLogin:(CDVInvokedUrlCommand *)command;
- (void) cordovaGooglePlusLogout:(CDVInvokedUrlCommand *)command;
#pragma -UtilMethods
-(NSDictionary *) getProfileInformation:(GTLPlusPerson *)person;
@end
