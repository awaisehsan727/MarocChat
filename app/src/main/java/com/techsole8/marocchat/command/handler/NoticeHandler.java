
package com.techsole8.marocchat.command.handler;

import android.content.Context;
import android.content.Intent;

import com.techsole8.marocchat.exception.CommandException;

import com.techsole8.marocchat.R;
import com.techsole8.marocchat.command.BaseHandler;
import com.techsole8.marocchat.irc.IRCService;
import com.techsole8.marocchat.model.Broadcast;
import com.techsole8.marocchat.model.Conversation;
import com.techsole8.marocchat.model.Message;
import com.techsole8.marocchat.model.Server;


public class NoticeHandler extends BaseHandler
{

    @Override
    public void execute(String[] params, Server server, Conversation conversation, IRCService service) throws CommandException
    {
        if (params.length > 2) {
            String text = BaseHandler.mergeParams(params);

            Message message = new Message(">" + params[1] + "< " + text);
            message.setIcon(R.drawable.info);
            conversation.addMessage(message);

            Intent intent = Broadcast.createConversationIntent(
                Broadcast.CONVERSATION_MESSAGE,
                server.getId(),
                conversation.getName()
            );
            service.sendBroadcast(intent);

            service.getConnection(server.getId()).sendNotice(params[1], text);
        } else {
            throw new CommandException(service.getString(R.string.invalid_number_of_params));
        }
    }

     @Override
    public String getUsage()
    {
        return "/notice <nickname> <message>";
    }

      @Override
    public String getDescription(Context context)
    {
        return context.getString(R.string.command_desc_notice);
    }
}
