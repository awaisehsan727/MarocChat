
package com.techsole8.marocchat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.techsole8.marocchat.irc.IRCService;
import com.techsole8.marocchat.model.Broadcast;
import com.techsole8.marocchat.model.Server;


public class ReconnectReceiver extends BroadcastReceiver
{
    private IRCService service;
    private Server server;

    /**
     * Create a new reconnect receiver
     * 
     * @param server The server to reconnect to
     */
    public ReconnectReceiver(IRCService service, Server server)
    {
        this.service = service;
        this.server = server;
    }

    /**
     * On receive broadcast
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (!intent.getAction().equals(Broadcast.SERVER_RECONNECT + server.getId())) {
            return;
        }
        service.connect(server);
    }
}
