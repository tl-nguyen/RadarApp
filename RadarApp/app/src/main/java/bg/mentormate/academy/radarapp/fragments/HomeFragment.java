package bg.mentormate.academy.radarapp.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.activities.MainActivity;
import bg.mentormate.academy.radarapp.adapters.RoomAdapter;
import bg.mentormate.academy.radarapp.data.LocalDb;
import bg.mentormate.academy.radarapp.models.Room;
import bg.mentormate.academy.radarapp.models.User;
import bg.mentormate.academy.radarapp.views.RoomItem;

/**
 * Created by tl on 09.02.15.
 */
public class HomeFragment extends ListFragment {
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

    private RoomAdapter mRecentRoomAdapter;

    private RoomItem mRiMyRoom;
    private TextView mTvNoRoomInfo;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        init(rootView);

        return rootView;
    }

    private void init(View rootView) {
        mLocalDb = LocalDb.getInstance();

        mCurrentUser = mLocalDb.getCurrentUser();

        mMyRoom = mCurrentUser.getRoom();

        mRiMyRoom = (RoomItem) rootView.findViewById(R.id.riMyRoom);
        mTvNoRoomInfo = (TextView) rootView.findViewById(R.id.tvNoRoomInfo);

        if (mMyRoom != null) {
            roomOwnedVisibility();

            mMyRoom.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject room, ParseException e) {
                    mRiMyRoom.setData(mCurrentUser, mMyRoom);
                }
            });
        } else {
            roomNotOwnedVisibility();
        }

        mRecentRoomAdapter = new RoomAdapter(getActivity(), null);

        setListAdapter(mRecentRoomAdapter);
    }

    private void roomNotOwnedVisibility() {
        mRiMyRoom.setVisibility(View.GONE);
        mTvNoRoomInfo.setVisibility(View.VISIBLE);
    }

    private void roomOwnedVisibility() {
        mRiMyRoom.setVisibility(View.VISIBLE);
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
}
