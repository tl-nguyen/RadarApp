package bg.mentormate.academy.radarapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.List;

/**
 * Created by tl on 08.02.15.
 */

@ParseClassName("Room")
public class Room extends ParseObject{
    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        put("name", name);
    }

    public String getPassKey() {
        return getString("passKey");
    }

    public void setPassKey(String passKey) {
        put("passKey", passKey);
    }

    public User getCreatedBy() {
        return (User) getParseUser("createdBy");
    }

    public void setCreatedBy(User createdBy) {
        put("createdBy", createdBy);
    }

    public List<User> getUsers() {
        return getList("users");
    }

    public void setUsers(List<User> users) {
        put("users", users);
    }
}
