package me.kirimin.kumin.ui.notification;

import me.kirimin.kumin.R;
import me.kirimin.kumin.ui.activity.SettingMainActivity;
import me.kirimin.kumin.ui.service.TweetViewService;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class AppNotificationBuilder {

    /**
     * 必要な情報をセットしたNotificationを生成して返す
     *
     * @return 生成したNotificationインスタンス
     */
    public static Notification create(Context context) {
        Intent changeModeIntent = new Intent(context, TweetViewService.class);
        changeModeIntent.setAction("touch");
        Notification notification = new NotificationCompat.Builder(context)
                .setContentIntent(PendingIntent.getService(context, 0, new Intent(context, TweetViewService.class), PendingIntent.FLAG_UPDATE_CURRENT))
                .addAction(0, context.getString(R.string.notification_action_change_mode),
                        PendingIntent.getService(context, 0, changeModeIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                .addAction(0, context.getString(R.string.notification_action_settings),
                        PendingIntent.getActivity(context, 0, new Intent(context, SettingMainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT))
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.status_bar_mess))
                .setSmallIcon(R.drawable.ic_stat_icon)
                .setAutoCancel(true)
                .build();

        notification.flags = Notification.FLAG_ONGOING_EVENT;
        return notification;
    }
}
