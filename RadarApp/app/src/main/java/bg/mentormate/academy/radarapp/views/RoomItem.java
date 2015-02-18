package bg.mentormate.academy.radarapp.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import bg.mentormate.academy.radarapp.Constants;
import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.activities.ProfileActivity;
import bg.mentormate.academy.radarapp.activities.RoomActivity;
import bg.mentormate.academy.radarapp.models.Room;
import bg.mentormate.academy.radarapp.models.User;
import bg.mentormate.academy.radarapp.tools.AlertHelper;

/**
 * Created by tl on 18.02.15.
 */
public class RoomItem extends LinearLayout implements View.OnClickListener {

    private static final String USER_ID = "USER_ID";

    private TextView mTvRoomName;
    private TextView mTvUsername;
    private ParseImageView mPivAvatar;
    private Button mBtnJoin;

    private User mUser;
    private Room mRoom;

    public RoomItem(Context context) {
        super(context);
        init();
    }

    public RoomItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.item_room, this);

        mTvRoomName = (TextView) findViewById(R.id.tvRoomName);
        mTvUsername = (TextView) findViewById(R.id.tvUsername);
        mPivAvatar = (ParseImageView) findViewById(R.id.pivAvatar);
        mBtnJoin = (Button) findViewById(R.id.btnJoin);

        mTvUsername.setOnClickListener(this);
        mPivAvatar.setOnClickListener(this);
        mBtnJoin.setOnClickListener(this);
    }

    public void setData(Room room, User user) {
        mRoom = room;
        mUser = user;

        mRoom.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                mTvRoomName.setText(mRoom.getName());
            }
        });

        mUser.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                mTvUsername.setText(mUser.getUsername());
                mPivAvatar.setParseFile(mUser.getAvatar());
                mPivAvatar.loadInBackground();
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
            case R.id.btnJoin:
                onJoinClicked();
                break;
        }
    }

    private void goToProfile() {
        Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
        profileIntent.putExtra(USER_ID, mUser.getObjectId());
        getContext().startActivity(profileIntent);
    }

    private void onJoinClicked() {
        if (!mRoom.getUsers().contains(mUser)) {
            checkForPassKey();
        } else {
            goToRoom();
        }
    }

    private void checkForPassKey() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View dvCreateRoom = inflater.inflate(R.layout.dialog_passkey_check, null);

        final EditText etPassKey = (EditText) dvCreateRoom.findViewById(R.id.etPassKey);

        builder.setView(dvCreateRoom)
                .setTitle(getContext().getString(R.string.check_keypass_title))
                .setPositiveButton(getContext().getString(R.string.got_it_btn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String passKey = etPassKey.getText().toString().trim();

                        if (passKey.equals(mRoom.getPassKey())) {
                            // Go to Room if the passkey is correct
                            mRoom.getUsers().add(mUser);
                            mRoom.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        goToRoom();
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
                                    getContext().getString(R.string.passkey_incorrect_message));
                        }
                    }
                })
                .setNegativeButton(getContext().getString(R.string.cancel_btn), null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void goToRoom() {
        Intent roomIntent = new Intent(getContext(), RoomActivity.class);
        roomIntent.putExtra(Constants.ROOM_ID, mRoom.getObjectId());
        getContext().startActivity(roomIntent);
    }
}
