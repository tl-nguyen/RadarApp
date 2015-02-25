package bg.mentormate.academy.radarapp.tools;

import com.parse.GetCallback;
import com.parse.ParseQuery;

import bg.mentormate.academy.radarapp.Constants;
import bg.mentormate.academy.radarapp.models.Room;
import bg.mentormate.academy.radarapp.models.User;

/**
 * Created by tl on 25.02.15.
 */
public class QueryHelper {

    public static void getRoomById(String roomId, GetCallback<Room> callback) {
        ParseQuery<Room> query = new ParseQuery<>(Constants.ROOM_TABLE);

        query.getInBackground(roomId, callback);
    }

    public static void getUserById(String userId, GetCallback<User> callback) {
        ParseQuery<User> query = new ParseQuery<>(Constants.USER_TABLE);

        query.getInBackground(userId, callback);
    }
}
