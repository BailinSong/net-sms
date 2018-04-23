package com.blueline.net.sms.common;

/**
 * Exception representing an error in the SMS library.
 *
 * @author Markus Eriksson
 * @version $Id$
 */
public class SmsException extends Exception
{
    private static final long serialVersionUID = -5850718942412939905L;

    /**
     * Creates an SmsException.
     *
     * @param msg The error message
     */
    public SmsException(String msg)
    {
        super(msg);
    }
    
    /**
     * Creates an SmsException.
     *
     * @param msg The error message
     * @param cause Chained exception
     */
    public SmsException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
    
    /**
     * Creates an SmsException.
     *
     * @param cause Chained exception
     */
    public SmsException(Throwable cause)
    {
        super(cause);
    }
}

