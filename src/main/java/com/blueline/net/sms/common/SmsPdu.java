package com.blueline.net.sms.common;

import  java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SmsPdu
{
    protected SmsUdhElement[] udhElements_;
    protected SmsUserData ud_;

    /**
     * Creates an empty SMS pdu object
     */
    public SmsPdu()
    {
        // Empty
    }

    /**
     * Creates an SMS pdu object.
     * 
     * @param udhElements
     *            The UDH elements
     * @param ud
     *            The content
     * @param udLength
     *            The length of the content. Can be in octets or septets
     *            depending on the DCS
     */
    public SmsPdu(SmsUdhElement[] udhElements, byte[] ud, int udLength, SmsDcs dcs)
    {
        setUserDataHeaders(udhElements);
        setUserData(ud, udLength, dcs);
    }

    /**
     * Creates an SMS pdu object.
     * 
     * @param udhElements
     *            The UDH elements
     * @param ud
     *            The content
     */
    public SmsPdu(SmsUdhElement[] udhElements, SmsUserData ud)
    {
        setUserDataHeaders(udhElements);
        setUserData(ud);
    }
    
    /**
     * Sets the UDH field
     * 
     * @param udhElements
     *            The UDH elements
     */
    public void setUserDataHeaders(SmsUdhElement[] udhElements)
    {
        if (udhElements != null)
        {
            udhElements_ = new SmsUdhElement[udhElements.length];

            System.arraycopy(udhElements, 0, udhElements_, 0, udhElements.length);
        }
        else
        {
            udhElements_ = null;
        }
    }

    /**
     * Returns the user data headers
     * 
     * @return A byte array representing the UDH fields or null if there aren't
     *         any UDH
     */
    public byte[] getUserDataHeaders()
    {
        if (udhElements_ == null)
        {
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream(100);

        baos.write((byte) SmsUdhUtil.getTotalSize(udhElements_));

        try
        {
            for (SmsUdhElement anUdhElements_ : udhElements_) {
                anUdhElements_.writeTo(baos);
            }
        }
        catch (IOException ioe)
        {
            // Shouldn't happen.
            throw new RuntimeException("Failed to write to ByteArrayOutputStream");
        }

        return baos.toByteArray();
    }

    /**
     * Sets the user data field of the message.
     * 
     * @param ud
     *            The content
     * @param udLength
     *            The length, can be in septets or octets depending on the DCS
     * @param dcs
     *            The data coding scheme
     */
    public void setUserData(byte[] ud, int udLength, SmsDcs dcs)
    {
        ud_ = new SmsUserData(ud, udLength, dcs);
    }

    /**
     * Sets the user data field of the message.
     * 
     * @param ud
     *            The content
     */
    public void setUserData(SmsUserData ud)
    {
        ud_ = ud;
    }
    
    /**
     * Returns the user data part of the message.
     * 
     * @return UD field
     */
    public SmsUserData getUserData()
    {
        return ud_;
    }
    
    /**
     * Returns the dcs.
     * 
     * @return dcs
     */
    public SmsDcs getDcs()
    {
        return ud_.getDcs();
    }
}