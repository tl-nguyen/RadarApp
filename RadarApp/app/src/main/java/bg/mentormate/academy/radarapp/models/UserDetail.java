package bg.mentormate.academy.radarapp.models;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import bg.mentormate.academy.radarapp.Constants;

/**
 * Created by tl on 14.02.15.
 */

@ParseClassName(Constants.USER_DETAIL_TABLE)
public class UserDetail extends ParseObject {

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(Constants.USER_DETAIL_COL_LOCATION);
    }

    public void setLocation(ParseGeoPoint location) {
        put(Constants.USER_DETAIL_COL_LOCATION, location);
    }

    public String getProvider() {
        return getString(Constants.USER_DETAIL_COL_PROVIDER);
    }

    public void setProvider(String provider) {
        put(Constants.USER_DETAIL_COL_PROVIDER, provider);
    }

    public boolean getActive() {
        return getBoolean(Constants.USER_DETAIL_COL_ACTIVE);
    }

    public void setActive(boolean active) {
        put(Constants.USER_DETAIL_COL_ACTIVE, active);
    }

    public String getStatus() { return getString(Constants.USER_DETAIL_COL_STATUS); }

    public void setStatus(String status) { put(Constants.USER_DETAIL_COL_STATUS, status); }
}
