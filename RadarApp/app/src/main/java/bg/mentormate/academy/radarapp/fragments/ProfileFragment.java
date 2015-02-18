package bg.mentormate.academy.radarapp.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.ArrayList;

import bg.mentormate.academy.radarapp.Constants;
import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.activities.EditProfileActivity;
import bg.mentormate.academy.radarapp.activities.MainActivity;
import bg.mentormate.academy.radarapp.data.LocalDb;
import bg.mentormate.academy.radarapp.models.Room;
import bg.mentormate.academy.radarapp.models.User;
import bg.mentormate.academy.radarapp.tools.AlertHelper;
import bg.mentormate.academy.radarapp.views.RoomItem;

/**
 * Created by tl on 08.02.15.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ProfileFragment newInstance(int sectionNumber) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    private LocalDb mLocalDb;
    private User mUser;
    private Room mMyRoom;

    private TextView mTvFollowersCount;
    private TextView mTvFollowingCount;
    private ParseImageView mPivBigAvatar;

    private RoomItem mRiMyRoom;

    private Button mBtnCreate;
    private Button mBtnDestroy;
    private Button mBtnEdit;
    private ProgressBar mProgressBar;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mLocalDb = LocalDb.getInstance();
        mUser = mLocalDb.getCurrentUser();

        init(rootView);

        return rootView;
    }

    private void init(View rootView) {
        mTvFollowersCount = (TextView) rootView.findViewById(R.id.tvFollowersCount);
        mTvFollowingCount = (TextView) rootView.findViewById(R.id.tvFollowingCount);
        mPivBigAvatar = (ParseImageView) rootView.findViewById(R.id.pivBigAvatar);
        mRiMyRoom = (RoomItem) rootView.findViewById(R.id.riMyRoom);
        mBtnCreate = (Button) rootView.findViewById(R.id.btnCreate);
        mBtnDestroy = (Button) rootView.findViewById(R.id.btnDestroy);
        mBtnEdit = (Button) rootView.findViewById(R.id.btnEditProfile);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        mBtnCreate.setOnClickListener(this);
        mBtnDestroy.setOnClickListener(this);
        mBtnEdit.setOnClickListener(this);

        mTvFollowersCount.setText(mUser.getFollowers().size() + "");
        mTvFollowingCount.setText(mUser.getFollowing().size() + "");

        mMyRoom = mUser.getRoom();

        setRoomManagementElements();
    }

    private void showErrorAlert(ParseException e) {
        AlertHelper.alert(getActivity(),
                getString(R.string.dialog_error_title),
                e.getMessage());
    }

    private void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void setRoomManagementElements() {
        if (mMyRoom == null) {
            roomNotCreatedVisibility();
        } else {
            roomCreatedVisibility();

            mMyRoom.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject room, ParseException e) {
                    if (e == null) {
                        mRiMyRoom.setData((Room) room, mUser);
                    }
                }
            });
        }
    }

    private void roomCreatedVisibility() {
        mRiMyRoom.setVisibility(View.VISIBLE);
        mBtnCreate.setVisibility(View.GONE);
        mBtnDestroy.setVisibility(View.VISIBLE);
    }

    private void roomNotCreatedVisibility() {
        mRiMyRoom.setVisibility(View.INVISIBLE);
        mBtnCreate.setVisibility(View.VISIBLE);
        mBtnDestroy.setVisibility(View.GONE);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER),
                    mUser.getUsername());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPivBigAvatar.setParseFile(mUser.getAvatar());
        mPivBigAvatar.loadInBackground();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnCreate:
                onCreateClicked();
                break;
            case R.id.btnDestroy:
                onDestroyClicked();
                break;
            case R.id.btnEditProfile:
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
        }
    }

    private void onCreateClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dvCreateRoom = inflater.inflate(R.layout.dialog_create_room, null);

        final EditText etName = (EditText) dvCreateRoom.findViewById(R.id.etRoomName);
        final EditText etPassKey = (EditText) dvCreateRoom.findViewById(R.id.etPassKey);

        builder.setView(dvCreateRoom)
                .setTitle(getString(R.string.create_room_title))
                .setPositiveButton(getString(R.string.make_room_btn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String name = etName.getText().toString().trim();
                        String passKey = etPassKey.getText().toString().trim();

                        createRoom(name, passKey);
                    }
                })
                .setNegativeButton(getString(R.string.cancel_btn), null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createRoom(String name, String passKey) {
        if (name.isEmpty() || passKey.isEmpty()) {
            AlertHelper.alert(getActivity(), getString(R.string.dialog_error_title),
                    getString(R.string.create_room_invalid_inputs_message));
        } else {
            if (mMyRoom == null && !mUser.containsKey(Constants.USER_COL_ROOM)) {
                showProgressBar();

                mMyRoom = new Room();
                mMyRoom.setName(name);
                mMyRoom.setPassKey(passKey);
                mMyRoom.setCreatedBy(mUser);
                mMyRoom.setUsers(new ArrayList<User>());
                mMyRoom.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        hideProgressBar();
                        if (e == null) {
                            // managing UI elements
                            setRoomManagementElements();

                            mUser.setRoom(mMyRoom);
                            mUser.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e != null) {
                                        showErrorAlert(e);
                                    }
                                }
                            });
                        } else {
                            showErrorAlert(e);
                        }
                    }
                });
            }
        }
    }

    private void onDestroyClicked() {
        if (mUser.containsKey(Constants.USER_COL_ROOM)) {

            showProgressBar();
            mUser.remove(Constants.USER_COL_ROOM);
            mMyRoom.deleteInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        mMyRoom = null;
                        mUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                hideProgressBar();
                                if (e == null) {
                                    setRoomManagementElements();
                                } else {
                                    showErrorAlert(e);
                                }
                            }
                        });
                    } else {
                        hideProgressBar();
                        showErrorAlert(e);
                    }
                }
            });
        }
    }
}
