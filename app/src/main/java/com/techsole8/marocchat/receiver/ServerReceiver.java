
package com.techsole8.marocchat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.techsole8.marocchat.listener.ServerListener;


public class ServerReceiver extends BroadcastReceiver
{
    private final ServerListener listener;

    /**
     * Create a new server receiver
     * 
     * @param listener
     */
    public ServerReceiver(ServerListener listener)
    {
        this.listener = listener;
    }

    /**
     * On receive broadcast
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        listener.onStatusUpdate();
    }
}
