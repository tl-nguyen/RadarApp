package bg.mentormate.academy.radarapp.services;

import android.app.Service;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

public class TrackingService extends Service implements Handler.Callback {

    public final static String ACTION_START_MONITORING = "bg.mentormate.academy.action.ACTION_START_MONITORING";
    public final static String ACTION_STOP_MONITORING = "bg.mentormate.academy.action.ACTION_STOP_MONITORING";
    public final static String HANDLER_THREAD_NAME = "trackingservicethread";

    private LocationListener mGpsListener;
    private LocationListener mNetworkListener;
    private Looper mLooper;
    private Handler mHandler;

    public TrackingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        HandlerThread thread = new HandlerThread(HANDLER_THREAD_NAME);
        thread.start();

        mLooper = thread.getLooper();

        mHandler = new Handler(mLooper, this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            mHandler.sendMessage(mHandler.obtainMessage(0, intent));
        }

        return START_STICKY;
    }

    @Override
    public boolean handleMessage(Message msg) {
        Intent intent = (Intent) msg.obj;

        String action = intent.getAction();

        if (action.equalsIgnoreCase(ACTION_START_MONITORING)) {
            doStartTracking();
        } else if (action.equalsIgnoreCase(ACTION_STOP_MONITORING)) {
            doStopTracking();
            stopSelf();
        }

        return true;
    }

    private void doStartTracking() {
        doStopTracking();

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Track with GPS provider
        mGpsListener = new ServiceLocationListener();
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, mGpsListener, mLooper);

        // Track with Network provider
        mNetworkListener = new ServiceLocationListener();
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 20000, 0, mNetworkListener, mLooper);
    }

    private void doStopTracking() {

        if (mGpsListener != null) {
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            lm.removeUpdates(mGpsListener);
            mGpsListener = null;
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        doStopTracking();

        if (mLooper != null) {
            mLooper.quit();
        }
    }
}
