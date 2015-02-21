package bg.mentormate.academy.radarapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseImageView;

import java.util.List;

import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.models.User;

/**
 * Created by tl on 21.02.15.
 */
public class UserAdapter extends BaseAdapter {

    private Context mContext;
    private List<User> mUsers;

    public UserAdapter(Context context, List<User> users) {
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

        ParseImageView ivIcon = (ParseImageView) convertView.findViewById(R.id.ivIcon);
        TextView tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);

        ivIcon.setParseFile(mUsers.get(position).getAvatar());
        tvUsername.setText(mUsers.get(position).getUsername());

        ivIcon.loadInBackground();

        return convertView;
    }
}
