/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package com.iderainc.prayerline;

import static android.content.Intent.ACTION_MAIN;

import java.lang.reflect.Array;
import java.util.Arrays;

import org.apache.cordova.CordovaActivity;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.PayloadType;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RemoteViews;

public class Netcastdigital extends CordovaActivity implements OnClickListener {
	public static String SIP_DOMAIN;
	public static String SIP_USERNAME;
	public static String SIP_PASSWORD;
	private Handler mHandler;
	AppWidgetManager appWidgetManager;
	private ServiceWaitThread mThread;
	LayoutInflater inflater;
	ViewGroup container;
	Bundle savedInstanceState;
	Intent intent;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set by <content src="index.html" /> in config.xml
		// super.setIntegerProperty("loadUrlTimeoutValue", 70000);
		loadUrl("file:///android_asset/www/index.html");
//		MusicMedia();
		// onCreateView();
		SIP_DOMAIN = getResources().getString(R.string.sip_domain);
		SIP_USERNAME = getResources().getString(R.string.sip_username);
		SIP_PASSWORD = getResources().getString(R.string.sip_password);

		mHandler = new Handler();

		if (LinphoneService.isReady()) {
			onServiceReady();
		} else {
			// start linphone as background
			startService(new Intent(ACTION_MAIN).setClass(this,
					LinphoneService.class));
			mThread = new ServiceWaitThread();
			mThread.start();
		}
	}

	protected void onServiceReady() {

		LinphoneService.instance().setActivityToLaunchOnIncomingReceived(
				Netcastdigital.class);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					enableAllAudioCodecs();
				} catch (LinphoneCoreException e) {
					e.printStackTrace();
				}
			}
		}, 1000);
	}

	private void enableAllAudioCodecs() throws LinphoneCoreException {
		LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
		for (final PayloadType pt : lc.getAudioCodecs()) {
			LinphoneManager.getLcIfManagerNotDestroyedOrNull()
					.enablePayloadType(pt, true);
		}
	}

	private class ServiceWaitThread extends Thread {
		public void run() {
			while (!LinphoneService.isReady()) {
				try {
					sleep(30);
				} catch (InterruptedException e) {
					throw new RuntimeException(
							"waiting thread sleep() has been interrupted");
				}
			}

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					onServiceReady();
				}
			});
			mThread = null;
		}
	}
	public void checkFalse()
	{
		NotificationBroadcast.checkBack = false;
		NotificationBroadcast.checkpause = false;
		NotificationBroadcast.checkplay = false;
		NotificationBroadcast.checknext = false;
		NotificationBroadcast.checkprevious = false;
		NotificationBroadcast.checkExit = false;
	}
	

	@Override
	protected void onResume() {
		super.onResume();
	
		
		if (NotificationBroadcast.checkExit!=null && NotificationBroadcast.checkExit) {
				String hhh="";
				sendJavascript("playerStop();");
				LinPhonePlugin.hangUp();
				LinPhonePlugin.signOut(LinPhonePlugin.user, LinPhonePlugin.Domain);
				checkFalse();
		}else if (NotificationBroadcast.checknext!=null && NotificationBroadcast.checknext) {
				LinPhonePlugin.dialDtmf('6');
				sendJavascript("playerForward();");
				checkFalse();

		}else if (NotificationBroadcast.checkpause!=null && NotificationBroadcast.checkpause) {
				LinPhonePlugin.dialDtmf('#');
				sendJavascript("playerPlay(true);");
				checkFalse();
		}else if (NotificationBroadcast.checkplay!=null && NotificationBroadcast.checkplay) {
				LinPhonePlugin.dialDtmf('#');
				sendJavascript("playerPlay(true);");
				checkFalse();
		}else if (NotificationBroadcast.checkprevious!=null && NotificationBroadcast.checkprevious) {
				LinPhonePlugin.dialDtmf('4');
				sendJavascript("playerPlay();");
				checkFalse();
		}else if (NotificationBroadcast.checkBack!=null) {
			String messageId = LinPhonePlugin.message;
//			int middle = messageId.length() / 2;
//			while (messageId.charAt(middle)=='-') {
//				middle++;
//			}
			if (messageId.contains("playMessage")) {
				 String arr[] = messageId.split("-");
			        String Id = arr[3];
			        loadUrl("file:///android_asset/www/prayer-line-message.html?message_id="+Id+"\" rel=\"external\">");
			}
			if (messageId.contains("playback")) {
				loadUrl("file:///android_asset/www/prayer-line-play-list.html");
			}
//	        String arr[] = messageId.split("-");
//	        String Id = arr[3];
//	        loadUrl("file:///android_asset/www/prayer-line-message.html?message_id="+Id+"\" rel=\"external\">");
			checkFalse();
		}
//		
//		if (NotificationBroadcast.checkExit!=null) {
//			sendJavascript("playerStop();");
//		}
//		
		// }

		// Bundle extras = getIntent().getExtras();
		// if (extras != null) {
		// if
		// (extras.containsKey(this.getApplicationContext().getString(R.string.refresh_player)))
		// {
		// Boolean refreshPlayer =
		// extras.getBoolean(this.getApplicationContext().getString(R.string.refresh_player));
		// if (refreshPlayer) {
		// appView.sendJavascript("playerStop()");
		// }
		// }
		// }

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		

	}

}
