package bg.mentormate.academy.radarapp;

/**
 * Created by tl on 10.02.15.
 */
public class Constants {

    public final static String INACTIVE_STATE = "INACTIVE";

    public final static String CATEGORY = "CATEGORY";
    public final static String FOLLOWER = "FOLLOWER";
    public final static String FOLLOWING = "FOLLOWING";
    public final static String SEARCH = "SEARCH";

    public final static String ROOM_ID = "ROOM_ID";

    /**
     * Parse.com common cols
     */
    public final static String PARSE_COL_CREATED_AT = "createdAt";
    public final static String PARSE_COL_OBJECT_ID = "objectId";

    /**
     * Parse.com Follow table constants
     */
    public final static String FOLLOW_TABLE = "Follow";
    public final static String FOLLOW_COL_FOLLOWERS = "followers";
    public final static String FOLLOW_COL_FOLLOWINGS = "followings";

    /**
     * Parse.com CurrentLocation table constants
     */
    public final static String CURRENT_LOCATION_TABLE = "CurrentLocation";
    public final static String CURRENT_LOCATION_COL_LOCATION = "location";
    public final static String CURRENT_LOCATION_COL_PROVIDER = "provider";
    public final static String CURRENT_LOCATION_COL_ACTIVE = "active";
    public static final String CURRENT_LOCATION_COL_STATUS = "status";

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
    public final static String USER_COL_FOLLOW = "follow";
    public final static String USER_COL_USERNAME = "username";

    /***
     * Constants for working with the camera
     */
    public static final int ACTION_TAKE_PHOTO = 0;
    public static final String CAMERA_DIR = "/DCIM/";
    public static final String JPEG_FILE_PREFIX = "IMG_";
    public static final String JPEG_FILE_SUFFIX = ".jpg";
    public static final String ALBUM_NAME = "CameraSample";
}
