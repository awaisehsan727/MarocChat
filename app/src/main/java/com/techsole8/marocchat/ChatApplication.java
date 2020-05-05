
package com.techsole8.marocchat;

import android.app.Application;


public class ChatApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Yaaic.getInstance().loadServers(this);
    }
}
