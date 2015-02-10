package bg.mentormate.academy.radarapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.List;

/**
 * Created by tl on 08.02.15.
 */

@ParseClassName(Constants.ROOM_TABLE)
public class Room extends ParseObject{
    public String getName() {
        return getString(Constants.ROOM_COL_NAME);
    }

    public void setName(String name) {
        put(Constants.ROOM_COL_NAME, name);
    }

    public String getPassKey() {
        return getString(Constants.ROOM_COL_PASSKEY);
    }

    public void setPassKey(String passKey) {
        put(Constants.ROOM_COL_PASSKEY, passKey);
    }

    public User getCreatedBy() {
        return (User) getParseUser(Constants.ROOM_COL_CREATED_BY);
    }

    public void setCreatedBy(User createdBy) {
        put(Constants.ROOM_COL_CREATED_BY, createdBy);
    }

    public List<User> getUsers() {
        return getList(Constants.ROOM_COL_USERS);
    }

    public void setUsers(List<User> users) {
        put(Constants.ROOM_COL_USERS, users);
    }
}
