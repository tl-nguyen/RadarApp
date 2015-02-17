package bg.mentormate.academy.radarapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import bg.mentormate.academy.radarapp.Constants;
import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.models.User;

/**
 * Created by tl on 17.02.15.
 */
public class UserAdapter extends ParseQueryAdapter<User> {

    private static final int LIMIT = 50;

    private Context mContext;

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

        mContext = context;
    }

    @Override
    public View getItemView(User user, View v, ViewGroup parent) {
        View row = v;
        final User selectedUser = user;
        final UserHolder holder;

        if (row == null) {
            row = LayoutInflater.from(mContext).inflate(R.layout.item_list_user, parent, false);

            holder = new UserHolder();
            holder.username = (TextView) row.findViewById(R.id.tvUsername);
            holder.avatar = (ParseImageView) row.findViewById(R.id.pivAvatar);

            row.setTag(holder);
        } else {
            holder = (UserHolder) row.getTag();
        }

        super.getItemView(selectedUser, v, parent);

        if (selectedUser != null) {
            selectedUser.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    holder.username.setText(selectedUser.getUsername());
                    holder.avatar.setParseFile(selectedUser.getAvatar());
                    holder.avatar.loadInBackground();
                }
            });
        }

        return row;
    }

    static class UserHolder {
        private TextView username;
        private ParseImageView avatar;
    }
}
