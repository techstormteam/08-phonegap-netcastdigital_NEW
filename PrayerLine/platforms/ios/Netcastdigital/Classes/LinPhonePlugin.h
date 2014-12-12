//
//  LinPhonePlugin.h
//  Netcastdigital
//
//  Created by Macbook air on 11/11/14.
//
//


#import <Cordova/CDV.h>
#include "linphone/linphonecore.h"
#include "LinphoneCoreSettingsStore.h"

#define NOT_REGISTERED @"NOT-REGISTERED"

@interface LinPhonePlugin : CDVPlugin

- (void) callSip:(CDVInvokedUrlCommand *)command;

- (void) cancelSip:(CDVInvokedUrlCommand *)command;

- (void) registerSip:(CDVInvokedUrlCommand *)command;

- (void) deregisterSip:(CDVInvokedUrlCommand *)command;

- (void) backWind:(CDVInvokedUrlCommand *)command;

- (void) forwardWind:(CDVInvokedUrlCommand *)command;

- (void) pauseSip:(CDVInvokedUrlCommand *)command;

- (void) signOut:(CDVInvokedUrlCommand *)command;

+ (void)doProxyConfigUpdate:(LinphoneProxyConfig*)config;
+ (void)doSignOut:(NSString*)sipUsername domain:(NSString*)domain;

@end