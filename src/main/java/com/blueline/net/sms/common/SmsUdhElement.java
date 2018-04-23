package com.blueline.net.sms.common;

import java.io.IOException;
import java.io.OutputStream;

public final class SmsUdhElement
{
    protected final SmsUdhIei udhIei_;
    protected final byte[] udhIeiData_;

    /**
     * Creates an SmsUdhElement
     *
     * @param udhIei
     * @param udhIeiData
     */
    public SmsUdhElement(SmsUdhIei udhIei, byte[] udhIeiData)
    {
        udhIei_ = udhIei;
        udhIeiData_ = udhIeiData;
    }

    /**
     * Returns the total length of this UDH element.
     * <p>
     * The length is including the UDH data length and the UDH "header" (2 bytes)
     * @return the length
     */
    public int getTotalSize()
    {
        return udhIeiData_.length + 2;
    }

    /**
     * Returns the length of the UDH iei data
     * <p>
     * The length returned is only the length of the data
     * @return Length of data
     */
    public int getUdhIeiDataLength()
    {
        return udhIeiData_.length;
    }

    /**
     * Returns the Udh Iei Data excluding the UDH "header"
     * @return Data
     */
    public byte[] getUdhIeiData()
    {
        return udhIeiData_;
    }

    /**
     * Return the UDH element including the UDH "header" (two bytes)
     *
     * @return Data
     */
    public byte[] getData()
    {
        byte[] allData = new byte[udhIeiData_.length + 2];

        allData[0] = (byte) (udhIei_.getValue() & 0xff);
        allData[1] = (byte) (udhIeiData_.length & 0xff);
        System.arraycopy(udhIeiData_, 0, allData, 2, udhIeiData_.length);

        return allData;
    }

    /**
     * Writes the UDH element including UDH "header" to the given stream
     *
     * @param os Stream to write to
     * @throws IOException
     */
    public void writeTo(OutputStream os)
        throws IOException
    {
        os.write(udhIei_.getValue());
        os.write(udhIeiData_.length);
        os.write(udhIeiData_);
    }
}