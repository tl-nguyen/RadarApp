package bg.mentormate.academy.radarapp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;

import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.models.User;

/**
 * Created by tl on 18.02.15.
 */
public class UserItem extends LinearLayout {

    private TextView mTvUsername;
    private ParseImageView mPivAvatar;

    private User mUser;

    public UserItem(Context context) {
        super(context);
        init();
    }

    public UserItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.item_user, this);

        mTvUsername = (TextView) findViewById(R.id.tvUsername);
        mPivAvatar= (ParseImageView) findViewById(R.id.pivAvatar);
    }

    public void setData(User user) {
        mUser = user;

        mUser.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                mTvUsername.setText(mUser.getUsername());
                mPivAvatar.setParseFile(mUser.getAvatar());
                mPivAvatar.loadInBackground();
            }
        });
    }
}
