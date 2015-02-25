package bg.mentormate.academy.radarapp.fragments;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import bg.mentormate.academy.radarapp.Constants;
import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.adapters.UserQueryAdapter;

/**
 * Created by tl on 16.02.15.
 */
public class SearchUsersFragment extends ListFragment implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {

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

    private UserQueryAdapter mUserQueryAdapter;
    private EditText mEtQuery;
    private Button mBtnSearch;
    private SwipeRefreshLayout mSrlRefresh;

    public SearchUsersFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_list, container, false);

        initData(savedInstanceState);
        initViews(rootView);

        return rootView;
    }

    private void initData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String query = savedInstanceState.getString(QUERY);
            mEtQuery.setText(query);
            mUserQueryAdapter = new UserQueryAdapter(getActivity(), query, Constants.SEARCH, null);
        } else {
            mUserQueryAdapter = new UserQueryAdapter(getActivity(), null, Constants.SEARCH, null);
        }

        setListAdapter(mUserQueryAdapter);
    }

    private void initViews(View rootView) {
        mEtQuery = (EditText) rootView.findViewById(R.id.etQuery);
        mBtnSearch = (Button) rootView.findViewById(R.id.btnSeach);
        mSrlRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.srlRefresh);

        mSrlRefresh.setColorSchemeColors(
                getResources().getColor(R.color.br_dark_background));

        mBtnSearch.setOnClickListener(this);
        mSrlRefresh.setOnRefreshListener(this);
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
                onSearchClicked();
                break;
        }
    }

    private void onSearchClicked() {
        String query = mEtQuery.getText().toString();

        mUserQueryAdapter = new UserQueryAdapter(getActivity(), query, Constants.SEARCH, null);

        setListAdapter(mUserQueryAdapter);
    }

    @Override
    public void onRefresh() {
        onSearchClicked();
        mSrlRefresh.setRefreshing(false);
    }
}
