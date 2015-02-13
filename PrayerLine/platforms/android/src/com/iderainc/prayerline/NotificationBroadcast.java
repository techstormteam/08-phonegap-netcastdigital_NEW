package com.iderainc.prayerline;



import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

public class NotificationBroadcast extends BroadcastReceiver {
	public static final String NOTIFY_PREVIOUS = "com.tutorialsface.audioplayer.previous";
	public static final String NOTIFY_DELETE = "com.tutorialsface.audioplayer.delete";
	public static final String NOTIFY_PAUSE = "com.tutorialsface.audioplayer.pause";
	public static final String NOTIFY_PLAY = "com.tutorialsface.audioplayer.play";
	public static final String NOTIFY_NEXT = "com.tutorialsface.audioplayer.next";
	public static final String NOTIFY_CREATE = "com.tutorialsface.audioplayer.create";
	public static final String NOTIFY_BACK = "com.tutorialsface.audioplayer.back";

	public static Boolean checkBack;
	public static Boolean checknext;
	public static Boolean checkpause ;
	public static Boolean checkplay ;
	public static Boolean checkprevious ;

	Notification notification;
	int NOTIFICATION_ID = 1111;
	Intent intent;
	public static Boolean checkExit;
	public static Boolean checkPA;
	public static Boolean checkPL;
	private MyBroadcastReceiver myBroadcastReceiver;
	private MyBroadcastReceiver_Update myBroadcastReceiver_Update;

	@Override
	public void onReceive(Context context, Intent intent) {
		checkBack = false;
		checkpause = false;
		checkplay = false;
		checknext = false;
		checkprevious = false;
		checkExit = false;
		
		if (intent.getAction().equals(LinPhonePlugin.NOTIFY_CREATE)) {

			createNotification(context, intent);

		} else if (intent.getAction().equals(LinPhonePlugin.NOTIFY_KILL)) {
			
//			LinphoneService.instance().canCel();
			updateNotification(context, intent);
		} else if (intent.getAction().equals(LinPhonePlugin.NOTIFY_DELETE)) {
//			LinphoneService.instance().stopForeground(true);
			Intent getExit = new Intent(context, Netcastdigital.class);
			getExit.setAction("exit");
			getExit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(getExit);
			checkExit = intent.getAction().equals(LinPhonePlugin.NOTIFY_DELETE);
			LinphoneService.instance().stopForeground(true);
			LinphoneService.instance().Notification();
		} else if (intent.getAction().equals(LinPhonePlugin.NOTIFY_NEXT)) {
			Intent getNext = new Intent(context, Netcastdigital.class);
			getNext.setAction("next");
			getNext.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(getNext);
			checknext = intent.getAction().equals(LinPhonePlugin.NOTIFY_NEXT);

		} else if (intent.getAction().equals(LinPhonePlugin.NOTIFY_PAUSE)) {
			checkPA = intent.getAction().equals(LinPhonePlugin.NOTIFY_PAUSE);
			Intent getpause = new Intent(context, Netcastdigital.class);
		updateNotification(context, intent);
			getpause.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(getpause);
			checkpause = intent.getAction().equals(LinPhonePlugin.NOTIFY_PAUSE);

		} else if (intent.getAction().equals(LinPhonePlugin.NOTIFY_PLAY)) {
			checkPL = intent.getAction().equals(LinPhonePlugin.NOTIFY_PLAY);
			updateNotification(context, intent);
			Intent getplay = new Intent(context, Netcastdigital.class);
			getplay.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(getplay);
			checkplay = intent.getAction().equals(LinPhonePlugin.NOTIFY_PLAY);
		} else if (intent.getAction().equals(LinPhonePlugin.NOTIFY_PREVIOUS)) {
			LinPhonePlugin.dialDtmf('4');
			Intent getprevious = new Intent(context, Netcastdigital.class);
			getprevious.setAction("previous");
			getprevious.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(getprevious);
			checkprevious = intent.getAction().equals(LinPhonePlugin.NOTIFY_PREVIOUS);

		}

	}
	public static Boolean checkPAUSE(Intent intent){
		checkpause = intent.getAction().equals(LinPhonePlugin.NOTIFY_PAUSE);
		return checkpause;
				
	}
	public static Boolean checkPLAY(Intent intent){
		checkplay = intent.getAction().equals(LinPhonePlugin.NOTIFY_PAUSE);
		return checkplay;
				
	}
	
