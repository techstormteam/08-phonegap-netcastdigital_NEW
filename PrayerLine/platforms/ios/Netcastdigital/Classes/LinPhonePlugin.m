//
//  LinPhonePlugin.m
//  Netcastdigital
//
//  Created by Macbook air on 11/11/14.
//
//

#import "LinPhonePlugin.h"

#include "LinphoneManager.h"
#include "linphone/linphonecore.h"

@implementation LinPhonePlugin

#define GENERIC_DOMAIN @"cloud.netcastdigital.net"
#define NOT_IN_CALL @"NOT-IN-CALL"

- (void) callSip:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        NSString *callTo = [command.arguments objectAtIndex:0];
        NSString *sipUsername = [command.arguments objectAtIndex:1];
        NSString *password = [command.arguments objectAtIndex:2];
        NSString *domain = GENERIC_DOMAIN;
        [self doRegisterSip:sipUsername password:password domain:domain];
        [self doSip:[NSString stringWithFormat:@"sip:%@@%@", callTo, domain]];
        [self initUISpeaker];
        [[LinphoneManager instance] setSpeakerEnabled:TRUE];
        
        NSDictionary *jsonObj = [ [NSDictionary alloc]
                                 initWithObjectsAndKeys :
                                 @"true", @"success",
                                 nil
                                 ];
        
        CDVPluginResult *pluginResult = [ CDVPluginResult
                                         resultWithStatus    : CDVCommandStatus_OK
                                         messageAsDictionary : jsonObj
                                         ];
        
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

- (void) cancelSip:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        NSString *sipUsername = [command.arguments objectAtIndex:0];
        NSString *domain = GENERIC_DOMAIN;
        [self doHangUp];
        [LinPhonePlugin doSignOut:sipUsername domain:domain];
        
        NSDictionary *jsonObj = [ [NSDictionary alloc]
                                 initWithObjectsAndKeys :
                                 @"true", @"success",
                                 nil
                                 ];
        
        CDVPluginResult *pluginResult = [ CDVPluginResult
                                         resultWithStatus    : CDVCommandStatus_OK
                                         messageAsDictionary : jsonObj
                                         ];
        
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}
- (void) registerSip:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        NSString *sipUsername = [command.arguments objectAtIndex:0];
        NSString *password = [command.arguments objectAtIndex:1];
        NSString *domain = GENERIC_DOMAIN;
        [self doRegisterSip:sipUsername password:password domain:domain];
        
        NSDictionary *jsonObj = [ [NSDictionary alloc]
                                 initWithObjectsAndKeys :
                                 @"true", @"success",
                                 nil
                                 ];
        
        CDVPluginResult *pluginResult = [ CDVPluginResult
                                         resultWithStatus    : CDVCommandStatus_OK
                                         messageAsDictionary : jsonObj
                                         ];
        
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}
- (void) deregisterSip:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        NSString *sipUsername = [command.arguments objectAtIndex:0];
        NSString *status = [command.arguments objectAtIndex:1];
        NSString *domain = GENERIC_DOMAIN;
        [LinPhonePlugin doSignOut:sipUsername domain:domain];
        if ([NOT_IN_CALL isEqualToString:status]) {
            [LinPhonePlugin doSignOut:sipUsername domain:domain];
        }
        
        NSDictionary *jsonObj = [ [NSDictionary alloc]
                                 initWithObjectsAndKeys :
                                 @"true", @"success",
                                 nil
                                 ];
        
        CDVPluginResult *pluginResult = [ CDVPluginResult
                                         resultWithStatus    : CDVCommandStatus_OK
                                         messageAsDictionary : jsonObj
                                         ];
        
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}
- (void) backWind:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        [self doDialDtmf:'4'];
        
        NSDictionary *jsonObj = [ [NSDictionary alloc]
                                 initWithObjectsAndKeys :
                                 @"true", @"success",
                                 nil
                                 ];
        
        CDVPluginResult *pluginResult = [ CDVPluginResult
                                         resultWithStatus    : CDVCommandStatus_OK
                                         messageAsDictionary : jsonObj
                                         ];
        
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}
- (void) forwardWind:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        [self doDialDtmf:'6'];
        
        NSDictionary *jsonObj = [ [NSDictionary alloc]
                                 initWithObjectsAndKeys :
                                 @"true", @"success",
                                 nil
                                 ];
        
        CDVPluginResult *pluginResult = [ CDVPluginResult
                                         resultWithStatus    : CDVCommandStatus_OK
                                         messageAsDictionary : jsonObj
                                         ];
        
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}
- (void) pauseSip:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        [self doDialDtmf:'#'];
        
        NSDictionary *jsonObj = [ [NSDictionary alloc]
                                 initWithObjectsAndKeys :
                                 @"true", @"success",
                                 nil
                                 ];
        
        CDVPluginResult *pluginResult = [ CDVPluginResult
                                         resultWithStatus    : CDVCommandStatus_OK
                                         messageAsDictionary : jsonObj
                                         ];
        
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}
- (void) signOut:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        NSString *sipUsername = [command.arguments objectAtIndex:0];
        NSString *domain = GENERIC_DOMAIN;
        [LinPhonePlugin doSignOut:sipUsername domain:domain];
        
        NSDictionary *jsonObj = [ [NSDictionary alloc]
                                 initWithObjectsAndKeys :
                                 @"true", @"success",
                                 nil
                                 ];
        
        CDVPluginResult *pluginResult = [ CDVPluginResult
                                         resultWithStatus    : CDVCommandStatus_OK
                                         messageAsDictionary : jsonObj
                                         ];

        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}
