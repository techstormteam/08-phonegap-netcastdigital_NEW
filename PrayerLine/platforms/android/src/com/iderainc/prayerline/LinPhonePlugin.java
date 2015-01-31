package com.iderainc.prayerline;

import java.util.ArrayList;
import java.util.List;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.linphone.core.LinphoneAddress.TransportType;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCore.RegistrationState;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneProxyConfig;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.iderainc.prayerline.LinphonePreferences.AccountBuilder;

public class LinPhonePlugin extends CordovaPlugin {
	public static final String NOTIFY_CREATE = "com.tutorialsface.audioplayer.create";
	public static final String NOTIFY_PREVIOUS = "com.tutorialsface.audioplayer.previous";
	public static final String NOTIFY_DELETE = "com.tutorialsface.audioplayer.delete";
	public static final String NOTIFY_PAUSE = "com.tutorialsface.audioplayer.pause";
	public static final String NOTIFY_PLAY = "com.tutorialsface.audioplayer.play";
	public static final String NOTIFY_BACK = "com.tutorialsface.audioplayer.back";
	public static final String NOTIFY_NEXT = "com.tutorialsface.audioplayer.next";
	public static final String NOTIFY_KILL = "com.tutorialsface.audioplayer.kill";
	public static final String NOTIFY_RETURN = "com.tutorialsface.audioplayer.return";
	public static final String NOT_REGISTERED = "NOT-REGISTERED";
	public static final String REGISTERED = "REGISTERED";
	public static final String NOT_IN_CALL = "NOT-IN-CALL";
	public static final String IN_CALL = "IN-CALL";
	public static String currentSipUsername = "";
	private Handler mHandler;
	AppWidgetManager appWidgetManager;
	LayoutInflater inflater;
	ViewGroup container;
	Bundle savedInstanceState;
	public static String message;
	private BroadcastReceiver mReceiver;
	public static String user;
	public static String Domain;
	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		if (action.equals("callSip")) {
			String callTo = (String) args.get(0);
			message = callTo;
			String sipUsername = (String) args.get(1);
			String password = (String) args.get(2);
			String domain = Netcastdigital.SIP_DOMAIN;
			registerSip(sipUsername, password, domain);
			sip(String.format("sip:%s@%s", callTo, Netcastdigital.SIP_DOMAIN));
			deleteNotification();
			creatNotification();
			LinphoneManager.getInstance().routeAudioToSpeaker();
			LinphoneManager.getLc().enableSpeaker(true);
			callbackContext.success("call sip");
			return true;
		} else if (action.equals("cancelSip")) {
			String sipUsername = (String) args.get(0);
			String domain = Netcastdigital.SIP_DOMAIN;
//		
			hangUp();
			signOut(sipUsername, domain);
			callbackContext.success("cancel sip");
			return true;
		} else if (action.equals("registerSip")) {
			String sipUsername = (String) args.get(0);
			String password = (String) args.get(1);
			String domain = Netcastdigital.SIP_DOMAIN;
			user = sipUsername;
			Domain = domain;
			// registerSip(sipUsername, password, domain);
			PluginResult result = new PluginResult(Status.OK);
			callbackContext.sendPluginResult(result);
			return true;
		} else if (action.equals("deregisterSip")) {
			String sipUsername = (String) args.get(0);
			String status = (String) args.get(1);
			String domain = Netcastdigital.SIP_DOMAIN;
			if (NOT_IN_CALL.equals(status)) {
				signOut(sipUsername, domain);
			}
			PluginResult result = new PluginResult(Status.OK);
			callbackContext.sendPluginResult(result);
			return true;
		} else if (action.equals("backWind")) {
			dialDtmf('4');
			callbackContext.success("back wind");
			return true;
		} else if (action.equals("forwardWind")) {
			dialDtmf('6');
			return true;
		} else if (action.equals("pauseSip")) {
			dialDtmf('#');
			callbackContext.success("pause sip");
			return true;
		} else if (action.equals("signOut")) {
			String sipUsername = (String) args.get(0);
			String domain = Netcastdigital.SIP_DOMAIN;
			signOut(sipUsername, domain);
			callbackContext.success("Sign out successful.");
			return true;
			
		}
		return false;
	}
	

	private void registerSip(String sipUsername, String password, String domain) {
		currentSipUsername = sipUsername;
		String sipAddress = sipUsername + "@" + domain;

		LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();

		if (lc.isNetworkReachable()) {
			signOut(sipUsername, domain);

			// Get account index.
			int nbAccounts = LinphonePreferences.instance().getAccountCount();
			List<Integer> accountIndexes = findAuthIndexOf(sipAddress);

			if (accountIndexes == null || accountIndexes.isEmpty()) { // User
																		// haven't
																		// registered
																		// in
																		// linphone
				logIn(sipUsername, password, domain, false);
				lc.refreshRegisters();
				accountIndexes.add(nbAccounts);
			}

			for (Integer accountIndex : accountIndexes) {
				if (LinphonePreferences.instance().getDefaultAccountIndex() != accountIndex) {
					LinphonePreferences.instance().setDefaultAccount(
							accountIndex);
					LinphonePreferences.instance().setAccountEnabled(
							accountIndex, true);
					lc.setDefaultProxyConfig((LinphoneProxyConfig) LinphoneManager
							.getLc().getProxyConfigList()[accountIndex]);
					lc.refreshRegisters();
				} else {
					if (lc != null && lc.getDefaultProxyConfig() != null) {
						if (RegistrationState.RegistrationOk == LinphoneManager
								.getLc().getDefaultProxyConfig().getState()) {
							// empty
						} else if (RegistrationState.RegistrationFailed == LinphoneManager
								.getLc().getDefaultProxyConfig().getState()
								|| RegistrationState.RegistrationNone == LinphoneManager
										.getLc().getDefaultProxyConfig()
										.getState()) {
							LinphonePreferences.instance().setAccountEnabled(
									accountIndex, true);
							lc.refreshRegisters();
						}
					}
				}
			}
		}
	}

	public static void deleteAccount(int n) {
		final LinphoneProxyConfig proxyCfg = getProxyConfig(n);

		if (proxyCfg != null) {
			LinphoneManager.getLc().removeProxyConfig(proxyCfg);
		}

		if (LinphoneManager.getLc().getProxyConfigList().length == 0) {
			LinphoneManager.getLc().refreshRegisters();
			// LinphoneActivity.instance().getStatusFragment().registrationStateChanged(RegistrationState.RegistrationNone);
		} else {
			resetDefaultProxyConfig();
			LinphoneManager.getLc().refreshRegisters();
		}
	}

	public static void resetDefaultProxyConfig() {
		int count = LinphoneManager.getLc().getProxyConfigList().length;
		for (int i = 0; i < count; i++) {
			if (isAccountEnabled(i)) {
				LinphoneManager.getLc()
						.setDefaultProxyConfig(getProxyConfig(i));
				break;
			}
		}

		if (LinphoneManager.getLc().getDefaultProxyConfig() == null) {
			LinphoneManager.getLc().setDefaultProxyConfig(getProxyConfig(0));
		}
	}

	public static boolean isAccountEnabled(int n) {
		return getProxyConfig(n).registerEnabled();
	}

	// Accounts settings
	private static LinphoneProxyConfig getProxyConfig(int n) {
		LinphoneProxyConfig[] prxCfgs = LinphoneManager.getLc()
				.getProxyConfigList();
		if (n < 0 || n >= prxCfgs.length)
			return null;
		return prxCfgs[n];
	}

	private static List<Integer> findAuthIndexOf(String sipAddress) {
		int nbAccounts = LinphonePreferences.instance().getAccountCount();
		List<Integer> indexes = new ArrayList<Integer>();
		for (int index = 0; index < nbAccounts; index++) {
			String accountUsername = LinphonePreferences.instance()
					.getAccountUsername(index);
			String accountDomain = LinphonePreferences.instance()
					.getAccountDomain(index);
			String identity = accountUsername + "@" + accountDomain;
			if (identity.equals(sipAddress)) {
				indexes.add(index);
			}
		}
		return indexes;
	}

	
	
	private void creatNotification() {

//
//		Intent create = new Intent(this.cordova.getActivity()
//				.getApplicationContext(), Netcastdigital.class);
//		create.setAction("hehe");
		Intent previous = new Intent(NOTIFY_CREATE);

		this.cordova.getActivity().getApplicationContext().sendBroadcast(previous);
	}
	private void deleteNotification() {

		//
//				Intent create = new Intent(this.cordova.getActivity()
//						.getApplicationContext(), Netcastdigital.class);
//				create.setAction("hehe");
				Intent delete = new Intent(NOTIFY_KILL);

				this.cordova.getActivity().getApplicationContext().sendBroadcast(delete);
			}