	private void updateNotification(Context context, Intent intent) {
//		checkBack = false;
//		checkpause = false;
//		checkplay = false;
//		checknext = false;
//		checkprevious = false;
//		checkExit = false;
//		LinphoneService.instance().stopSelf(LinphoneService.NOTIF_ID);
//		LinphoneService.instance().stopSelf();
//		LinphoneService.instance().stopForeground(true);
//		LinphoneService.instance().stopForegroundCompat(LinphoneService.NOTIF_ID);
		LinphoneService.instance().mNM = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		Intent getBack = new Intent(context, Netcastdigital.class);
		getBack.setAction("receive");
		getBack.setAction("exit");
		PendingIntent pBack = PendingIntent.getActivity(context, 0, getBack,
				PendingIntent.FLAG_UPDATE_CURRENT);

		RemoteViews simpleContentView = new RemoteViews(
				context.getPackageName(), R.layout.big_notification);
		Notification notification = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.icon).build();
		notification.setLatestEventInfo(context, "", "", pBack);
		notification.contentView = simpleContentView;
		setListeners(simpleContentView, context);
		notification.contentView.setTextViewText(R.id.textName,
				LinPhonePlugin.titleNotification);
		if (checkPA != null) {
			if (checkPA) {
				notification.contentView.setViewVisibility(R.id.btnPause,
						View.GONE);
				notification.contentView.setViewVisibility(R.id.btnPlay,
						View.VISIBLE);
			}
		}
		if (checkPL != null) {
			if (checkPL) {
				notification.contentView.setViewVisibility(R.id.btnPause,
						View.VISIBLE);
				notification.contentView.setViewVisibility(R.id.btnPlay,
						View.GONE);
			}
		}
		checkBack = intent.getAction().equals("receive");
		checkPA = null;
		checkPL = null;
//		notification.flags |= Notification.FLAG_ONGOING_EVENT;
//		LinphoneService.mNM = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
//		LinphoneService.mNM.notify(LinphoneService.NOTIF_ID, notification); // in case of crash the icon is not removed
//		LinphoneService.instance().startForeground(LinphoneService.NOTIF_ID, notification);
//		LinphoneService.instance().startForegroundCompat(LinphoneService.NOTIF_ID, notification);
		LinphoneService.instance().mNM.notify(LinphoneService.instance().close, notification);
		LinphoneService.instance().notifyWrapper(LinphoneService.instance().close, notification);
		LinphoneService.instance().startForegroundCompat(LinphoneService.instance().close, notification);
		LinphoneService.instance().startForeground(LinphoneService.instance().close, notification);
	}
	
	private void createNotification(Context context, Intent intent) {
//		checkBack = false;
//		checkpause = false;
//		checkplay = false;
//		checknext = false;
//		checkprevious = false;
//		checkExit = false;
		Intent getBack = new Intent(context, Netcastdigital.class);
		getBack.setAction("receive");
		getBack.setAction("exit");
		PendingIntent pBack = PendingIntent.getActivity(context, 0, getBack,
				PendingIntent.FLAG_UPDATE_CURRENT);

		RemoteViews simpleContentView = new RemoteViews(
				context.getPackageName(), R.layout.big_notification);
		LinphoneService.instance().mNotif = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.icon).build();
		LinphoneService.instance().mNotif.setLatestEventInfo(context, "", "", pBack);
		LinphoneService.instance().mNotif.contentView = simpleContentView;
		setListeners(simpleContentView, context);
		LinphoneService.instance().mNotif.contentView.setTextViewText(R.id.textName,
				LinPhonePlugin.titleNotification);
		if (checkPA != null) {
			if (checkPA) {
				LinphoneService.instance().mNotif.contentView.setViewVisibility(R.id.btnPause,
						View.GONE);
				LinphoneService.instance().mNotif.contentView.setViewVisibility(R.id.btnPlay,
						View.VISIBLE);
			}
		}
		if (checkPL != null) {
			if (checkPL) {
				LinphoneService.instance().mNotif.contentView.setViewVisibility(R.id.btnPause,
						View.VISIBLE);
				LinphoneService.instance().mNotif.contentView.setViewVisibility(R.id.btnPlay,
						View.GONE);
			}
		}
		checkBack = intent.getAction().equals("receive");
		checkPA = null;
		checkPL = null;
		LinphoneService.instance().mNotif.flags |= Notification.FLAG_ONGOING_EVENT;
		LinphoneService.instance().startForeground(NOTIFICATION_ID,
				notification);
		LinphoneService.instance().startForegroundCompat(LinphoneService.NOTIF_ID,LinphoneService.instance().mNotif);
	}

	public void setListeners(RemoteViews view, Context context) {
		Intent previous = new Intent(NOTIFY_PREVIOUS);
		Intent delete = new Intent(NOTIFY_DELETE);
		Intent pause = new Intent(NOTIFY_PAUSE);
		Intent next = new Intent(NOTIFY_NEXT);
		Intent play = new Intent(NOTIFY_PLAY);

		PendingIntent pPrevious = PendingIntent.getBroadcast(context, 0,
				previous, PendingIntent.FLAG_UPDATE_CURRENT);
		view.setOnClickPendingIntent(R.id.btnPrevious, pPrevious);

		PendingIntent pDelete = PendingIntent.getBroadcast(context, 0, delete,
				PendingIntent.FLAG_UPDATE_CURRENT);
		view.setOnClickPendingIntent(R.id.btnDelete, pDelete);

		PendingIntent pPause = PendingIntent.getBroadcast(context, 0, pause,
				PendingIntent.FLAG_UPDATE_CURRENT);
		view.setOnClickPendingIntent(R.id.btnPause, pPause);

		PendingIntent pNext = PendingIntent.getBroadcast(context, 0, next,
				PendingIntent.FLAG_UPDATE_CURRENT);
		view.setOnClickPendingIntent(R.id.btnNext, pNext);

		PendingIntent pPlay = PendingIntent.getBroadcast(context, 0, play,
				PendingIntent.FLAG_UPDATE_CURRENT);
		view.setOnClickPendingIntent(R.id.btnPlay, pPlay);

	}
	public class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String result = intent.getStringExtra(LinphoneService.EXTRA_KEY_OUT);
		}
	}
	
	public class MyBroadcastReceiver_Update extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			int update = intent.getIntExtra(LinphoneService.EXTRA_KEY_UPDATE, 0);
		}
	}
	public String ComponentName() {
		return this.getClass().getName();
	}
}
