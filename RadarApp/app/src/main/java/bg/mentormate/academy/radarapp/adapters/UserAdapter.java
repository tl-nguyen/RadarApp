package bg.mentormate.academy.radarapp.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import bg.mentormate.academy.radarapp.Constants;
import bg.mentormate.academy.radarapp.models.User;
import bg.mentormate.academy.radarapp.views.UserItem;

/**
 * Created by tl on 17.02.15.
 */
public class UserAdapter extends ParseQueryAdapter<User> {

    private static final int LIMIT = 50;

    public UserAdapter(final Context context, final String searchQuery) {
        super(context, new QueryFactory<User>() {

            @Override
            public ParseQuery<User> create() {
                ParseQuery query = new ParseQuery(Constants.USER_TABLE);
                query.orderByDescending(Constants.PARSE_COL_CREATED_AT);

                if (searchQuery != null) {
                    query.whereContains(Constants.USER_COL_USERNAME, searchQuery);
                }

                query.setLimit(LIMIT);

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
