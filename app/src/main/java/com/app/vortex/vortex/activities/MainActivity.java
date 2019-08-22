package com.app.vortex.vortex.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.app.vortex.vortex.R;
import com.app.vortex.vortex.app.Vortex;
import com.app.vortex.vortex.fragments.ControlFragment;
import com.app.vortex.vortex.fragments.HomeFragment;
import com.app.vortex.vortex.fragments.SettingFragment;
import com.app.vortex.vortex.services.TankInfoService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String CURRENT_POSITION = "position";
    private static final String FRAGMENT_TAG = "visible_fragment";

    private NavigationView navigationView;
    private int currentPos = 0;
    private DrawerLayout drawer;
    private int device_id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if(!sharedPreferences.contains(Vortex.USER_ID)){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        device_id = sharedPreferences.getInt(Vortex.DEVICE_ID, -1);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
                if(fragment instanceof HomeFragment) {
                    currentPos = 0;
                }else if(fragment instanceof ControlFragment) {
                    currentPos = 1;
                }else if(fragment instanceof SettingFragment) {
                    currentPos = 2;
                }

                navigationView.setCheckedItem(navigationView.getMenu().getItem(currentPos).getItemId());
                setTitle(navigationView.getMenu().getItem(currentPos).getTitle());
            }
        });

        if (savedInstanceState != null) {
            currentPos = savedInstanceState.getInt(CURRENT_POSITION);
            onNavigationItemSelected(navigationView.getMenu().getItem(currentPos));
        } else {
            currentPos = 0;

            //first fragment should not be added to backstack
            Fragment fragment = new HomeFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment, FRAGMENT_TAG);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();

            //set titles and select menu item
            navigationView.setCheckedItem(navigationView.getMenu().getItem(currentPos).getItemId());
            setTitle(navigationView.getMenu().getItem(currentPos).getTitle());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            TankInfoService service = new TankInfoService();
            service.ManualRefresh(device_id);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean showRefresh = (currentPos != 2);//no need to show refresh on settings page
        menu.findItem(R.id.action_refresh).setVisible(showRefresh);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                currentPos = 0;
                break;
            case R.id.nav_control:
                currentPos = 1;
                break;
            case R.id.nav_settings:
                currentPos = 2;
                break;
            case R.id.nav_logout:
                logoutUser();
                break;
        }

        setTitle(item.getTitle());
        setPage(currentPos);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void logoutUser(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());;
        sharedPreferences.edit().remove(Vortex.USER_ID).remove(Vortex.DEVICE_ID).apply();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    void setPage(int pos) {
        Fragment fragment;

        switch (pos) {
            case 1:
                fragment = new ControlFragment();
                break;

            case 2:
                fragment = new SettingFragment();
                break;

            default:
                fragment = new HomeFragment();
                break;
        }

        currentPos = pos;

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment, FRAGMENT_TAG);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_POSITION, currentPos);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment f = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (f != null && f instanceof SettingFragment) {
            f.onActivityResult(requestCode, resultCode, data);
        }
    }

}
