package bg.mentormate.academy.radarapp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ToggleButton;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.List;

import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.models.Follow;
import bg.mentormate.academy.radarapp.models.User;
import bg.mentormate.academy.radarapp.tools.AlertHelper;

/**
 * Created by tl on 18.02.15.
 */
public class FollowButton extends ToggleButton {

    public FollowButton(Context context) {
        super(context);
        init();
    }

    public FollowButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FollowButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setTextOn(getContext().getString(R.string.unfollow_text_btn));
        setTextOff(getContext().getString(R.string.follow_text_btn));
    }

    public void setData(final User currentUser, final User followingUser) {
        final Follow follow = currentUser.getFollow();

        follow.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                final List<User> followingUsers = follow.getFollowings();

                if (followingUser.equals(currentUser)) {
                    setVisibility(View.GONE);
                    return;
                }

                if (followingUsers.contains(followingUser)) {
                    setChecked(true);
                } else {
                    setChecked(false);
                }

                setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onFollowClicked(followingUsers, followingUser, currentUser);
                    }
                });
            }
        });
    }

    private void onFollowClicked(List<User> followingUsers, final User followingUser, final User currentUser) {
        if (followingUsers.contains(followingUser)) {
            followingUsers.remove(followingUser);
        } else {
            followingUsers.add(followingUser);
        }

        currentUser.getFollow().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    followingUser.getFollow().fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            if (e == null) {
                                Follow follow = (Follow) parseObject;
                                List<User> followers = follow.getFollowers();
                                if (followers.contains(currentUser)) {
                                    followers.remove(currentUser);
                                } else {
                                    followers.add(currentUser);
                                }
                                follow.saveInBackground();
                            } else {
                                AlertHelper.alert(getContext(),
                                        getContext().getString(R.string.dialog_error_title),
                                        e.getMessage());
                            }
                        }
                    });
                } else {
                    AlertHelper.alert(getContext(),
                            getContext().getString(R.string.dialog_error_title),
                            e.getMessage());
                }
            }
        });
    }
}
