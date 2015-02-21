package bg.mentormate.academy.radarapp.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import bg.mentormate.academy.radarapp.Constants;
import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.adapters.UserQueryAdapter;
import bg.mentormate.academy.radarapp.models.User;
import bg.mentormate.academy.radarapp.tools.AlertHelper;

/**
 * Created by tl on 19.02.15.
 */
public class FollowingFragment extends ListFragment implements View.OnClickListener {

    private static final String USER_ID = "USER_ID";

    private UserQueryAdapter mUserQueryAdapter;
    private EditText mEtQuery;
    private Button mBtnSearch;

    private User mUser;

    public FollowingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_list, container, false);

        String id = getArguments().getString(USER_ID);

        if (id != null) {
            retrieveUserById(id);

            mEtQuery = (EditText) rootView.findViewById(R.id.etQuery);
            mBtnSearch = (Button) rootView.findViewById(R.id.btnSeach);

            mBtnSearch.setOnClickListener(this);
        }

        return rootView;
    }

    private void retrieveUserById(String userId) {
        ParseQuery query = new ParseQuery(Constants.USER_TABLE);

        query.getInBackground(userId, new GetCallback() {
            @Override
            public void done(ParseObject user, ParseException e) {
                if (e == null) {
                    mUser = (User) user;

                    mUserQueryAdapter = new UserQueryAdapter(getActivity(), null, Constants.FOLLOWING, mUser);

                    setListAdapter(mUserQueryAdapter);
                } else {
                    AlertHelper.alert(getActivity(),
                            getString(R.string.dialog_error_title),
                            e.getMessage());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btnSeach:
                searchForUsers();
                break;
        }
    }

    private void searchForUsers() {
        String query = mEtQuery.getText().toString();

        mUserQueryAdapter = new UserQueryAdapter(getActivity(), query, Constants.FOLLOWING, mUser);

        setListAdapter(mUserQueryAdapter);
    }
}
