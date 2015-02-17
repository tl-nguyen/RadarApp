package bg.mentormate.academy.radarapp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.activities.MainActivity;

/**
 * Created by tl on 16.02.15.
 */
public class SearchFragment extends Fragment implements ActionBar.OnNavigationListener {

    private static final String ROOMS_TAG = "ROOMS";
    private static final String USERS_TAG = "USERS";

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SearchFragment newInstance(int sectionNumber) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    private SearchRoomsFragment mSearchRoomsFragment;
    private SearchUsersFragment mSearchUsersFragment;

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER),
                    null);
        }

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<>(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        new String[]{
                                getString(R.string.rooms_search_title),
                                getString(R.string.users_search_title)
                        }),
                this);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
                ((ActionBarActivity)getActivity()).getSupportActionBar().setSelectedNavigationItem(
                        savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                ((ActionBarActivity) getActivity()).getSupportActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        // When the given dropdown item is selected, show its contents in the
        // container view.
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        switch (position) {
            case 0:
                mSearchRoomsFragment = (SearchRoomsFragment) fragmentManager.findFragmentByTag(ROOMS_TAG);

                if (mSearchRoomsFragment == null) {
                    mSearchRoomsFragment = SearchRoomsFragment.newInstance(position + 1);
                }

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, mSearchRoomsFragment, ROOMS_TAG)
                        .addToBackStack(ROOMS_TAG)
                        .commit();
                break;
            case 1:
                mSearchUsersFragment = (SearchUsersFragment) fragmentManager.findFragmentByTag(USERS_TAG);

                if (mSearchUsersFragment == null) {
                    mSearchUsersFragment = SearchUsersFragment.newInstance(position + 1);
                }

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, mSearchUsersFragment, USERS_TAG)
                        .addToBackStack(USERS_TAG)
                        .commit();
                break;
        }

        return true;
    }
}
