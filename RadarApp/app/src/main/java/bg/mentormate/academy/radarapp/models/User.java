package bg.mentormate.academy.radarapp.models;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

/**
 * Created by tl on 05.02.15.
 */

@ParseClassName("_User")
public class User extends ParseUser{

    public ParseGeoPoint getCurrentLocation() {
        return getParseGeoPoint("currentLocation");
    }

    public void setCurrentLocation(ParseGeoPoint currentLocation) {
        put("currentLocation", currentLocation);
    }

}
