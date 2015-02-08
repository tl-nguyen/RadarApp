package bg.mentormate.academy.radarapp.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.List;

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

    public ParseFile getAvatar() {
        return getParseFile("avatar");
    }

    public void setAvatar(ParseFile avatar) {
        put("avatar", avatar);
    }

    public List<User> getFollowers() {
        return getList("followers");
    }

    public void setFollowers(List<User> followers) {
        put("followers", followers);
    }

    public List<User> getFollowing() {
        return getList("following");
    }

    public void setFollowing(List<User> following) {
        put("following", following);
    }
}
