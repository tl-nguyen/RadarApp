package bg.mentormate.academy.radarapp.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

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
import com.parse.SaveCallback;

import java.util.List;

import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.models.Constants;
import bg.mentormate.academy.radarapp.models.Room;
import bg.mentormate.academy.radarapp.models.User;
import bg.mentormate.academy.radarapp.tools.AlertHelper;

public class RoomActivity extends ActionBarActivity {

    private static final String ROOM_ID = "ROOM_ID";

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private User mUser;
    private Room mRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        mUser = (User) User.getCurrentUser();

        String roomId = getIntent().getStringExtra(ROOM_ID);

        if (roomId != null) {
            retrieveRoom(roomId);
        }
    }

    private void retrieveRoom(String roomId) {
        ParseQuery query = new ParseQuery(Constants.ROOM_TABLE);

        query.getInBackground(roomId, new GetCallback() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    mRoom = (Room) parseObject;

                    setUpMapIfNeeded();
                } else {
                    AlertHelper.alert(RoomActivity.this,
                            getString(R.string.dialog_error_title),
                            e.getMessage());
                }
            }
        });
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

    private void setUpMap() {
        ParseGeoPoint currentLocation = addingMarkers();

//        mMap.setMyLocationEnabled(true);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(),
                currentLocation.getLongitude())));

        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

        startListening();
    }

    private ParseGeoPoint addingMarkers() {
        mMap.clear();

        try {
            mRoom.fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<User> users = mRoom.getUsers();

        ParseGeoPoint currentLocation = mUser.getCurrentLocation();

        for (User user: users) {
            MarkerOptions marker = new MarkerOptions();

            ParseGeoPoint userLocation = user.getCurrentLocation();

            marker.position(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()))
                    .title(user.getUsername());

            byte[] bytes = new byte[0];

            try {
                bytes = user.getAvatar().getData();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Bitmap scaledBmp = Bitmap.createScaledBitmap(
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.length),
                    50, 50,
                    true);

            marker.icon(BitmapDescriptorFactory.fromBitmap(scaledBmp));

            mMap.addMarker(marker);
        }
        return currentLocation;
    }

    private void startListening() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        HandlerThread thread = new HandlerThread("locationThread");
        thread.start();

        Looper looper = thread.getLooper();

        CustomLocationListener gpsListener = new CustomLocationListener();
        CustomLocationListener networkListener = new CustomLocationListener();

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsListener, looper);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, networkListener, looper);
    }

    private class CustomLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(final Location location) {
            mUser.setCurrentLocation(new ParseGeoPoint(location.getLatitude(),
                    location.getLongitude()));
            mUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        RoomActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RoomActivity.this, "Location: " + location.getLatitude() + "/" + location.getLongitude(),
                                        Toast.LENGTH_LONG).show();

                                addingMarkers();
                            }
                        });
                    } else {
                        AlertHelper.alert(RoomActivity.this,
                                getString(R.string.dialog_error_title),
                                e.getMessage());
                    }
                }
            });
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
