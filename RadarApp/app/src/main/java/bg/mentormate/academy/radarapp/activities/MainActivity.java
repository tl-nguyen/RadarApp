package bg.mentormate.academy.radarapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import bg.mentormate.academy.radarapp.data.LocalDb;
import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.fragments.HomeFragment;
import bg.mentormate.academy.radarapp.fragments.NavigationDrawerFragment;
import bg.mentormate.academy.radarapp.fragments.ProfileFragment;
import bg.mentormate.academy.radarapp.fragments.SearchFragment;
import bg.mentormate.academy.radarapp.models.User;
import bg.mentormate.academy.radarapp.services.LocationTrackingService;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    /**
     * Fragments Tags
     */
    private final static String HOME_TAG = "HOME";
    private final static String MY_PROFILE_TAG = "MY_PROFILE";
    private final static String SEARCH_TAG = "SEARCH";

    /**
     * Fragments relating to the drawer menu items
     */
    private HomeFragment mHomeFragment;
    private ProfileFragment mMyProfileFragment;
    private SearchFragment mSearchFragment;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private LocalDb mLocalDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

    }

    private void init() {
        mLocalDb = LocalDb.getInstance();

        User currentUser = mLocalDb.getCurrentUser();

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
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
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
                            .addToBackStack(HOME_TAG)
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
                case 2:
                    mSearchFragment = (SearchFragment) fragmentManager.findFragmentByTag(SEARCH_TAG);

                    if (mSearchFragment == null) {
                        mSearchFragment = SearchFragment.newInstance(position + 1);
                    }

                    fragmentManager.beginTransaction()
                            .replace(R.id.container, mSearchFragment, SEARCH_TAG)
                            .addToBackStack(SEARCH_TAG)
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
                mTitle = title;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    public int getIcon(int position) {
        switch (position) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
        }

        return 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_main, menu);

            MenuItem trackingStatus = menu.findItem(R.id.actionTrackingStatus);

            if (!mLocalDb.isTrackingOn()) {
                trackingStatus.setIcon(getResources().getDrawable(R.drawable.ic_marker_off));
            } else {
                trackingStatus.setIcon(getResources().getDrawable(R.drawable.ic_marker_on));
            }

            if (mTitle != null) {
                restoreActionBar();
            }
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.actionLogout:
                logout();
                return true;
            case R.id.actionTrackingStatus:
                toggleTrackingStatus(item);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void toggleTrackingStatus(MenuItem item) {
        if (!mLocalDb.isTrackingOn()) {
            startPositionTracking();
            item.setIcon(getResources().getDrawable(R.drawable.ic_marker_on));
        } else {
            stopPositionTracking();
            item.setIcon(getResources().getDrawable(R.drawable.ic_marker_off));
        }
    }

    private void startPositionTracking() {
        Intent trackingIntent = new Intent(this, LocationTrackingService.class);
        trackingIntent.setAction(LocationTrackingService.ACTION_START_MONITORING);
        startService(trackingIntent);

        mLocalDb.setTrackingOn(true);
    }

    private void stopPositionTracking() {
        Intent trackingIntent = new Intent(this, LocationTrackingService.class);
        trackingIntent.setAction(LocationTrackingService.ACTION_STOP_MONITORING);
        startService(trackingIntent);

        mLocalDb.setTrackingOn(false);
    }

    private void logout() {
        stopPositionTracking();
        User.logOut();
        LocalDb.getInstance().setCurrentUser(null);
        goToLoginScreen();
    }

    private void goToLoginScreen() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }
}
