package bg.mentormate.academy.radarapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.adapters.UserToDeleteAdapter;
import bg.mentormate.academy.radarapp.data.LocalDb;
import bg.mentormate.academy.radarapp.models.Room;
import bg.mentormate.academy.radarapp.models.User;

public class EditRoomActivity extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    private static final String USER_ID = "USER_ID";
    private Menu mMenu;
    private LocalDb mLocalDb;
    private User mUser;
    private Room mRoom;
    private Button applyChangesBtn;
    private EditText editPassword;
    private EditText editRoomName;
    private EditText confirmPassword;
    private ListView listView;
    UserToDeleteAdapter uAdapter;
    List<User> users;
    ArrayList<User> selectedUsers;
    boolean usersDeleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_room);

        selectedUsers = new ArrayList<>();

        mLocalDb = LocalDb.getInstance();
        mUser = mLocalDb.getCurrentUser();
        mRoom = mUser.getRoom();

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        editPassword = (EditText) findViewById(R.id.changePass);
        confirmPassword = (EditText) findViewById(R.id.confirmChangePass);
        editRoomName = (EditText) findViewById(R.id.changeRoomName);
        applyChangesBtn = (Button) findViewById(R.id.applyChangesBtn);

        String currentRoomName = "";
        try {
            currentRoomName = mUser.getRoom().getName();
        }catch (Exception e)
        {
            e.printStackTrace();
            Log.d("EDIT_ROOM:", "Unable to resolve current room name. Please check!");
        }
        editRoomName.setHint(currentRoomName);
        applyChangesBtn.setOnClickListener(this);

        users = mRoom.getUsers();

        listView = (ListView) findViewById(R.id.usersList);
        uAdapter = new UserToDeleteAdapter(this, users);
        listView.setAdapter(uAdapter);

        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        usersDeleted = false;
        selectedUsers.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_room, menu);

        this.mMenu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.action_settings:
                return true;
            case R.id.delete_users:
                deleteSelectedUsers();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void deleteSelectedUsers() {
        if(!selectedUsers.isEmpty()){
            for(int i=0; i < selectedUsers.size(); i++){
                users.remove(selectedUsers.get(i));
            }
            uAdapter.notifyDataSetChanged();
            usersDeleted = true;
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.applyChangesBtn:
                applyChanges();
                break;
        }
    }

    private void applyChanges() {
        boolean roomNameChanged = false;
        boolean passChanged = false;
        boolean passChangeAttempt = false;

        String newRoomName;
        newRoomName = editRoomName.getText().toString();
        String pass;
        pass = editPassword.getText().toString();
        String confirmPass;
        confirmPass = confirmPassword.getText().toString();

        if(!pass.isEmpty() && !confirmPass.isEmpty()) {
            if (pass.equals(confirmPass)) {
                mUser.setPassword(pass.trim());
                passChanged = true;

            } else {
                passChangeAttempt = true;
                Toast.makeText(getApplicationContext(), "The new password should be re-typed correctly, please try again.", Toast.LENGTH_SHORT).show();
            }
        }

        Room myRoom = mRoom;

        if(!newRoomName.isEmpty() && !newRoomName.equals(mUser.getRoom().getName().toString())){
            myRoom.setName(newRoomName.trim());
            mUser.setRoom(myRoom);

            roomNameChanged = true;
        }

        // Delete the users selected from the list if the Delete option is clicked

        try {
            mRoom.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(roomNameChanged || passChanged || usersDeleted){

            if(usersDeleted && !roomNameChanged && !passChanged){
                Toast.makeText(getApplicationContext(), getString(R.string.membersHaveBeenRemoved), Toast.LENGTH_SHORT).show();
                this.finish();
            }

            if(passChangeAttempt) {
                Toast.makeText(getApplicationContext(), getString(R.string.detailsHaveBeenSaved), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), getString(R.string.allDetailsHaveBeenSaved), Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectMember(view, position);

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        goToProfile();
        return true;
    }

    private void selectMember(View view, int position) {
        User selectedMember = users.get(position);

        if (!selectedUsers.contains(selectedMember)) {
            selectedUsers.add(selectedMember);
            view.setBackgroundColor(Color.GRAY);
        } else {
            selectedUsers.remove(selectedMember);
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        setDeleteIconVisibility();
    }

    private void setDeleteIconVisibility() {
        if (selectedUsers.size() > 0) {
            mMenu.findItem(R.id.delete_users).setVisible(true);
        }
        else {
            mMenu.findItem(R.id.delete_users).setVisible(false);
        }
    }

    private void goToProfile() {
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        profileIntent.putExtra(USER_ID, mUser.getObjectId());
        startActivity(profileIntent);
    }
}
