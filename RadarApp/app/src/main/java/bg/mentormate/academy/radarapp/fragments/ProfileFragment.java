package bg.mentormate.academy.radarapp.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.ArrayList;

import bg.mentormate.academy.radarapp.LocalDb;
import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.activities.EditProfileActivity;
import bg.mentormate.academy.radarapp.activities.MainActivity;
import bg.mentormate.academy.radarapp.activities.RoomActivity;
import bg.mentormate.academy.radarapp.Constants;
import bg.mentormate.academy.radarapp.models.Room;
import bg.mentormate.academy.radarapp.models.User;
import bg.mentormate.academy.radarapp.tools.AlertHelper;

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
    private ImageView mIvAvatar;
    private LinearLayout mLlMyRoom;
    private TextView mTvMyRoomName;
    private Button mBtnJoin;
    private Button mBtnCreate;
    private Button mBtnDestroy;
    private Button mBtnEdit;
    private ProgressBar mProgressBar;
    View rootView;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mLocalDb = LocalDb.getInstance();
        mUser = mLocalDb.getCurrentUser();

        init(rootView);

        return rootView;
    }

    private void init(View rootView) {
        mTvFollowersCount = (TextView) rootView.findViewById(R.id.tvFollowersCount);
        mTvFollowingCount = (TextView) rootView.findViewById(R.id.tvFollowingCount);
        mIvAvatar = (ImageView) rootView.findViewById(R.id.ivAvatar);
        mLlMyRoom = (LinearLayout) rootView.findViewById(R.id.llMyRoom);
        mTvMyRoomName = (TextView) rootView.findViewById(R.id.tvMyRoomName);
        mBtnJoin = (Button) rootView.findViewById(R.id.btnJoin);
        mBtnCreate = (Button) rootView.findViewById(R.id.btnCreate);
        mBtnDestroy = (Button) rootView.findViewById(R.id.btnDestroy);
        mBtnEdit = (Button) rootView.findViewById(R.id.btnEditProfile);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        mBtnJoin.setOnClickListener(this);
        mBtnCreate.setOnClickListener(this);
        mBtnDestroy.setOnClickListener(this);
        mBtnEdit.setOnClickListener(this);

        mTvFollowersCount.setText(mUser.getFollowers().size() + "");
        mTvFollowingCount.setText(mUser.getFollowing().size() + "");

        mUser.getAvatar().getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
                if (e == null) {
                    Bitmap imgBitmap = BitmapFactory.decodeByteArray(
                            bytes,
                            0,
                            bytes.length);

                    mIvAvatar.setImageBitmap(imgBitmap);
                } else {
                    showErrorAlert(e);
                }
            }
        });

        mMyRoom = mUser.getRoom();

        setRoomManagementElements();
    }

    private void showErrorAlert(ParseException e) {
        AlertHelper.alert(getActivity(), getString(R.string.dialog_error_title), e.getMessage());
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
                public void done(ParseObject parseObject, ParseException e) {
                    if (e == null) {
                        mTvMyRoomName.setText(mMyRoom.getName());
                    } else {
                        showErrorAlert(e);
                    }
                }
            });
        }
    }

    private void roomCreatedVisibility() {
        mLlMyRoom.setVisibility(View.VISIBLE);
        mBtnCreate.setVisibility(View.GONE);
        mBtnDestroy.setVisibility(View.VISIBLE);
    }

    private void roomNotCreatedVisibility() {
        mLlMyRoom.setVisibility(View.INVISIBLE);
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
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnJoin:
                onJoinClicked();
                break;
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

    private void onJoinClicked() {
        if (!mMyRoom.getUsers().contains(mUser)) {
            checkForPassKey(mMyRoom);
        } else {
            goToRoom(mMyRoom);
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
                            room.getUsers().add(mUser);
                            room.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        goToRoom(mMyRoom);
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

    @Override
    public void onResume() {
        super.onResume();
        init(rootView);
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
