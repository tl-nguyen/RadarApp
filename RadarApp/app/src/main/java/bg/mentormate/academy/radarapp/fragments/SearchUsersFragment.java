package bg.mentormate.academy.radarapp.fragments;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.adapters.UserAdapter;

/**
 * Created by tl on 16.02.15.
 */
public class SearchUsersFragment extends ListFragment implements View.OnClickListener {

    private static final String QUERY = "query";

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_TAB_NUMBER = "tab_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SearchUsersFragment newInstance(int sectionNumber) {
        SearchUsersFragment fragment = new SearchUsersFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TAB_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    private UserAdapter mUserAdapter;
    private EditText mEtQuery;
    private Button mBtnSearch;

    public SearchUsersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_list, container, false);

        mUserAdapter = new UserAdapter(getActivity(), null);
        setListAdapter(mUserAdapter);

        mEtQuery = (EditText) rootView.findViewById(R.id.etQuery);
        mBtnSearch = (Button) rootView.findViewById(R.id.btnSeach);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(QUERY)) {
                String query = savedInstanceState.getString(QUERY);
                mEtQuery.setText(query);
            }
        }

        mBtnSearch.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(QUERY, mEtQuery.getText().toString());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btnSeach:
                searchForRooms();
                break;
        }
    }

    private void searchForRooms() {
        String query = mEtQuery.getText().toString();

        mUserAdapter = new UserAdapter(getActivity(), query);

        setListAdapter(mUserAdapter);
    }
}
