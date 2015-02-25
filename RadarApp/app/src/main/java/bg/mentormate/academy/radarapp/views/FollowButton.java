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
import bg.mentormate.academy.radarapp.tools.NotificationHelper;

/**
 * Created by tl on 18.02.15.
 */
public class FollowButton extends ToggleButton {

    public FollowButton(Context context) {
        super(context);
    }

    public FollowButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FollowButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
                    setColorToggleOn();
                    setChecked(true);
                } else {
                    setColorToggleOff();
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

    private void setColorToggleOn() {
        setBackgroundColor(getResources().getColor(R.color.br_toggle_on));
        setTextColor(getResources().getColor(android.R.color.white));
    }

    private void setColorToggleOff() {
        setBackgroundColor(getResources().getColor(R.color.br_button));
        setTextColor(getResources().getColor(R.color.br_text));
    }

    private void onFollowClicked(List<User> followingUsers, final User followingUser, final User currentUser) {
        if (followingUsers.contains(followingUser)) {
            setColorToggleOff();
            followingUsers.remove(followingUser);
        } else {
            setColorToggleOn();
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
                                NotificationHelper.alert(getContext(),
                                        getContext().getString(R.string.dialog_error_title),
                                        e.getMessage());
                            }
                        }
                    });
                } else {
                    NotificationHelper.alert(getContext(),
                            getContext().getString(R.string.dialog_error_title),
                            e.getMessage());
                }
            }
        });
    }
}
