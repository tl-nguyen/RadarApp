package bg.mentormate.academy.radarapp.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.adapters.EditRoomUserAdapter;
import bg.mentormate.academy.radarapp.data.LocalDb;
import bg.mentormate.academy.radarapp.models.Room;
import bg.mentormate.academy.radarapp.models.User;

public class EditRoomActivity extends ActionBarActivity
        implements View.OnClickListener, AdapterView.OnItemLongClickListener {

    private Menu mMenu;
    private Button mBtnApplyChanges;
    private EditText mEtChangePassword;
    private EditText mEtChangeRoomName;
    private GridView mGvUsers;

    private LocalDb mLocalDb;
    private User mCurrentUser;
    private Room mRoom;
    private EditRoomUserAdapter mEditRoomUserAdapter;
    private List<User> mUsers;
    private ArrayList<User> mSelectedUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_room);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }

    private void init() {
        mLocalDb = LocalDb.getInstance();
        mCurrentUser = mLocalDb.getCurrentUser();
        mRoom = mCurrentUser.getRoom();

        mSelectedUsers = new ArrayList<>();

        mEtChangePassword = (EditText) findViewById(R.id.etChangePass);
        mEtChangeRoomName = (EditText) findViewById(R.id.etChangeRoomName);
        mBtnApplyChanges = (Button) findViewById(R.id.btnApplyChanges);
        mGvUsers = (GridView) findViewById(R.id.usersList);

        mRoom.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                mEtChangeRoomName.setHint(mRoom.getName());
            }
        });

        mUsers = mRoom.getUsers();

        mEditRoomUserAdapter = new EditRoomUserAdapter(this, mUsers);
        mGvUsers.setAdapter(mEditRoomUserAdapter);

        mBtnApplyChanges.setOnClickListener(this);
        mGvUsers.setOnItemLongClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_room, menu);

        this.mMenu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.delete_users:
                deleteSelectedUsers();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteSelectedUsers() {
        if(!mSelectedUsers.isEmpty()){
            for(int i=0; i < mSelectedUsers.size(); i++){
                mEditRoomUserAdapter.getView(i, null, null).setBackgroundColor(Color.TRANSPARENT);
                mUsers.remove(mSelectedUsers.get(i));
            }
            mEditRoomUserAdapter = new EditRoomUserAdapter(this, mUsers);
            mGvUsers.setAdapter(mEditRoomUserAdapter);
            mSelectedUsers.clear();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnApplyChanges:
                applyChanges();
                break;
        }
    }

    private void applyChanges() {
        String newRoomName = mEtChangeRoomName.getText().toString();
        String passkey = mEtChangePassword.getText().toString();
        boolean changesApplied = false;

        if (!newRoomName.trim().isEmpty() && !newRoomName.equals(mRoom.getName())) {
            mRoom.setName(newRoomName.trim());
            changesApplied = true;
        }

        if(!passkey.trim().isEmpty()) {
            mRoom.setPassKey(passkey.trim());
            changesApplied = true;
        }

        if(changesApplied) {
            mRoom.saveInBackground();
            finish();
        }

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        User selectedMember = mUsers.get(position);

        if (selectedMember.equals(mCurrentUser)) {
            Toast.makeText(this,
                    getString(R.string.cant_select_yourself_warning),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!mSelectedUsers.contains(selectedMember)) {
            mSelectedUsers.add(selectedMember);
            view.setBackgroundColor(Color.GRAY);
        } else {
            mSelectedUsers.remove(selectedMember);
            view.setBackgroundColor(Color.WHITE);
        }

        setDeleteIconVisibility();
        return true;
    }

    private void setDeleteIconVisibility() {
        if (mSelectedUsers.size() > 0) {
            mMenu.findItem(R.id.delete_users).setVisible(true);
        } else {
            mMenu.findItem(R.id.delete_users).setVisible(false);
        }
    }
}
