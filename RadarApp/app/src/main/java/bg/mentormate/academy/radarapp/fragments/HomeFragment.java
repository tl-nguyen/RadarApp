package bg.mentormate.academy.radarapp.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import bg.mentormate.academy.radarapp.Constants;
import bg.mentormate.academy.radarapp.LocalDb;
import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.activities.MainActivity;
import bg.mentormate.academy.radarapp.activities.RoomActivity;
import bg.mentormate.academy.radarapp.adapters.RecentRoomsAdapter;
import bg.mentormate.academy.radarapp.models.Room;
import bg.mentormate.academy.radarapp.models.User;
import bg.mentormate.academy.radarapp.tools.AlertHelper;

/**
 * Created by tl on 09.02.15.
 */
public class HomeFragment extends ListFragment implements View.OnClickListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static HomeFragment newInstance(int sectionNumber) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    private LocalDb mLocalDb;

    private User mCurrentUser;
    private Room mMyRoom;

    private RecentRoomsAdapter mRecentRoomsAdapter;

    private TextView mTvMyRoomName;
    private TextView mTvNoRoomInfo;
    private LinearLayout mLlMyRoom;
    private Button mBtnJoin;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        mLocalDb = LocalDb.getInstance();

        if (mLocalDb.getCurrentUser() == null) {
            mLocalDb.setCurrentUser((User) User.getCurrentUser());
        }

        mCurrentUser = mLocalDb.getCurrentUser();

        mMyRoom = mCurrentUser.getRoom();

        mTvMyRoomName = (TextView) rootView.findViewById(R.id.tvMyRoomName);
        mTvNoRoomInfo = (TextView) rootView.findViewById(R.id.tvNoRoomInfo);
        mLlMyRoom = (LinearLayout) rootView.findViewById(R.id.llMyRoom);
        mBtnJoin = (Button) rootView.findViewById(R.id.btnJoin);

        if (mMyRoom != null) {
            roomOwnedVisibility();

            mMyRoom.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    mTvMyRoomName.setText(mMyRoom.getName());
                }
            });

            mBtnJoin.setOnClickListener(this);
        } else {
            roomNotOwnedVisibility();
        }

        mRecentRoomsAdapter = new RecentRoomsAdapter(getActivity());

        setListAdapter(mRecentRoomsAdapter);

        return rootView;
    }

    private void roomNotOwnedVisibility() {
        mLlMyRoom.setVisibility(View.GONE);
        mTvNoRoomInfo.setVisibility(View.VISIBLE);
    }

    private void roomOwnedVisibility() {
        mLlMyRoom.setVisibility(View.VISIBLE);
        mTvNoRoomInfo.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER),
                    null);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btnJoin:
                onJoinClicked(mMyRoom);
                break;
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Room selectedRoom = mRecentRoomsAdapter.getItem(position);

        onJoinClicked(selectedRoom);
    }

    private void onJoinClicked(Room room) {
        if (!room.getUsers().contains(mCurrentUser)) {
            checkForPassKey(room);
        } else {
            goToRoom(room);
        }
    }

    private void checkForPassKey(final Room room) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dvCreateRoom = inflater.inflate(R.layout.dialog_passkey_check, null);

        final EditText etPassKey = (EditText) dvCreateRoom.findViewById(R.id.etPassKey);

        builder.setView(dvCreateRoom)
                .setTitle(getString(R.string.check_keypass_title))
                .setPositiveButton(getString(R.string.got_it_btn), new DialogInterface.OnClickListener() {
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
                                        AlertHelper.alert(getActivity(),
                                                getString(R.string.dialog_error_title),
                                                e.getMessage());
                                    }
                                }
                            });
                        } else {
                            AlertHelper.alert(getActivity(),
                                    getString(R.string.dialog_error_title),
                                    getString(R.string.passkey_incorrect_message));
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel_btn), null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void goToRoom(Room room) {
        Intent roomIntent = new Intent(getActivity(), RoomActivity.class);
        roomIntent.putExtra(Constants.ROOM_ID, room.getObjectId());
        startActivity(roomIntent);
    }
}
