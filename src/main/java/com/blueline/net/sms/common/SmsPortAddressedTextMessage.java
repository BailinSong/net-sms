package com.blueline.net.sms.common;

import java.io.Serializable;

/**
 * A port addressed message that delegates the text part to SmsTextMessage.
 *   
 * @author Markus
 * @version $Id$
 */
public class SmsPortAddressedTextMessage extends SmsPortAddressedMessage implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 5122972699556731923L;
	/** The text message part. */ 
    protected final SmsTextMessage smsTextMessage_;
    
    public SmsPortAddressedTextMessage(SmsPort destPort, SmsPort origPort, SmsTextMessage msg)
    {
        super(destPort, origPort);
        smsTextMessage_ = msg;
    }
    /**
     * Creates a new SmsPortAddressedTextMessage with default 6Bit GSM Alphabet.
     *
     * @param destPort
     * @param origPort
     * @param msg
     */
    public SmsPortAddressedTextMessage(SmsPort destPort, SmsPort origPort, String msg)
    {
        super(destPort, origPort);
        smsTextMessage_ = new SmsTextMessage(msg);
    }
    
    /**
     * Creates a new SmsPortAddressedTextMessage with the given alphabet and message class.
     *
     * @param destPort
     * @param origPort
     * @param msg
     * @param alphabet
     * @param messageClass
     */
    public SmsPortAddressedTextMessage(SmsPort destPort, SmsPort origPort, String msg, SmsAlphabet alphabet, SmsMsgClass messageClass)
    {
        super(destPort, origPort);
        smsTextMessage_ = new SmsTextMessage(msg, alphabet, messageClass);
    }
    
    /**
     * Creates a SmsPortAddressedTextMessage with the given dcs.
     * 
     * @param destPort
     * @param origPort
     * @param msg
     * @param dcs
     */
    public SmsPortAddressedTextMessage(SmsPort destPort, SmsPort origPort, String msg, SmsDcs dcs)
    {
        super(destPort, origPort);
        smsTextMessage_ = new SmsTextMessage(msg, dcs);
    }

    public SmsUserData getUserData()
    {
        return smsTextMessage_.getUserData();
    }
    
    /**
     * Returns the text message. 
     */
    public String getText()
    {
        return smsTextMessage_.getText();
    }
    
    /**
     * Sets the text.
     * 
     * @param text
     */
    public void setText(String text)
    {
        smsTextMessage_.setText(text);
    }

    /**
     * Sets the text.
     * 
     * @param text
     */
    public void setText(String text, SmsDcs dcs)
    {
        smsTextMessage_.setText(text, dcs);
    }
    
    /**
     * Returns the dcs.
     */
    public SmsDcs getDcs()
    {
        return smsTextMessage_.getDcs();
    }
}
