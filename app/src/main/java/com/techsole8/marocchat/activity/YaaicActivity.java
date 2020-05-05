
package com.techsole8.marocchat.activity;

import android.support.v7.widget.Toolbar;

import com.techsole8.marocchat.irc.IRCBinder;
import com.techsole8.marocchat.model.Server;


public interface YaaicActivity {
    IRCBinder getBinder();

    Toolbar getToolbar();

    void setToolbarTitle(String title);

    void onServerSelected(Server server);
}
