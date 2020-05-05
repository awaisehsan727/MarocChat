package com.techsole8.marocchat.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.techsole8.marocchat.R;
import com.techsole8.marocchat.activity.GetGuest;
import com.techsole8.marocchat.activity.MainActivity;
import com.techsole8.marocchat.activity.YaaicActivity;


public class SettingsFragment extends PreferenceFragmentCompat {
    public static final String TRANSACTION_TAG = "fragment_settings";

    private YaaicActivity activity;
    private static final int REQUEST_CODE_USERS = 2;

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//
//        if (!(context instanceof YaaicActivity))
//            throw new IllegalArgumentException("Activity has to implement YaaicActivity interface");
//
//        this.activity = (YaaicActivity) context;
//    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume() {
        super.onResume();

     //    activity.setToolbarTitle(getString(R.string.navigation_settings));

    }
}