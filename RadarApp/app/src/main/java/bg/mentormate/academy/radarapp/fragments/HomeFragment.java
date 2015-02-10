package bg.mentormate.academy.radarapp.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.activities.MainActivity;
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

        mCurrentUser = (User) User.getCurrentUser();
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof MainActivity) {
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
                // TODO: Implementing joining to room

                AlertHelper.alert(getActivity(), "Hey!", "You've selected '" + mMyRoom.getName() + "'");

                break;
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Room selectedRoom = mRecentRoomsAdapter.getItem(position);

        AlertHelper.alert(getActivity(), "Hey!", "You've selected '" + selectedRoom.getName() + "'");

    }
}
