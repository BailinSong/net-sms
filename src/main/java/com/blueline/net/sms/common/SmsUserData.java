package com.blueline.net.sms.common;

/**
 * 
 * @author Markus Eriksson
 * @version $Id$
 */
public class SmsUserData
{
    /** The actual user data. */
    protected final byte[] data_;
    
    /** Length of data, in octets or septets depending on the dcs. */
    protected final int length_;
    
    /** Data Coding Scheme for this user data. */
    protected final SmsDcs dcs_;
    
    public SmsUserData(byte[] userData, int userDataLength, SmsDcs dataCodingScheme)
    {
        data_ = userData;
        length_ = userDataLength;
        dcs_ = dataCodingScheme;
    }
    
    public SmsUserData(byte[] userData)
    {
        data_ = userData;
        length_ = userData.length;
        dcs_ = SmsDcs.getGeneralDataCodingDcs(SmsAlphabet.LATIN1, SmsMsgClass.CLASS_UNKNOWN);
    }
    
    public byte[] getData()
    {
        return data_;
    }
    
    /**
     * Returns the length of the user data field.
     * 
     * This can be in characters or byte depending on the message (DCS). If
     * message is 7 bit coded the length is given in septets. If 8bit or UCS2
     * the length is in octets.
     * 
     * @return The length
     */
    public int getLength()
    {
        return length_;
    }
    
    /**
     * Returns the data coding scheme.
     * 
     * @return The dcs
     */
    public SmsDcs getDcs()
    {
        return dcs_;
    }
}
