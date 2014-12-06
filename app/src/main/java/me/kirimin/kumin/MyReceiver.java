package me.kirimin.kumin;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import me.kirimin.kumin.ui.notification.AppNotificationBuilder;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED) && !context.getPackageName().equals(intent.getData().getSchemeSpecificPart())) {
            return;
        }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if (pref.getBoolean(context.getString(R.string.setting_value_is_running), false)) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(1, AppNotificationBuilder.create(context));
        }
    }
}
