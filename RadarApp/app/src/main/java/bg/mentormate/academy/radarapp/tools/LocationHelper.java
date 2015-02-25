package bg.mentormate.academy.radarapp.tools;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

/**
 * Created by tl on 25.02.15.
 */
public class LocationHelper {

    public static Location getLastKnownLocation(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location == null) {
            location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        return location;
    }

}
