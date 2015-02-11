package bg.mentormate.academy.radarapp.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import bg.mentormate.academy.radarapp.R;

public class RoomActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private LatLng mSofia = new LatLng(42.646539, 23.378262);
    private LatLng mPlovdiv = new LatLng(42.136200, 24.754512);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link com.google.android.gms.maps.SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
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

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

//        mMap.moveCamera();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mSofia, 15.0f), 5000, null);

        mMap.addMarker(new MarkerOptions()
                        .position(mSofia)
                        .title("Mentormate Sofia")
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        );

//        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

//        CircleOptions circleOptions = new CircleOptions();
//        circleOptions.center(mSofia);
//        circleOptions.radius(1000f);

//        Circle circle = mMap.addCircle(circleOptions);

//        PolygonOptions polygonOptions = new PolygonOptions();
//        polygonOptions.add(
//                mSofia,
//                new LatLng(mSofia.latitude, mSofia.longitude + 0.1f),
//                new LatLng(mSofia.latitude + 0.1f, mSofia.longitude + 0.1f),
//                new LatLng(mSofia.latitude + 0.1f, mSofia.longitude),
//                mSofia
//        );

//        Polygon polygon = mMap.addPolygon(polygonOptions);
//        polygon.setFillColor(Color.BLUE);
//        polygon.setStrokeColor(Color.GREEN);

//        Uri streetViewURI = Uri.parse("google.streetview:cbll=46.414382,10.013988");
//        Intent showStreetView = new Intent(Intent.ACTION_VIEW, streetViewURI);
//        showStreetView.setPackage("com.google.android.apps.maps");
//        if (showStreetView.resolveActivity(getPackageManager()) != null) {
//            startActivity(showStreetView);
//        }

    }
}
