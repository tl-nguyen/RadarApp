package bg.mentormate.academy.radarapp.services;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;

import bg.mentormate.academy.radarapp.data.LocalDb;
import bg.mentormate.academy.radarapp.models.CurrentLocation;
import bg.mentormate.academy.radarapp.models.User;

/**
 * Created by tl on 13.02.15.
 */
public class ServiceLocationListener implements LocationListener {
    private User mCurrentUser;

    public ServiceLocationListener() {
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(ServiceLocationListener.class.getSimpleName(),
                "Provider: " +location.getProvider() + "|" +
                "Lat/Lng: " + location.getLatitude() + "/" + location.getLongitude());

        if (LocalDb.getInstance() != null) {
            mCurrentUser = LocalDb.getInstance().getCurrentUser();

            CurrentLocation currentLocation = mCurrentUser.getCurrentLocation();

            currentLocation.setLocation(
                    new ParseGeoPoint(
                            location.getLatitude(),
                            location.getLongitude()));

            try {
                currentLocation.save();
            } catch (ParseException e) {
                Log.d(ServiceLocationListener.class.getSimpleName(),
                                e.getMessage());
            }
        }

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
