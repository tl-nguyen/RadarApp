package bg.mentormate.academy.radarapp.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.ArrayList;
import java.util.List;

import bg.mentormate.academy.radarapp.Constants;
import bg.mentormate.academy.radarapp.models.User;
import bg.mentormate.academy.radarapp.views.UserItem;

/**
 * Created by tl on 17.02.15.
 */
public class UserAdapter extends ParseQueryAdapter<User> {

    private static final int LIMIT = 50;

    public UserAdapter(final Context context, final String searchQuery, final String state, final User user) {
        super(context, new QueryFactory<User>() {

            @Override
            public ParseQuery<User> create() {
                ParseQuery query = new ParseQuery(Constants.USER_TABLE);
                query.orderByDescending(Constants.PARSE_COL_CREATED_AT);

                if (state.equals(Constants.SEARCH)) {
                    query.setLimit(LIMIT);
                } else if (state.equals(Constants.FOLLOWING) && user != null) {
                    List<User> followings = user.getFollowing();
                    List<String> objectIds = new ArrayList<>();

                    for (User user: followings) {
                        objectIds.add(user.getObjectId());
                    }

                    query.whereContainedIn(Constants.PARSE_COL_OBJECT_ID, objectIds);
                }

                if (searchQuery != null) {
                    query.whereContains(Constants.USER_COL_USERNAME, searchQuery);
                }

                return query;
            }
        });
    }

    @Override
    public View getItemView(User user, View v, ViewGroup parent) {
        if (v == null) {
            v = new UserItem(getContext());
        }

        ((UserItem) v).setData(user);

        return v;
    }
}
