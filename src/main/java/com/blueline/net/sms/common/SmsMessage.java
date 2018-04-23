package com.blueline.net.sms.common;

public interface SmsMessage
{
    /**
     * Returns the content of this SmsMessage in form of pdus.
     *
     * @return Pdus
     */
    SmsPdu[] getPdus();
}