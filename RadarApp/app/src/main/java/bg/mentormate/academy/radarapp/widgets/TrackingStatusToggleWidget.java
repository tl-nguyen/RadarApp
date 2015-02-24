package bg.mentormate.academy.radarapp.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.activities.MainActivity;
import bg.mentormate.academy.radarapp.data.LocalDb;
import bg.mentormate.academy.radarapp.services.LocationTrackingService;

/**
 * Created by lopi on 2/22/2015.
 */
public class TrackingStatusToggleWidget extends AppWidgetProvider {

    private static final String ON_TRACKING_CLICK = "bg.mentormate.academy.radarapp.action.ON_TRACKING_CLICK";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equals(ON_TRACKING_CLICK)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            RemoteViews remoteViews = setupRemoteView(context);

            int[] appWidgetId = AppWidgetManager.getInstance(context).getAppWidgetIds(
                    new ComponentName(context, TrackingStatusToggleWidget.class));

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int ID_SIZE = appWidgetIds.length;

        for (int i=0; i < ID_SIZE; i++) {
            int widgetId = appWidgetIds[i];

            RemoteViews remoteViews = setupRemoteView(context);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    private RemoteViews setupRemoteView(Context context) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout);

        if (LocalDb.getInstance().isTrackingOn()) {
            remoteViews.setViewVisibility(R.id.ivSwitchOff, View.GONE);
            remoteViews.setViewVisibility(R.id.ivSwitchOn, View.VISIBLE);
        } else {
            remoteViews.setViewVisibility(R.id.ivSwitchOff, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.ivSwitchOn, View.GONE);
        }

        Intent openAppIntent = new Intent(context, MainActivity.class);
        PendingIntent openAppPendingIntent = PendingIntent
                .getActivity(context, 0, openAppIntent, 0);

        Intent startTrackingIntent = new Intent(context, LocationTrackingService.class);
        startTrackingIntent.setAction(LocationTrackingService.ACTION_START_MONITORING);

        PendingIntent startTrackingPendingIntent = PendingIntent
                .getService(context, 0, startTrackingIntent, 0);

        Intent stopTrackingIntent = new Intent(context, LocationTrackingService.class);
        stopTrackingIntent.setAction(LocationTrackingService.ACTION_STOP_MONITORING);

        PendingIntent stopTrackingPendingIntent = PendingIntent
                .getService(context, 0, stopTrackingIntent, 0);

        remoteViews.setOnClickPendingIntent(R.id.tvOpenApp, openAppPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.ivSwitchOff, startTrackingPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.ivSwitchOn, stopTrackingPendingIntent);

        return remoteViews;
    }
}
