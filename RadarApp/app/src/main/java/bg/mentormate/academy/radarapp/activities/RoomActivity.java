package bg.mentormate.academy.radarapp.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bg.mentormate.academy.radarapp.Constants;
import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.adapters.RoomUserAdapter;
import bg.mentormate.academy.radarapp.data.LocalDb;
import bg.mentormate.academy.radarapp.models.CurrentLocation;
import bg.mentormate.academy.radarapp.models.Room;
import bg.mentormate.academy.radarapp.models.User;
import bg.mentormate.academy.radarapp.services.LocationTrackingService;
import bg.mentormate.academy.radarapp.services.RetrieveRoomDataService;
import bg.mentormate.academy.radarapp.tools.BitmapHelper;
import bg.mentormate.academy.radarapp.tools.LocationHelper;
import bg.mentormate.academy.radarapp.tools.NotificationHelper;
import bg.mentormate.academy.radarapp.tools.QueryHelper;

public class RoomActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private final static long DATA_UPDATE_INTERVAL = 4000;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private LocalDb mLocalDb;
    private User mCurrentUser;
    private Room mRoom;
    private List<User> mUsers;
    private Map<String, Marker> mMarkers;
    private RoomUserAdapter mRoomUserAdapter;

    private Intent mDataServiceIntent;

    private GridView mGvUsers;

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

        initData();
        initViews();
        setUpMapIfNeeded();
    }

    private void initData() {
        mUsers = new ArrayList<>();
        mMarkers = new HashMap<>();

        mLocalDb = LocalDb.getInstance();
        mCurrentUser = (User) User.getCurrentUser();
    }

    private void initViews() {
        mGvUsers = (GridView) findViewById(R.id.gvUsers);
        mRoomUserAdapter = new RoomUserAdapter(this, mUsers);
        mGvUsers.setAdapter(mRoomUserAdapter);
        mGvUsers.setOnItemClickListener(this);
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map))
                    .getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            mMap = googleMap;

                            // Check if we were successful in obtaining the map.
                            if (mMap != null) {
                                setUpMap();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        startServiceForLocationTracking();
        startServiceForUpdatingPositions();

        registerReceiver(positionsUpdateReceiver,
                new IntentFilter(RetrieveRoomDataService.BROADCAST_RESULT));
    }

    private void startServiceForLocationTracking() {
        Intent trackingIntent = new Intent(this, LocationTrackingService.class);
        trackingIntent.setAction(LocationTrackingService.ACTION_START_MONITORING);
        startService(trackingIntent);
    }

    private void startServiceForUpdatingPositions() {
        final long ALARM_TRIGGER_AT_TIME = SystemClock.elapsedRealtime() + 20000;
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        mDataServiceIntent = new Intent(this, RetrieveRoomDataService.class);

        PendingIntent pendingIntent = PendingIntent.getService(this, 0, mDataServiceIntent, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                ALARM_TRIGGER_AT_TIME,
                DATA_UPDATE_INTERVAL,
                pendingIntent);
    }

    private void setUpMap() {
        mMap.setMyLocationEnabled(true);

        navigateToCurrentLocationOnMap();

        String roomId = getIntent().getStringExtra(Constants.ROOM_ID);

        if (roomId != null) {
            QueryHelper.getRoomById(roomId, new GetCallback<Room>() {
                @Override
                public void done(Room room, ParseException e) {
                    if (e == null) {
                        mRoom = room;
                        mLocalDb.setSelectedRoom(mRoom);

                        // Set activity title
                        getSupportActionBar().setTitle(mRoom.getName());

                        // Do an async update
                        updateMarkers();
                    } else {
                        NotificationHelper.alert(RoomActivity.this,
                                getString(R.string.dialog_error_title),
                                e.getMessage());
                    }
                }
            });
        }
    }

    private void navigateToCurrentLocationOnMap() {
        final CurrentLocation locationOnDb = mCurrentUser.getCurrentLocation();
        final Location lastKnownLocation = LocationHelper.getLastKnownLocation(this);

        if (lastKnownLocation != null) {
            // Show the position of the last know location on the map
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(lastKnownLocation.getLatitude(),
                            lastKnownLocation.getLongitude()),
                    15));

            // Save the last know location to Db
            locationOnDb.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    locationOnDb.setLocation(new ParseGeoPoint(lastKnownLocation.getLatitude(),
                            lastKnownLocation.getLongitude()));

                    locationOnDb.saveInBackground();
                }
            });
        } else {
            // Show the position from Db on the map
            locationOnDb.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(locationOnDb.getLocation().getLatitude(),
                                    locationOnDb.getLocation().getLongitude()),
                            15));
                }
            });
        }
    }

    private void updateMarkers() {
        MarkersUpdateTask markersUpdateTask = new MarkersUpdateTask();
        markersUpdateTask.execute();
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ParseGeoPoint userLocation = mUsers.get(position)
                .getCurrentLocation()
                .getLocation();

        LatLng latLng = new LatLng(userLocation.getLatitude(),
                userLocation.getLongitude());

        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
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

    /**
     * Using this class to do updating the markers in background
     */
    private class MarkersUpdateTask extends AsyncTask<Void, MarkerOptions, Void> {

        // Those variables are needed for building the avatar icon
        private IconGenerator mIconGenerator;
        private ImageView mImageView;

        private MarkersUpdateTask() {
            this.mIconGenerator = new IconGenerator(RoomActivity.this);
            this.mImageView = new ImageView(RoomActivity.this);
        }

        @Override
        protected Void doInBackground(Void... params) {
            List<User> fetchedUsers = mRoom.getUsers();

            boolean isInTheRoom = false;
            boolean newUserAdded = false;
            boolean userHasUnregistered = false;

            // Naive way to check if someone has left and remove his marker from the Map
            for (User localUser : mUsers) {
                if (!fetchedUsers.contains(localUser)) {
                    userHasUnregistered = true;
                    mUsers.remove(localUser);

                    if (mMarkers.containsKey(localUser.getObjectId())) {
                        Marker marker = mMarkers.get(localUser.getObjectId());

                        removeMarkerFromMap(marker);
                        mMarkers.remove(localUser.getObjectId());
                    }
                }
            }

            for (final User user: fetchedUsers) {
                ParseGeoPoint userLocation = null;
                final Bitmap[] avatarIcon = {null};

                try {
                    user.fetchIfNeeded();

                    if (!mUsers.contains(user)) {
                        mUsers.add(user);
                        newUserAdded = true;
                    }

                    // If the user is me, don't do anything
                    if (user.equals(mCurrentUser)) {
                        isInTheRoom = true;
                        continue;
                    }

                    user.getCurrentLocation().fetchIfNeeded();

                    userLocation = user.getCurrentLocation().getLocation();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // Visualize the user's marker
                if (userLocation != null) {
                    updateMarkerOnMap(user, userLocation, avatarIcon);
                }
            }

            // If the user is not from the room list then...
            if (!isInTheRoom) {
                kickFromTheRoom();
            }

            // Update the User Grid
            final boolean toRefreshTheUserGrid = newUserAdded || userHasUnregistered;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (toRefreshTheUserGrid) {
                        mRoomUserAdapter.notifyDataSetChanged();
                    }
                }
            });

            return null;
        }

        private void removeMarkerFromMap(final Marker marker) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    marker.remove();
                }
            });
        }

        private void updateMarkerOnMap(final User user,
                                       ParseGeoPoint userLocation,
                                       final Bitmap[] avatarIcon) {

            final LatLng latLngPosition = new LatLng(
                    userLocation.getLatitude(),
                    userLocation.getLongitude());

            // Add new marker to the markers Map Collection if it's not in it
            if (!mMarkers.containsKey(user.getObjectId())) {
                final MarkerOptions markerOptions = getMarkerOptions(user, avatarIcon, latLngPosition);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Marker marker = mMap.addMarker(markerOptions);
                        mMarkers.put(user.getObjectId(), marker);
                    }
                });
            } else {
                final Marker marker = mMarkers.get(user.getObjectId());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateMarker(marker, latLngPosition, avatarIcon, user);
                    }
                });
            }
        }

        private MarkerOptions getMarkerOptions(User user,
                                               Bitmap[] avatarIcon,
                                               LatLng position) {

            MarkerOptions markerOptions = new MarkerOptions();

            // Build user avatar
            avatarIcon[0] = BitmapHelper.buildAvatarIcon(
                    RoomActivity.this,
                    user,
                    mImageView,
                    mIconGenerator);

            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(avatarIcon[0]))
                    .title(user.getUsername())
                    .position(position);

            return markerOptions;
        }

        private void updateMarker(Marker marker,
                                  LatLng latLngPosition,
                                  Bitmap[] avatarIcon,
                                  User user) {
            user.getCurrentLocation().getActive();

            if (marker.getPosition().latitude != latLngPosition.latitude ||
                    marker.getPosition().longitude != latLngPosition.longitude) {
                marker.setPosition(latLngPosition);
            }

            // Build user avatar
            avatarIcon[0] = BitmapHelper.buildAvatarIcon(
                    RoomActivity.this,
                    user,
                    mImageView,
                    mIconGenerator);

            marker.setIcon(BitmapDescriptorFactory.fromBitmap(avatarIcon[0]));
            marker.setTitle(user.getUsername());
        }

        private void kickFromTheRoom() {
            // Intent to send a email to the room owner if needed
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String[] emailList = new String[1];
            emailList[0] = mRoom.getCreatedBy().getEmail();
            intent.putExtra(Intent.EXTRA_EMAIL, emailList);

            NotificationHelper.notifyTheUser(RoomActivity.this,
                    R.string.kick_out_text_title,
                    R.string.kickout_text_description,
                    intent);

            finish();
        }
    }
}
