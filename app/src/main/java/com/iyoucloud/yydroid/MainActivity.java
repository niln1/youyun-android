package com.iyoucloud.yydroid;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.iyoucloud.yydroid.adapter.NavDrawerListAdapter;
import com.iyoucloud.yydroid.fragment.HomeFragment;
import com.iyoucloud.yydroid.fragment.PickupFragment;
import com.iyoucloud.yydroid.model.NavDrawerItem;
import com.iyoucloud.yydroid.util.OnSocketMessageListener;
import com.iyoucloud.yydroid.util.OnToggleSwitchListener;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.apache.http.Header;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity implements OnToggleSwitchListener, OnSocketMessageListener {


    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private NavDrawerListAdapter adapter;

    private List<NavDrawerItem> dataList;


    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        dataList = new ArrayList<NavDrawerItem>();
        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        // adding nav drawer items to
        dataList.add(new NavDrawerItem(true)); // adding a userProfile to the list
        // Home
        dataList.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Pickup report
        dataList.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Logout
        dataList.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));


        // Recycle the typed array
        navMenuIcons.recycle();

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(this, R.layout.drawer_list_item, dataList, app);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setIcon(null);
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE
                | ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_HOME_AS_UP);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB_MR2) {
            getActionBar().setHomeButtonEnabled(true);
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ){
            public void onDrawerClosed(View view) {
            //    getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
           //     getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0, null);
        }

        HomeFragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
        app.connectSocket(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        client.get(getApplicationContext(), app.URL_HELPER.getUserAccountUrl(), new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                try{
                    JSONObject jsonObject = parseJsonResponse(response);

                    adapter.updateProfile(jsonObject);
                    Toast.makeText(getApplicationContext(),
                            "good good",
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.e(this.getClass().getName(), e.getMessage());
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(getApplicationContext(),
                        throwable.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }



    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    public void logout(View view) {
        final MainActivity self = this;

        client.get(getApplicationContext(), app.URL_HELPER.getLogoutUrl(), new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                Toast.makeText(getApplicationContext(),
                        "logged out",
                        Toast.LENGTH_LONG).show();

                app.clearCookie();
                Intent intent = new Intent(self, LoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(getApplicationContext(),
                        throwable.getMessage(),
                        Toast.LENGTH_LONG).show();
                app.clearCookie();
                Intent intent = new Intent(self, LoginActivity.class);
                startActivity(intent);            }
        });
    }


    /**
     * Display fragment view for selected nav drawer list item
     * */
    private void displayView(int position, View view) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            //user avator
            case 0:
                break;
            //home
            case 1:
                fragment = new HomeFragment();
                break;
            //pickup
            case 2:
                fragment = new PickupFragment();
                break;
            //logout
            case 3:
                logout(view);
                return;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position-1]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            //selected on user profile
            mDrawerLayout.closeDrawer(mDrawerList);

            // error in creating fragment
            Log.i("MainActivity", "user profile clicked");
        }
    }

    @Override
    public void onSwitchToggled(ToggleButton view, JSONObject jsonObject, PickupFragment fragment) {
        app.sendSocketMessage("pickup::teacher::pickup-student", jsonObject, fragment);
    }

    @Override
    public void onSocketMessage(String event, Object... jsonObject) {

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(getApplicationContext(),
//                        "picked",
//                        Toast.LENGTH_LONG).show();
//            }
//        });
    }

    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position, view);
        }
    }

}
