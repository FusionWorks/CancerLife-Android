package com.platforms;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.platforms.R;
import com.platforms.main.ChatsListActivity;
import com.platforms.main.MessagesActivity;

import java.util.Timer;
import java.util.TimerTask;

public class GCMIntentService extends GCMBaseIntentService {

    // Use your PROJECT ID from Google API into SENDER_ID
    public static final String SENDER_ID = "301805774352";

    public GCMIntentService() {
        super(SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "onRegistered: registrationId=" + registrationId);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefs.edit().putString("regId", registrationId).commit();
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {

        Log.i(TAG, "onUnregistered: registrationId=" + registrationId);
    }

    @Override
    protected void onMessage(Context context, Intent data) {
        String jid = data.getStringExtra("jid");
        String name = data.getStringExtra("name");
        String message = data.getStringExtra("message");
        Intent intent = new Intent(this, MessagesActivity.class);
        // Pass data to the new activity
        intent.putExtra("jid", jid);
        intent.putExtra("name", name);

        // Starts the activity on notification click
        PendingIntent pIntent;
            pIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        String title = "Message";


        String barMessage = message;

        // Create the notification with a notification builder
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(barMessage).setContentIntent(pIntent)
                .build();
        // Remove the notification on click
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.contentIntent = pIntent;
        long[] vibrate = {200,200};
        notification.vibrate = vibrate;
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(R.string.app_name, notification);

        {
            // Wake Android Device when notification received
            PowerManager pm = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            final PowerManager.WakeLock mWakelock = pm.newWakeLock(
                    PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "GCM_PUSH");
            mWakelock.acquire();

            // Timer before putting Android Device to sleep mode.
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                public void run() {
                    mWakelock.release();
                }
            };
            timer.schedule(task, 5000);
        }

    }

    @Override
    protected void onError(Context arg0, String errorId) {

        Log.e(TAG, "onError: errorId=" + errorId);
    }

}