package bg.mentormate.academy.radarapp.tools;

import com.parse.GetCallback;
import com.parse.ParseQuery;

import bg.mentormate.academy.radarapp.Constants;

/**
 * Created by tl on 25.02.15.
 */
public class QueryHelper {

    public static void getRoomById(String roomId, GetCallback callback) {
        ParseQuery query = new ParseQuery(Constants.ROOM_TABLE);

        query.getInBackground(roomId, callback);
    }
}
