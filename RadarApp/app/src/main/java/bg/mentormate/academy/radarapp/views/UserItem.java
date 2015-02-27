package bg.mentormate.academy.radarapp.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;

import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.activities.ProfileActivity;
import bg.mentormate.academy.radarapp.data.LocalDb;
import bg.mentormate.academy.radarapp.models.User;

/**
 * Created by tl on 18.02.15.
 */
public class UserItem extends LinearLayout implements View.OnClickListener {

    private static final String USER_ID = "USER_ID";

    private TextView mTvUsername;
    private ParseImageView mPivAvatar;
    private FollowButton mFbFollow;

    private User mSelectedUser;
    private User mCurrentUser;

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

        mCurrentUser = LocalDb.getInstance().getCurrentUser();

        mTvUsername = (TextView) findViewById(R.id.tvUsername);
        mPivAvatar = (ParseImageView) findViewById(R.id.pivAvatar);
        mFbFollow = (FollowButton) findViewById(R.id.fbFollow);

        mTvUsername.setOnClickListener(this);
        mPivAvatar.setOnClickListener(this);
    }

    public void setData(User user) {
        mSelectedUser = user;

        mSelectedUser.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                mTvUsername.setText(mSelectedUser.getUsername());
                mPivAvatar.setParseFile(mSelectedUser.getAvatar());

                if (mSelectedUser.getAvatar() != null) {
                    mPivAvatar.loadInBackground();
                } else {
                    mPivAvatar.setBackground(getResources().getDrawable(R.drawable.ic_avatar));
                }

                if (!mCurrentUser.equals(mSelectedUser)) {
                    mFbFollow.setData(mCurrentUser, mSelectedUser);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.tvUsername:
            case R.id.pivAvatar:
                goToProfile();
                break;
        }
    }

    private void goToProfile() {
        Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
        profileIntent.putExtra(USER_ID, mSelectedUser.getObjectId());
        getContext().startActivity(profileIntent);
    }
}
