
package com.techsole8.marocchat.model;


public class Query extends Conversation
{
    /**
     * Create a new query
     * 
     * @param name The user's nickname
     */
    public Query(String name)
    {
        super(name);
    }

    /**
     * Get the type of this conversation
     */
    @Override
    public int getType()
    {
        return Conversation.TYPE_QUERY;
    }
}
