package bg.mentormate.academy.radarapp.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.SaveCallback;

import bg.mentormate.academy.radarapp.Constants;
import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.activities.RoomActivity;
import bg.mentormate.academy.radarapp.data.LocalDb;
import bg.mentormate.academy.radarapp.models.Room;
import bg.mentormate.academy.radarapp.models.User;
import bg.mentormate.academy.radarapp.tools.AlertHelper;

/**
 * Created by tl on 10.02.15.
 */
public class RoomAdapter extends ParseQueryAdapter<Room> {

    private static final int LIMIT = 50;

    private Activity mHostActivity;
    private User mCurrentUser;

    public RoomAdapter(final Activity hostActivity, final String searchQuery) {
        super(hostActivity, new QueryFactory<Room>() {

            @Override
            public ParseQuery<Room> create() {
                ParseQuery query = new ParseQuery(Constants.ROOM_TABLE);
                query.orderByDescending(Constants.PARSE_COL_CREATED_AT);

                if (searchQuery != null) {
                    query.whereContains(Constants.ROOM_COL_NAME, searchQuery);
                }

                query.setLimit(LIMIT);

                return query;
            }
        });

        mHostActivity = hostActivity;
        mCurrentUser = LocalDb.getInstance().getCurrentUser();
    }

    @Override
    public View getItemView(Room room, View v, ViewGroup parent) {
        final Room selectedRoom = room;

        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.item_list_room, parent, false);
        }

        final TextView tvRoomName = (TextView) v.findViewById(R.id.tvRoomName);
        final TextView tvUsername = (TextView) v.findViewById(R.id.tvUsername);
        final ParseImageView pivAvatar = (ParseImageView) v.findViewById(R.id.pivAvatar);
        Button btnJoin = (Button) v.findViewById(R.id.btnJoin);

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onJoinClicked(selectedRoom);
            }
        });

        super.getItemView(selectedRoom, v, parent);

        if (room != null) {
            final User owner = room.getCreatedBy();

            owner.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    tvRoomName.setText(selectedRoom.getName());
                    tvUsername.setText(owner.getUsername());
                    pivAvatar.setParseFile(owner.getAvatar());
                    pivAvatar.loadInBackground();
                }
            });
        }

        return v;
    }

    private void onJoinClicked(Room room) {
        if (!room.getUsers().contains(mCurrentUser)) {
            checkForPassKey(room);
        } else {
            goToRoom(room);
        }
    }

    private void checkForPassKey(final Room room) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mHostActivity);

        LayoutInflater inflater = mHostActivity.getLayoutInflater();
        final View dvCreateRoom = inflater.inflate(R.layout.dialog_passkey_check, null);

        final EditText etPassKey = (EditText) dvCreateRoom.findViewById(R.id.etPassKey);

        builder.setView(dvCreateRoom)
                .setTitle(mHostActivity.getString(R.string.check_keypass_title))
                .setPositiveButton(mHostActivity.getString(R.string.got_it_btn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String passKey = etPassKey.getText().toString().trim();

                        if (passKey.equals(room.getPassKey())) {
                            // Go to Room if the passkey is correct
                            room.getUsers().add(mCurrentUser);
                            room.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        goToRoom(room);
                                    } else {
                                        AlertHelper.alert(mHostActivity,
                                                mHostActivity.getString(R.string.dialog_error_title),
                                                e.getMessage());
                                    }
                                }
                            });
                        } else {
                            AlertHelper.alert(mHostActivity,
                                    mHostActivity.getString(R.string.dialog_error_title),
                                    mHostActivity.getString(R.string.passkey_incorrect_message));
                        }
                    }
                })
                .setNegativeButton(mHostActivity.getString(R.string.cancel_btn), null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void goToRoom(Room room) {
        Intent roomIntent = new Intent(mHostActivity, RoomActivity.class);
        roomIntent.putExtra(Constants.ROOM_ID, room.getObjectId());
        mHostActivity.startActivity(roomIntent);
    }
}
