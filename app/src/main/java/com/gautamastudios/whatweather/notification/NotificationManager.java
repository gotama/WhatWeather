package com.gautamastudios.whatweather.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;

import com.gautamastudios.whatweather.R;
import com.gautamastudios.whatweather.ui.activity.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class NotificationManager {

    private final static int NORMAL_NOTIFICATION = 0;
    private final static int CUSTOM_NOTIFICATION = 1;

    private Context context;
    private static NotificationManagerCompat notificationManager;

    public NotificationManager(Context context) {
        this.context = context;
        notificationManager = NotificationManagerCompat.from(context);
    }

    public void setNormalStyle(String contentText, String remoteImageUrl, int iconResourceID) {
        new NotificationBuilderAsync(NORMAL_NOTIFICATION, context, iconResourceID).execute(contentText, remoteImageUrl);
    }

    public void setCustomView(String contentText, int iconResourceID) {
        new NotificationBuilderAsync(CUSTOM_NOTIFICATION, context, iconResourceID).execute(contentText);
    }

    /**
     * Notification AsyncTask to create and notify with NotificationManager
     */
    private class NotificationBuilderAsync extends AsyncTask<String, Void, Void> {

        int style = -1;
        Context context;
        int iconResourceID;

        /**
         * @param style {@link #NORMAL_NOTIFICATION}, {@link #CUSTOM_NOTIFICATION}
         */
        private NotificationBuilderAsync(int style, Context context, int iconResourceID) {
            this.style = style;
            this.context = context;
            this.iconResourceID = iconResourceID;
        }

        @Override
        protected Void doInBackground(String... contentText) {
            Notification noti = new Notification();

            switch (style) {
                case NORMAL_NOTIFICATION:
                    noti = setNormalNotification(context, contentText[0], contentText[1], iconResourceID);
                    break;

                case CUSTOM_NOTIFICATION:
                    noti = setCustomViewNotification();
                    break;

            }

            noti.defaults |= Notification.DEFAULT_LIGHTS;
            noti.defaults |= Notification.DEFAULT_VIBRATE;
            noti.defaults |= Notification.DEFAULT_SOUND;

            noti.flags |= Notification.FLAG_ONLY_ALERT_ONCE;

            notificationManager.notify(0, noti);

            return null;

        }
    }

    private Notification setNormalNotification(Context context, String contentText, String url, int iconResourceID) {
        Bitmap remoteBmp = null;

        try {
            remoteBmp = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //TODO Add master view to back stack and detail view to next intent
        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(context, "weather-notifications").setSmallIcon(iconResourceID)
                .setAutoCancel(true).setLargeIcon(remoteBmp).setContentIntent(resultPendingIntent).addAction(
                        R.drawable.ic_stat_warning, "Alert Warning", resultPendingIntent).setContentTitle(
                        "Weather Change").setContentText(contentText).build();
    }

    //TODO
    private Notification setCustomViewNotification() {

        //        Intent resultIntent = new Intent(this, MainActivity.class);
        //        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        //        stackBuilder.addParentStack(MainActivity.class);
        //        stackBuilder.addNextIntent(resultIntent);
        //        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent
        // .FLAG_UPDATE_CURRENT);
        //        RemoteViews expandedView = new RemoteViews(this.getPackageName(), R.layout
        // .notification_custom_remote);
        //        expandedView.setTextViewText(R.id.text_view, "");
        //        Notification notification = new NotificationCompat.Builder(this)
        //                .setSmallIcon(R.drawable.ic_launcher)
        //                .setAutoCancel(true)
        //                .setContentIntent(resultPendingIntent)
        //                .setContentTitle("Custom View").build();
        //        notification.bigContentView = expandedView;
        //        return notification;

        return null;
    }
}
