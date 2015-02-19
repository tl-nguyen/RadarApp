package bg.mentormate.academy.radarapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bg.mentormate.academy.radarapp.R;

/**
 * Created by tl on 19.02.15.
 */
public class FollowingFragment extends Fragment {

    public FollowingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_following, container, false);
        return rootView;
    }
}