//	@Override
//	public void onNewIntent(Intent intent) {
//		// TODO Auto-generated method stub
//		intent.getBundleExtra("hehe");
//		if (intent.getAction().equals("pause")) {
//			dialDtmf('#');
//			NotificationManager notificationManager = (NotificationManager) this.cordova
//					.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
//			notificationManager.cancelAll();
//			creatNotificationplay();
//			
////			Notification n = new Notification.Builder(this.cordova.getActivity()
////					.getApplicationContext()).build();
////			n.flags = Notification.FLAG_NO_CLEAR;
//			
//		}else if (intent.getAction().equals("play")) {
//			dialDtmf('#');
//			NotificationManager notificationManager = (NotificationManager) this.cordova
//					.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
//			notificationManager.cancelAll();
//			creatNotification();
////			Notification n = new Notification.Builder(this.cordova.getActivity()
////					.getApplicationContext()).build();
////			n.flags = Notification.FLAG_NO_CLEAR;
//		}
//		else if ((intent.getAction().equals("next"))) {
//			dialDtmf('6');
//		}else {
//			dialDtmf('4');
//
//		}
//		super.onNewIntent(intent);
//	}

	public static void signOut(String sipUsername, String domain) {
		if (LinphoneManager.isInstanciated()) {
			LinphoneCore lc = LinphoneManager
					.getLcIfManagerNotDestroyedOrNull();

			String sipAddress = sipUsername + "@" + domain;
			List<Integer> accountIndexes = findAuthIndexOf(sipAddress);
			for (Integer accountIndex : accountIndexes) {
				// LinphonePreferences.instance().setAccountEnabled(accountIndex,
				// false);
				// LinphoneProxyConfig proxyCfg =
				// lc.getProxyConfigList()[accountIndex];
				// lc.removeProxyConfig(proxyCfg);
				deleteAccount(accountIndex);
			}

			// LinphoneAuthInfo authInfo = lc.findAuthInfo(sipUsername, null,
			// domain);
			// if (authInfo != null) {
			// lc.removeAuthInfo(authInfo);
			// }
			lc.refreshRegisters();

		}
	}

	public static void dialDtmf(char keyCode) {
		LinphoneCore lc = LinphoneManager.getLc();
		lc.stopDtmf();
		if (lc.isIncall()) {
			lc.sendDtmf(keyCode);
		}
	}

	public static void hangUp() {
		LinphoneCore lc = LinphoneManager.getLc();
		LinphoneCall currentCall = lc.getCurrentCall();

		if (currentCall != null) {
			lc.terminateCall(currentCall);
		} else if (lc.isInConference()) {
			lc.terminateConference();
		} else {
			lc.terminateAllCalls();
		}
	}

	private void sip(String address) {
		try {
			if (!LinphoneManager.getInstance().acceptCallIfIncomingPending()) {
				if (address.length() > 0) {
					LinphoneManager.getInstance().newOutgoingCall(address);
				}
			}
		} catch (LinphoneCoreException e) {
			LinphoneManager.getInstance().terminateCall();
		}
		;
	}

	private void logIn(String username, String password, String domain,
			boolean sendEcCalibrationResult) {
		// InputMethodManager imm = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);
		// if (imm != null && getCurrentFocus() != null) {
		// imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		// }

		saveCreatedAccount(username, password, domain);

		if (LinphoneManager.getLc().getDefaultProxyConfig() != null) {
			// launchEchoCancellerCalibration(sendEcCalibrationResult);
		}
	}

	public void saveCreatedAccount(String username, String password,
			String domain) {
		// if (accountCreated)
		// return;

		boolean isMainAccountLinphoneDotOrg = false;
		boolean useLinphoneDotOrgCustomPorts = false;
		AccountBuilder builder = new AccountBuilder(LinphoneManager.getLc())
				.setUsername(username).setDomain(domain).setPassword(password);

		if (isMainAccountLinphoneDotOrg && useLinphoneDotOrgCustomPorts) {
			boolean disable_all_security_features_for_markets = true;
			if (disable_all_security_features_for_markets) {
				builder.setProxy(domain + ":5228").setTransport(
						TransportType.LinphoneTransportTcp);
			} else {
				builder.setProxy(domain + ":5223").setTransport(
						TransportType.LinphoneTransportTls);
			}

			builder.setExpires("604800")
					.setOutboundProxyEnabled(true)
					.setAvpfEnabled(true)
					.setAvpfRRInterval(3)
					.setQualityReportingCollector(
							"sip:voip-metrics@sip.linphone.org")
					.setQualityReportingEnabled(true)
					.setQualityReportingInterval(180)
					.setRealm("sip.linphone.org");

			// mPrefs.setStunServer(getString(R.string.default_stun));
			// mPrefs.setIceEnabled(true);
			// mPrefs.setPushNotificationEnabled(true);
		} else {
			// String forcedProxy =
			// getResources().getString(R.string.setup_forced_proxy);
			// if (!TextUtils.isEmpty(forcedProxy)) {
			// builder.setProxy(forcedProxy)
			// .setOutboundProxyEnabled(true)
			// .setAvpfRRInterval(5);
			// }
		}

		try {
			builder.saveNewAccount();
			// accountCreated = true;
		} catch (LinphoneCoreException e) {
			e.printStackTrace();
		}
	}

}
