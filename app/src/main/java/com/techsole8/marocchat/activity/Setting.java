package com.techsole8.marocchat.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import com.techsole8.marocchat.R;
import com.techsole8.marocchat.fragment.SettingsFragment;
import com.techsole8.marocchat.irc.IRCBinder;
import com.techsole8.marocchat.receiver.ServerReceiver;

public class Setting extends AppCompatActivity {

    private ActionBarDrawerToggle toggle;
    private android.support.v7.widget.Toolbar toolbar;
    public DrawerLayout drawer;
    private LinearLayout serverContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ImageView back=findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initializeDrawer();
        switchToFragment(new SettingsFragment(), SettingsFragment.TRANSACTION_TAG);
    }
    public void initializeDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0);

        drawer.setDrawerListener(toggle);

        serverContainer = (LinearLayout) findViewById(R.id.server_container);
    }
    private void switchToFragment(Fragment fragment, String tag) {
        drawer.closeDrawers();

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.findFragmentByTag(tag) != null) {
            // We are already showing this fragment
            return;
        }

        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                .replace(R.id.container, fragment, tag)
                .commit();
    }



}
