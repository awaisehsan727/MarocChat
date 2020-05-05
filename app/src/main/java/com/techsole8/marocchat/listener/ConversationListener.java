
package com.techsole8.marocchat.listener;

public interface ConversationListener
{
    /**
     * On new conversation message for given target
     *
     * @param target
     */
    public void onConversationMessage(String target);

    /**
     * On new conversation created (for given target)
     *
     * @param target
     */
    public void onNewConversation(String target);

    /**
     * On conversation removed (for given target)
     *
     * @param target
     */
    public void onRemoveConversation(String target);

    /**
     * On topic changed (for given target)
     *
     * @param target
     */
    public void onTopicChanged(String target);
}
