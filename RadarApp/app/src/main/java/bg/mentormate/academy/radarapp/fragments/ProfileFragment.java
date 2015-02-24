package bg.mentormate.academy.radarapp.fragments;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;

import bg.mentormate.academy.radarapp.Constants;
import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.activities.EditProfileActivity;
import bg.mentormate.academy.radarapp.activities.EditRoomActivity;
import bg.mentormate.academy.radarapp.activities.FollowActivity;
import bg.mentormate.academy.radarapp.activities.MainActivity;
import bg.mentormate.academy.radarapp.activities.ProfileActivity;
import bg.mentormate.academy.radarapp.data.LocalDb;
import bg.mentormate.academy.radarapp.models.Follow;
import bg.mentormate.academy.radarapp.models.Room;
import bg.mentormate.academy.radarapp.models.User;
import bg.mentormate.academy.radarapp.tools.AlertHelper;
import bg.mentormate.academy.radarapp.views.FollowButton;
import bg.mentormate.academy.radarapp.views.RoomItem;

/**
 * Created by tl on 08.02.15.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final String USER_ID = "USER_ID";

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
    private boolean mIsCurrentUser;
    private Follow mFollow;

    private LinearLayout mLlFollowing;
    private LinearLayout mLlFollower;
    private TextView mTvFollowingCount;
    private TextView mTvFollowerCount;
    private ParseImageView mPivBigAvatar;

    private RoomItem mRiMyRoom;

    private Button mBtnCreate;
    private Button mBtnEditRoom;
    private Button mBtnDestroy;
    private Button mBtnEditProfile;
    private FollowButton mFbFollow;
    private ProgressBar mProgressBar;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        initData();
        initViews(rootView);

        return rootView;
    }

    private void initData() {
        String id = getArguments().getString(USER_ID);

        mLocalDb = LocalDb.getInstance();
        mUser = mLocalDb.getCurrentUser();
        mFollow = mUser.getFollow();

        mIsCurrentUser = id == null || mUser.getObjectId().equals(id);

        if (!mIsCurrentUser) {
            ParseQuery query = new ParseQuery(Constants.USER_TABLE);

            try {
                mUser = (User) query.get(id);
                mFollow = mUser.getFollow();

            } catch (ParseException e) {
                AlertHelper.alert(getActivity(),
                        getString(R.string.dialog_error_title),
                        e.getMessage());
            }
        }

        mMyRoom = mUser.getRoom();

        if (mMyRoom != null) {
            mMyRoom.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject room, ParseException e) {
                    if (e == null) {
                        mRiMyRoom.setData(LocalDb.getInstance().getCurrentUser(), mMyRoom);
                    }
                }
            });
        }
    }

    private void initViews(View rootView) {
        mLlFollowing = (LinearLayout) rootView.findViewById(R.id.llFollowing);
        mLlFollower = (LinearLayout) rootView.findViewById(R.id.llFollower);
        mTvFollowingCount = (TextView) rootView.findViewById(R.id.tvFollowingCount);
        mTvFollowerCount = (TextView) rootView.findViewById(R.id.tvFollowerCount);
        mPivBigAvatar = (ParseImageView) rootView.findViewById(R.id.pivBigAvatar);
        mRiMyRoom = (RoomItem) rootView.findViewById(R.id.riMyRoom);
        mBtnCreate = (Button) rootView.findViewById(R.id.btnCreate);
        mBtnEditRoom = (Button) rootView.findViewById(R.id.btnEditRoom);
        mBtnDestroy = (Button) rootView.findViewById(R.id.btnDestroy);
        mBtnEditProfile = (Button) rootView.findViewById(R.id.btnEditProfile);
        mFbFollow = (FollowButton) rootView.findViewById(R.id.fbFollow);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        mLlFollowing.setOnClickListener(this);
        mLlFollower.setOnClickListener(this);
        mBtnEditProfile.setOnClickListener(this);
        mBtnCreate.setOnClickListener(this);
        mBtnEditRoom.setOnClickListener(this);
        mBtnDestroy.setOnClickListener(this);

        mFollow.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                mTvFollowingCount.setText(mFollow.getFollowings().size() + "");
                mTvFollowerCount.setText(mFollow.getFollowers().size() + "");
            }
        });

        mFbFollow.setData(LocalDb.getInstance().getCurrentUser(), mUser);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER),
                    mUser.getUsername());
        } else if (getActivity() instanceof ProfileActivity) {
            ((ProfileActivity) getActivity()).getSupportActionBar().setTitle(mUser.getUsername());
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onResume() {
        super.onResume();

        if (mUser.getAvatar() != null) {
            mPivBigAvatar.setParseFile(mUser.getAvatar());
            mPivBigAvatar.loadInBackground();
        } else {
            mPivBigAvatar.setBackground(getResources().getDrawable(R.drawable.ic_avatar));
        }

        setRoomUIElementsVisibility();
    }

    private void setRoomUIElementsVisibility() {
        if (!mIsCurrentUser) {
            mBtnEditProfile.setVisibility(View.GONE);
            mFbFollow.setVisibility(View.VISIBLE);
            mBtnEditRoom.setVisibility(View.GONE);
            mBtnDestroy.setVisibility(View.GONE);
            mBtnCreate.setVisibility(View.GONE);
        }

        if (mMyRoom != null) {
            roomCreatedVisibility();
        } else {
            roomNotCreatedVisibility();
        }
    }

    private void roomCreatedVisibility() {
        mRiMyRoom.setVisibility(View.VISIBLE);

        if (mIsCurrentUser) {
            mBtnCreate.setVisibility(View.GONE);
            mBtnEditRoom.setVisibility(View.VISIBLE);
            mBtnDestroy.setVisibility(View.VISIBLE);
        }
    }

    private void roomNotCreatedVisibility() {
        mRiMyRoom.setVisibility(View.INVISIBLE);

        if (mIsCurrentUser) {
            mBtnCreate.setVisibility(View.VISIBLE);
            mBtnDestroy.setVisibility(View.GONE);
            mBtnEditRoom.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.llFollowing:
                goToFollowingPage(Constants.FOLLOWING);
                break;
            case R.id.llFollower:
                goToFollowingPage(Constants.FOLLOWER);
                break;
            case R.id.btnEditProfile:
                goToEditProfile();
                break;
            case R.id.btnCreate:
                onCreateClicked();
                break;
            case R.id.btnEditRoom:
                goToEditRoom();
                break;
            case R.id.btnDestroy:
                onDestroyClicked();
                break;
        }
    }

    private void goToFollowingPage(String state) {
        Intent followingIntent = new Intent(getActivity(), FollowActivity.class);
        followingIntent.putExtra(Constants.STATE, state);
        followingIntent.putExtra(USER_ID, mUser.getObjectId());
        startActivity(followingIntent);
    }

    private void goToEditRoom() {
        Intent editRoomIntent = new Intent(getActivity(), EditRoomActivity.class);
        startActivity(editRoomIntent);
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
                            setRoomUIElementsVisibility();

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
                                    setRoomUIElementsVisibility();
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

    private void goToEditProfile() {
        Intent editIntent = new Intent(getActivity(), EditProfileActivity.class);
        startActivity(editIntent);
    }
}
