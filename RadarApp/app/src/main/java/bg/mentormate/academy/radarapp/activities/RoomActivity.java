package bg.mentormate.academy.radarapp.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

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

import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.models.Constants;
import bg.mentormate.academy.radarapp.models.Room;
import bg.mentormate.academy.radarapp.models.User;
import bg.mentormate.academy.radarapp.tools.AlertHelper;

public class RoomActivity extends FragmentActivity {

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
        setupPositionMarkers();
    }

    private void setupPositionMarkers() {
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

            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, 50, 50, true);

            marker.icon(BitmapDescriptorFactory.fromBitmap(scaledBmp));

            mMap.addMarker(marker);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(),
                currentLocation.getLongitude())));

        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
    }

//    private void setupMyPositionMarker() {
//        final MarkerOptions myMarkerOptions = new MarkerOptions();
//
//        // Enable MyLocation Layer of Google Map
//        mMap.setMyLocationEnabled(true);
//
//        // Get LocationManager object from System Service LOCATION_SERVICE
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//        // Create a criteria object to retrieve provider
//        Criteria criteria = new Criteria();
//
//        // Get the name of the best provider
//        String provider = locationManager.getBestProvider(criteria, true);
//
//        // Get Current Location
//        Location myLocation = locationManager.getLastKnownLocation(provider);
//
//        // set map type
//        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//
//        // Get latitude of the current location
//        double latitude = myLocation.getLatitude();
//
//        // Get longitude of the current location
//        double longitude = myLocation.getLongitude();
//
//        // Create a LatLng object for the current location
//        LatLng latLng = new LatLng(latitude, longitude);
//
//        // Show the current location in Google Map
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//
//        // Zoom in the Google Map
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
//
//
//        myMarkerOptions.position(new LatLng(latitude, longitude))
//                .title(mUser.getUsername());
//
//        byte[] bytes = new byte[0];
//        try {
//            bytes = mUser.getAvatar().getData();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//
//        Bitmap bitmap = Bitmap.createScaledBitmap(bmp, 50, 50, true);
//
//        myMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
//
//        mMap.addMarker(myMarkerOptions);
//
//        mUser.setCurrentLocation(new ParseGeoPoint(myLocation.getLatitude(), myLocation.getLongitude()));
//        mUser.saveInBackground();
//    }
}
