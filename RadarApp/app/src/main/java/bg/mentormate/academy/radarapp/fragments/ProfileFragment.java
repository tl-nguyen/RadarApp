package bg.mentormate.academy.radarapp.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.activities.MainActivity;
import bg.mentormate.academy.radarapp.models.User;

/**
 * Created by tl on 08.02.15.
 */
public class ProfileFragment extends Fragment {
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

    private User mUser;

    private TextView mTvFollowersCount;
    private TextView mTvFollowingCount;
    private ImageView mIvAvatar;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mUser = (User) ParseUser.getCurrentUser();

        init(rootView);

        return rootView;
    }

    private void init(View rootView) {
        mTvFollowersCount = (TextView) rootView.findViewById(R.id.tvFollowersCount);
        mTvFollowingCount = (TextView) rootView.findViewById(R.id.tvFollowingCount);
        mIvAvatar = (ImageView) rootView.findViewById(R.id.imAvatar);

        mTvFollowersCount.setText(mUser.getFollowers().size() + "");
        mTvFollowingCount.setText(mUser.getFollowing().size() + "");

        mUser.getAvatar().getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
                Bitmap imgBitmap = BitmapFactory.decodeByteArray(
                        bytes,
                        0,
                        bytes.length);

                mIvAvatar.setImageBitmap(imgBitmap);
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER),
                mUser.getUsername());
    }
}
