package me.kirimin.kumin.ui.notification;

import me.kirimin.kumin.R;
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
        Intent intent = new Intent(context, TweetViewService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.status_bar_mess))
                .setSmallIcon(R.drawable.ic_stat_icon)
                .build();

        notification.flags = Notification.FLAG_ONGOING_EVENT;
        return notification;
    }
}