//------------

- (void) doRegisterSip:(NSString *)sipUsername password:(NSString*)password domain:(NSString*)domain {
    NSString *sipAddress = [NSString stringWithFormat:@"%@@%@", sipUsername, domain];
    
    LinphoneCore *lc = [LinphoneManager getLc];
    
    if (linphone_core_is_network_reachable(lc)) {
        
        // Get account index.
        const MSList *authInfoList = linphone_core_get_auth_info_list(lc);
        int nbAccounts = ms_list_size(authInfoList);
        
//        if (nbAccounts > 0
//            && [NOT_REGISTERED isEqualToString:registerStatus]) {
            [LinPhonePlugin doSignOut:sipUsername domain:domain];
//        }
        
        if (nbAccounts == 0) { // User haven't registered in linphone
            [self doLogIn:sipUsername password:password domain:domain];
            [[LinphoneManager instance] refreshRegisters];
        } else {
            LinphoneCoreSettingsStore *settingsStore = [[LinphoneCoreSettingsStore alloc] init];
            [settingsStore switchAccount:sipUsername];
        }
        
        
    }
}

- (int) getProxyConfigIndex:(LinphoneProxyConfig*) proxyConfig {
    int resultIndex = -1;
    LinphoneCore *lc = [LinphoneManager getLc];
    const MSList *proxyConfigList = linphone_core_get_proxy_config_list(lc);
    int size = ms_list_size(proxyConfigList);
    for (int index = 0; index < size; index++) {
        LinphoneProxyConfig *proxyCfg = ms_list_nth_data(proxyConfigList, index);
        if (linphone_proxy_config_get_identity(proxyCfg) == linphone_proxy_config_get_identity(proxyConfig)) {
            resultIndex = index;
            break;
        }
    }
    return resultIndex;
}

- (void) doSip:(NSString *)sipUrl {
    if( [sipUrl length] > 0){
        [[LinphoneManager instance] call:sipUrl displayName:sipUrl transfer:FALSE];
    }
}

- (void) doDialDtmf:(char)keyCode {
    linphone_core_send_dtmf([LinphoneManager getLc], keyCode);
}

- (void) doHangUp {
    LinphoneCore* lc = [LinphoneManager getLc];
    LinphoneCall* currentcall = linphone_core_get_current_call(lc);
    if (linphone_core_is_in_conference(lc) || // In conference
        (linphone_core_get_conference_size(lc) > 0 // && [UIHangUpButton callCount:lc] == 0
         ) // Only one conf
        ) {
        linphone_core_terminate_conference(lc);
    } else if(currentcall != NULL) { // In a call
        linphone_core_terminate_call(lc, currentcall);
    } else {
        const MSList* calls = linphone_core_get_calls(lc);
        if (ms_list_size(calls) == 1) { // Only one call
            linphone_core_terminate_call(lc,(LinphoneCall*)(calls->data));
        }
    }
}

+ (void) doSignOut:(NSString*)sipUsername domain:(NSString*)domain {
    if ([LinphoneManager isLcReady]) {
        LinphoneCore *lc = [LinphoneManager getLc];
        LinphoneProxyConfig* proxyCfg = NULL;
        linphone_core_get_default_proxy(lc, &proxyCfg);
        if (proxyCfg != NULL) {
            
            linphone_proxy_config_edit(proxyCfg);
            linphone_proxy_config_enable_register(proxyCfg, false);
            linphone_proxy_config_done(proxyCfg);
            
            linphone_core_get_default_proxy([LinphoneManager getLc], &proxyCfg);
            [LinPhonePlugin doProxyConfigUpdate:proxyCfg];
            
        }
    }
}

