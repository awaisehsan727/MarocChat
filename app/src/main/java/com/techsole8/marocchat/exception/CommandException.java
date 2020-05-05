
package com.techsole8.marocchat.exception;


public class CommandException extends Throwable
{
    private static final long serialVersionUID = -8317993941455253288L;

    /**
     * Create a new CommandException object
     */
    public CommandException(String message)
    {
        super(message);
    }
}
