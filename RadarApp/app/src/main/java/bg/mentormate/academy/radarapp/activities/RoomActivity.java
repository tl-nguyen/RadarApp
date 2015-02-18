package bg.mentormate.academy.radarapp.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import bg.mentormate.academy.radarapp.Constants;
import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.data.LocalDb;
import bg.mentormate.academy.radarapp.models.CurrentLocation;
import bg.mentormate.academy.radarapp.models.Room;
import bg.mentormate.academy.radarapp.models.User;
import bg.mentormate.academy.radarapp.services.LocationTrackingService;
import bg.mentormate.academy.radarapp.services.RetrieveRoomDataService;
import bg.mentormate.academy.radarapp.tools.AlertHelper;

public class RoomActivity extends ActionBarActivity {

    private final static long DATA_UPDATE_INTERVAL = 4000;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private LocalDb mLocalDb;
    private User mCurrentUser;
    private Room mRoom;
    private Intent mDataServiceIntent;

    private BroadcastReceiver positionsUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mRoom = mLocalDb.getSelectedRoom();
            updateMarkers();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLocalDb = LocalDb.getInstance();

        mCurrentUser = (User) User.getCurrentUser();

        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(positionsUpdateReceiver,
                new IntentFilter(RetrieveRoomDataService.BROADCAST_RESULT));

        startServiceForUpdatingPositions();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void startServiceForUpdatingPositions() {
        final long ALARM_TRIGGER_AT_TIME = SystemClock.elapsedRealtime() + 20000;
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        mDataServiceIntent = new Intent(this, RetrieveRoomDataService.class);

        PendingIntent pendingIntent = PendingIntent.getService(this, 0, mDataServiceIntent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, ALARM_TRIGGER_AT_TIME, DATA_UPDATE_INTERVAL, pendingIntent);
    }

    private void setUpMap() {
        mMap.setMyLocationEnabled(true);

        CurrentLocation currentLocation = mCurrentUser.getCurrentLocation();

        try {
            currentLocation.fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(currentLocation.getLocation().getLatitude(),
                        currentLocation.getLocation().getLongitude()),
                13));

        String roomId = getIntent().getStringExtra(Constants.ROOM_ID);

        if (roomId != null) {
            retrieveRoomById(roomId);
        }

        startServiceForLocationTracking();
    }

    private void retrieveRoomById(String roomId) {
        ParseQuery query = new ParseQuery(Constants.ROOM_TABLE);

        query.getInBackground(roomId, new GetCallback() {
            @Override
            public void done(ParseObject room, ParseException e) {
                if (e == null) {
                    mRoom = (Room) room;
                    mLocalDb.setSelectedRoom(mRoom);

                    // Set activity title
                    getSupportActionBar().setTitle(mRoom.getName());

                    updateMarkers();
                } else {
                    AlertHelper.alert(RoomActivity.this,
                            getString(R.string.dialog_error_title),
                            e.getMessage());
                }
            }
        });
    }

    private void updateMarkers() {
        MarkersUpdateTask markersUpdateTask = new MarkersUpdateTask();
        markersUpdateTask.execute();
    }

    private void startServiceForLocationTracking() {
        Intent trackingIntent = new Intent(LocationTrackingService.ACTION_START_MONITORING);
        startService(trackingIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        if (mDataServiceIntent != null) {
            stopService(mDataServiceIntent);
        }

        if (positionsUpdateReceiver != null) {
            unregisterReceiver(positionsUpdateReceiver);
        }

        super.onStop();
    }

    private class MarkersUpdateTask extends AsyncTask<Void, MarkerOptions, Void> {

        @Override
        protected void onPreExecute() {
            mMap.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            List<User> users = mRoom.getUsers();

            for (User user: users) {
                final MarkerOptions marker = new MarkerOptions();
                ParseGeoPoint userLocation = null;
                Bitmap scaledAvatar = null;

                try {
                    user.fetchIfNeeded();

                    if (user.equals(mCurrentUser)) {
                        continue;
                    }

                    user.getCurrentLocation().fetchIfNeeded();

                    userLocation = user.getCurrentLocation().getLocation();

                    scaledAvatar = getBitmapAvatar(user);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (userLocation != null) {
                    marker.position(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()))
                            .title(user.getUsername());
                }

                if (scaledAvatar != null) {
                    marker.icon(BitmapDescriptorFactory.fromBitmap(scaledAvatar));
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMap.addMarker(marker);
                    }
                });
            }

            return null;
        }

        private Bitmap getBitmapAvatar(User user) {
            Bitmap scaledBitmap = null;

            try {
                byte[] bytes = user.getAvatar().getData();
                Bitmap fetchedAvatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                if (fetchedAvatar != null) {
                    scaledBitmap = Bitmap.createScaledBitmap(
                            fetchedAvatar,
                            50, 50,
                            true);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return scaledBitmap;
        }
    }
}
