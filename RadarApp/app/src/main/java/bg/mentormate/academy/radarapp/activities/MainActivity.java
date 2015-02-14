package bg.mentormate.academy.radarapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import bg.mentormate.academy.radarapp.LocalDb;
import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.fragments.HomeFragment;
import bg.mentormate.academy.radarapp.fragments.NavigationDrawerFragment;
import bg.mentormate.academy.radarapp.fragments.ProfileFragment;
import bg.mentormate.academy.radarapp.models.User;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    /**
     * Fragments Tags
     */
    private final static String HOME_TAG = "HOME";
    private final static String MY_PROFILE_TAG = "MY_PROFILE";

    /**
     * Fragments relating to the drawer menu items
     */
    private HomeFragment mHomeFragment;
    private ProfileFragment mMyProfileFragment;


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocalDb.getInstance().setCurrentUser((User) User.getCurrentUser());

        User currentUser = LocalDb.getInstance().getCurrentUser();

        if (currentUser == null) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);

            finish();
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        User currentUser = (User) User.getCurrentUser();

        if (currentUser != null) {
            // update the main content by replacing fragments
            FragmentManager fragmentManager = getSupportFragmentManager();

            switch (position) {
                case 0:
                    mHomeFragment = (HomeFragment) fragmentManager.findFragmentByTag(HOME_TAG);

                    if (mHomeFragment == null) {
                        mHomeFragment = HomeFragment.newInstance(position + 1);
                    }

                    fragmentManager.beginTransaction()
                            .replace(R.id.container, mHomeFragment, HOME_TAG)
                            .commit();

                    break;
                case 1:
                    mMyProfileFragment = (ProfileFragment) fragmentManager.findFragmentByTag(MY_PROFILE_TAG);

                    if (mMyProfileFragment == null) {
                        mMyProfileFragment = ProfileFragment.newInstance(position + 1);
                    }

                    fragmentManager.beginTransaction()
                            .replace(R.id.container, mMyProfileFragment, MY_PROFILE_TAG)
                            .addToBackStack(MY_PROFILE_TAG)
                            .commit();
                    break;
            }
        }
    }

    public void onSectionAttached(int number, String title) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = title;
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        User.logOut();
        goToLoginScreen();
    }

    private void goToLoginScreen() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }
}
