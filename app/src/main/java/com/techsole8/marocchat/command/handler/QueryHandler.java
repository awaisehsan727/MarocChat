
package com.techsole8.marocchat.command.handler;

import android.content.Context;
import android.content.Intent;

import com.techsole8.marocchat.exception.CommandException;

import com.techsole8.marocchat.R;
import com.techsole8.marocchat.command.BaseHandler;
import com.techsole8.marocchat.irc.IRCService;
import com.techsole8.marocchat.model.Broadcast;
import com.techsole8.marocchat.model.Conversation;
import com.techsole8.marocchat.model.Query;
import com.techsole8.marocchat.model.Server;


public class QueryHandler extends BaseHandler
{
    /**
     * Execute /query
     */
    @Override
    public void execute(String[] params, Server server, Conversation conversation, IRCService service) throws CommandException
    {
        if (params.length == 2) {
            // Simple validation
            if (params[1].startsWith("#")) {
                throw new CommandException(service.getString(R.string.query_to_channel));
            }

            Conversation query = server.getConversation(params[1]);

            if (query != null) {
                throw new CommandException(service.getString(R.string.query_exists));
            }

            query = new Query(params[1]);
            query.setHistorySize(service.getSettings().getHistorySize());
            server.addConversation(query);

            Intent intent = Broadcast.createConversationIntent(
                Broadcast.CONVERSATION_NEW,
                server.getId(),
                query.getName()
            );
            service.sendBroadcast(intent);
        } else {
            throw new CommandException(service.getString(R.string.invalid_number_of_params));
        }
    }

    /**
     * Usage of /query
     */
    @Override
    public String getUsage()
    {
        return "/query <nickname>";
    }

    /**
     * Description of /query
     */
    @Override
    public String getDescription(Context context)
    {
        return context.getString(R.string.command_desc_query);
    }
}
