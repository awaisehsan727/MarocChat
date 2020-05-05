/*
Yaaic - Yet Another Android IRC Client

Copyright 2009-2013 Sebastian Kaspari

This file is part of Yaaic.

Yaaic is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Yaaic is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Yaaic.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.techsole8.marocchat.command.handler;

import android.content.Context;

import com.techsole8.marocchat.exception.CommandException;

import com.techsole8.marocchat.R;
import com.techsole8.marocchat.command.BaseHandler;
import com.techsole8.marocchat.irc.IRCService;
import com.techsole8.marocchat.model.Conversation;
import com.techsole8.marocchat.model.Server;

/**
 * Command: /away [<reason>]
 * 
 * Sets you away
 * 
 * @author Sebastian Kaspari <sebastian@yaaic.org>
 */
public class AwayHandler extends BaseHandler
{
    /**
     * Execute /away
     */
    @Override
    public void execute(String[] params, Server server, Conversation conversation, IRCService service) throws CommandException
    {
        service.getConnection(server.getId()).sendRawLineViaQueue("AWAY " + BaseHandler.mergeParams(params));
    }

    /**
     * Get description of /away
     */
    @Override
    public String getDescription(Context context)
    {
        return context.getString(R.string.command_desc_away);
    }

    /**
     * Get usage of /away
     */
    @Override
    public String getUsage()
    {
        return "/away [<reason>]";
    }
}
