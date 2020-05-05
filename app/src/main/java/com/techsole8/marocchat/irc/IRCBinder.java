
package com.techsole8.marocchat.irc;

import android.os.Binder;

import com.techsole8.marocchat.model.Server;


public class IRCBinder extends Binder
{
    private final IRCService service;

    /**
     * Create a new binder for given service
     *
     * @param service
     */
    public IRCBinder(IRCService service)
    {
        super();

        this.service = service;
    }

    /**
     * Connect to given server
     * 
     * @param server
     */
    public void connect(final Server server)
    {
        service.connect(server);
    }

    /**
     * Get service associated with this binder
     *
     * @return
     */
    public IRCService getService()
    {
        return service;
    }
}
