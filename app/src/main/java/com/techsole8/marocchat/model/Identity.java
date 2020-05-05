
package com.techsole8.marocchat.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Identity
{
    private String nickname;
    private final List<String> aliases = new ArrayList<String>();
    private String ident;
    private String realname;

    /**
     * Set the nickname of this identity
     * 
     * @param nickname The nickname to be set
     */
    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

    /**
     * Get the nickname of this identity
     *
     * @return The nickname
     */
    public String getNickname()
    {
        return nickname;
    }

    /**
     * Set a collection of aliases for this identity
     *
     * @param aliases
     */
    public void setAliases(Collection<String> aliases)
    {
        this.aliases.clear();
        this.aliases.addAll(aliases);
    }

    /**
     * Get all aliases for this identity
     *
     * @return
     */
    public List<String> getAliases()
    {
        return Collections.unmodifiableList(aliases);
    }

    /**
     * Set the ident of this identity
     * 
     * @param ident The ident to be set
     */
    public void setIdent(String ident)
    {
        this.ident = ident;
    }

    /**
     * Get the ident of this identity
     * 
     * @return The identity
     */
    public String getIdent()
    {
        return ident;
    }

    /**
     * Set the real name of this identity
     * 
     * @param realname The real name to be set
     */
    public void setRealName(String realname)
    {
        this.realname = realname;
    }

    /**
     * Get the real name of this identity
     * 
     * @return The realname
     */
    public String getRealName()
    {
        return realname;
    }
}