- (void) doLogIn:(NSString*)sipUsername password:(NSString*)password domain:(NSString*)domain {
    linphone_core_clear_all_auth_info([LinphoneManager getLc]);
    linphone_core_clear_proxy_config([LinphoneManager getLc]);
    LinphoneProxyConfig* proxyCfg = linphone_core_create_proxy_config([LinphoneManager getLc]);
    /*default domain is supposed to be preset from linphonerc*/
    NSString* identity = [NSString stringWithFormat:@"sip:%@@%@",sipUsername, domain];
    linphone_proxy_config_set_identity(proxyCfg,[identity UTF8String]);
    LinphoneAuthInfo* auth_info =linphone_auth_info_new([sipUsername UTF8String]
                                                        ,[sipUsername UTF8String]
                                                        ,[password UTF8String]
                                                        ,NULL
                                                        ,NULL
                                                        ,linphone_proxy_config_get_domain(proxyCfg));
    linphone_core_add_auth_info([LinphoneManager getLc], auth_info);
    linphone_core_add_proxy_config([LinphoneManager getLc], proxyCfg);
    linphone_core_set_default_proxy([LinphoneManager getLc], proxyCfg);
}

- (NSMutableArray*) findAuthIndexOf:(NSString*)sipAddress {
    LinphoneCore *lc = [LinphoneManager getLc];
    const MSList *accountList = linphone_core_get_auth_info_list(lc);
    int nbAccounts = ms_list_size(accountList);
    NSMutableArray *indexes = [[NSMutableArray alloc] init];
    for (int index = 0; index < nbAccounts; index++)
    {
        LinphoneAuthInfo* authInfo = ms_list_nth_data(accountList, index);
        
        const char *accountUsername = linphone_auth_info_get_username(authInfo);
        const char *accountDomain = linphone_auth_info_get_domain(authInfo);
        NSString *identity = [NSString stringWithFormat:@"%s@%s", accountUsername, accountDomain];
        if ([identity isEqualToString:sipAddress]) {
            [indexes addObject:[NSNumber numberWithInteger:index]];
        }
    }
    return indexes;
}

+ (void)doProxyConfigUpdate: (LinphoneProxyConfig*) config {
    LinphoneRegistrationState state = LinphoneRegistrationNone;
    NSString* message = nil;
    //    UIImage* image = nil;
    LinphoneCore* lc = [LinphoneManager getLc];
    LinphoneGlobalState gstate = linphone_core_get_global_state(lc);
    
    if( gstate == LinphoneGlobalConfiguring ){
        message = NSLocalizedString(@"Fetching remote configuration", nil);
    } else if (config == NULL) {
        state = LinphoneRegistrationNone;
        if(linphone_core_is_network_reachable([LinphoneManager getLc]))
            message = NSLocalizedString(@"No SIP account configured", nil);
        else
            message = NSLocalizedString(@"Network down", nil);
    } else {
        state = linphone_proxy_config_get_state(config);
        
        switch (state) {
            case LinphoneRegistrationOk:
                message = NSLocalizedString(@"Registered", nil); break;
            case LinphoneRegistrationNone:
            case LinphoneRegistrationCleared:
                message =  NSLocalizedString(@"Not registered", nil); break;
            case LinphoneRegistrationFailed:
                message =  NSLocalizedString(@"Registration failed", nil); break;
            case LinphoneRegistrationProgress:
                message =  NSLocalizedString(@"Registration in progress", nil); break;
            default: break;
        }
    }
    
    //    registrationStateLabel.hidden = NO;
    switch(state) {
        case LinphoneRegistrationFailed:
            //            registrationStateImage.hidden = NO;
            //            image = [UIImage imageNamed:@"led_error.png"];
            break;
        case LinphoneRegistrationCleared:
        case LinphoneRegistrationNone:
            //            registrationStateImage.hidden = NO;
            //            image = [UIImage imageNamed:@"led_disconnected.png"];
            break;
        case LinphoneRegistrationProgress:
            //            registrationStateImage.hidden = NO;
            //            image = [UIImage imageNamed:@"led_inprogress.png"];
            break;
        case LinphoneRegistrationOk:
            //            registrationStateImage.hidden = NO;
            //            image = [UIImage imageNamed:@"led_connected.png"];
            break;
    }
    //    [registrationStateLabel setText:message];
    //    [registrationStateImage setImage:image];
}

- (void)initUISpeaker {
//    AudioSessionInitialize(NULL, NULL, NULL, NULL);
//    OSStatus lStatus = AudioSessionAddPropertyListener(kAudioSessionProperty_AudioRouteChange, audioRouteChangeListenerCallback, self);
//    if (lStatus) {
//        [LinphoneLogger logc:LinphoneLoggerError format:"cannot register route change handler [%ld]",lStatus];
//    }
}

@end