package bg.mentormate.academy.radarapp.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.List;

import bg.mentormate.academy.radarapp.Constants;

/**
 * Created by tl on 05.02.15.
 */

@ParseClassName(Constants.USER_TABLE)
public class User extends ParseUser{

    public ParseGeoPoint getCurrentLocation() {
        return getParseGeoPoint(Constants.USER_COL_CURRENT_LOCATION);
    }

    public void setCurrentLocation(ParseGeoPoint currentLocation) {
        put(Constants.USER_COL_CURRENT_LOCATION, currentLocation);
    }

    public Room getRoom() {
        return (Room) getParseObject(Constants.USER_COL_ROOM);
    }

    public void setRoom(Room room) {
        put(Constants.USER_COL_ROOM, room);
    }

    public ParseFile getAvatar() {
        return getParseFile(Constants.USER_COL_AVATAR);
    }

    public void setAvatar(ParseFile avatar) {
        put(Constants.USER_COL_AVATAR, avatar);
    }

    public List<User> getFollowers() {
        return getList(Constants.USER_COL_FOLLOWERS);
    }

    public void setFollowers(List<User> followers) {
        put(Constants.USER_COL_FOLLOWERS, followers);
    }

    public List<User> getFollowing() {
        return getList(Constants.USER_COL_FOLLOWING);
    }

    public void setFollowing(List<User> following) {
        put(Constants.USER_COL_FOLLOWING, following);
    }
}
