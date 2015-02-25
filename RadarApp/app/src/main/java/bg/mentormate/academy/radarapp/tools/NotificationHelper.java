package bg.mentormate.academy.radarapp.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import bg.mentormate.academy.radarapp.R;

/**
 * Created by tl on 05.02.15.
 */
public class NotificationHelper {

    public static void alert(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton(android.R.string.ok, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void notifyTheUser(Context context, int title, int description, Intent intent) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(title))
                .setContentText(context.getString(description))
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);

        notificationManager.notify(1, notificationBuilder.build());
    }
}
