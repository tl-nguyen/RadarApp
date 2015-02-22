package bg.mentormate.academy.radarapp.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.activities.MainActivity;
import bg.mentormate.academy.radarapp.fragments.HomeFragment;
import bg.mentormate.academy.radarapp.fragments.ProfileFragment;

/**
 * Created by lopi on 2/22/2015.
 */
public class PrimaryWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        final int n = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i < n; i++) {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch MainActivity
            Intent intent = new Intent(context, ProfileFragment.class);
            Intent intent2 = new Intent(context, HomeFragment.class);
            Intent intentHome = new Intent(context, MainActivity.class);
            PendingIntent pendingIntentOn = PendingIntent.getActivity(context, 0, intent, 0);
            PendingIntent pendingIntentOff = PendingIntent.getActivity(context, 0, intent2, 0);
            PendingIntent pendingIntentHome = PendingIntent.getActivity(context, 0, intentHome, 0);

            // Get the layout for the App Widget and attach an on-click listener to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.id.widget_layout);
            views.setOnClickPendingIntent(R.id.buttonOn, pendingIntentOn);
            views.setOnClickPendingIntent(R.id.buttonOff, pendingIntentOff);
            views.setOnClickPendingIntent(R.id.buttonHome, pendingIntentHome);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
