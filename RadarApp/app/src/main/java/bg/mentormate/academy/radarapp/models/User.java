package bg.mentormate.academy.radarapp.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

import bg.mentormate.academy.radarapp.Constants;

/**
 * Created by tl on 05.02.15.
 */

@ParseClassName(Constants.USER_TABLE)
public class User extends ParseUser{

    public UserDetail getUserDetail() {
        return (UserDetail) getParseObject(Constants.USER_COL_USER_DETAIL);
    }

    public void setUserDetail(UserDetail userDetail) {
        put(Constants.USER_COL_USER_DETAIL, userDetail);
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

    public Follow getFollow() {
        return (Follow) getParseObject(Constants.USER_COL_FOLLOW);
    }

    public void setFollow(Follow follow) {
        put(Constants.USER_COL_FOLLOW, follow);
    }
}
