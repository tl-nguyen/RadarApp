package bg.mentormate.academy.radarapp.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.parse.GetCallback;
import com.parse.ParseException;

import bg.mentormate.academy.radarapp.Constants;
import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.adapters.UserQueryAdapter;
import bg.mentormate.academy.radarapp.models.User;
import bg.mentormate.academy.radarapp.tools.NotificationHelper;
import bg.mentormate.academy.radarapp.tools.QueryHelper;

/**
 * Created by tl on 19.02.15.
 */
public class FollowFragment extends ListFragment implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener{

    private static final String USER_ID = "USER_ID";

    private UserQueryAdapter mUserQueryAdapter;
    private EditText mEtQuery;
    private Button mBtnSearch;
    private SwipeRefreshLayout mSrlRefresh;

    private User mUser;
    private String mState;

    public FollowFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_list, container, false);

        initData();
        initViews(rootView);

        return rootView;
    }

    private void initData() {
        String id = getArguments().getString(USER_ID);
        mState = getArguments().getString(Constants.STATE);

        if (id != null && mState != null) {
            QueryHelper.getUserById(id, new GetCallback<User>() {
                @Override
                public void done(User user, ParseException e) {
                    if (e == null) {
                        mUser = user;

                        mUserQueryAdapter = new UserQueryAdapter(getActivity(), null, mState, mUser);
                        setListAdapter(mUserQueryAdapter);
                    } else {
                        NotificationHelper.alert(getActivity(),
                                getString(R.string.dialog_error_title),
                                e.getMessage());
                    }
                }
            });
        }
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setActivityTitle();
    }

    private void setActivityTitle() {
        ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();

        if (mState.equals(Constants.FOLLOWER)) {
            actionBar.setTitle(getString(R.string.followers_label));
        } else {
            actionBar.setTitle(getString(R.string.following_label));
        }
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

        mUserQueryAdapter = new UserQueryAdapter(getActivity(), query, mState, mUser);

        setListAdapter(mUserQueryAdapter);
    }

    @Override
    public void onRefresh() {
        onSearchClicked();
        mSrlRefresh.setRefreshing(false);
    }
}
