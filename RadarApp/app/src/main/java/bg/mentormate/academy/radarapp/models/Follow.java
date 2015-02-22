package bg.mentormate.academy.radarapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.List;

import bg.mentormate.academy.radarapp.Constants;

/**
 * Created by tl on 22.02.15.
 */
@ParseClassName(Constants.FOLLOW_TABLE)
public class Follow extends ParseObject {
    public List<User> getFollowers() {
        return getList(Constants.FOLLOW_COL_FOLLOWERS);
    }

    public void setFollowers(List<User> followers) {
        put(Constants.FOLLOW_COL_FOLLOWERS, followers);
    }

    public List<User> getFollowings() {
        return getList(Constants.FOLLOW_COL_FOLLOWINGS);
    }

    public void setFollowings(List<User> followings) {
        put(Constants.FOLLOW_COL_FOLLOWINGS, followings);
    }
}
