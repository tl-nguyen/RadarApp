package bg.mentormate.academy.radarapp;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import bg.mentormate.academy.radarapp.models.CurrentLocation;
import bg.mentormate.academy.radarapp.models.Room;
import bg.mentormate.academy.radarapp.models.User;

/**
 * Created by tl on 05.02.15.
 */
public class RadarApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);

        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Room.class);
        ParseObject.registerSubclass(CurrentLocation.class);

        Parse.initialize(this,
                "cuV0C51aioI2eMuE5eAncgkUgZL5XBaTQCqzigwx",
                "z3dJcBEZZIlt6MofwQXfh5P7cimHLBwxNh10LxKF");
    }
}
