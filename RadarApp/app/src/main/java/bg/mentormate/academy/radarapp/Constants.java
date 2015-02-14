package bg.mentormate.academy.radarapp;

/**
 * Created by tl on 10.02.15.
 */
public class Constants {

    public final static String ROOM_ID = "ROOM_ID";

    /**
     * Parse.com common cols
     */
    public final static String PARSE_COL_CREATED_AT = "createdAt";

    /**
     * Parse.com Room table constants
     */
    public final static String ROOM_TABLE = "Room";
    public final static String ROOM_COL_NAME = "name";
    public final static String ROOM_COL_PASSKEY = "passKey";
    public final static String ROOM_COL_CREATED_BY = "createdBy";
    public final static String ROOM_COL_USERS = "users";

    /**
     * Parse.com _User table constants
     */
    public final static String USER_TABLE = "_User";
    public final static String USER_COL_CURRENT_LOCATION = "currentLocation";
    public final static String USER_COL_ROOM = "room";
    public final static String USER_COL_AVATAR = "avatar";
    public final static String USER_COL_FOLLOWERS = "followers";
    public final static String USER_COL_FOLLOWING = "following";
}
