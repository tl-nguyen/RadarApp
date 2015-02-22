package bg.mentormate.academy.radarapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;

import java.util.List;

import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.models.User;

/**
 * Created by lopi on 2/22/2015.
 */
public class UserToDeleteAdapter extends BaseAdapter {

    private Context mContext;
    private List<User> mUsers;

    public UserToDeleteAdapter(Context context, List<User> users) {
        mContext = context;
        mUsers = users;
    }

    @Override
    public int getCount() {
        return mUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_user_cell, parent, false);
        }

        final ParseImageView ivIcon = (ParseImageView) convertView.findViewById(R.id.ivIcon);
        final TextView tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);

        final User u = mUsers.get(position);
        u.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                ParseFile avatar = u.getAvatar();
                if (avatar != null)
                    ivIcon.setParseFile(avatar);
                String username = u.getUsername();
                if (username != null)
                    tvUsername.setText(username);

                ivIcon.loadInBackground();
            }
        });


        return convertView;
    }
}