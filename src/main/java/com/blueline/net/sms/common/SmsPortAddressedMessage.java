package com.blueline.net.sms.common;

import java.io.Serializable;

/**
 * Base class for all port adressed messages.
 * 
 * It is using a 16 bit port address.
 * 
 * @author Markus
 * @version $Id$
 */
public abstract class SmsPortAddressedMessage extends SmsConcatMessage implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 2836960661981730324L;
	protected SmsPort destPort_;
    protected SmsPort origPort_;
    
    /**
     * Creates a new SmsPortAddressedMessage with the given dest and orig port.
     * 
     * @param destPort
     * @param origPort
     */
    protected SmsPortAddressedMessage(SmsPort destPort, SmsPort origPort)
    {
        setPorts(destPort, origPort);
    }
    
    /**
     * Sets the dest and orig ports.
     * 
     * @param destPort
     * @param origPort
     */
    public void setPorts(SmsPort destPort, SmsPort origPort)
    {
        destPort_ = destPort;
        origPort_ = origPort;
    }
    
    
    public int getDestPort_() {
		return destPort_.getPort();
	}

	public int getOrigPort_() {
		return origPort_.getPort();
	}

	public SmsUdhElement[] getUdhElements()
    {
        return new SmsUdhElement[] { SmsUdhUtil.get16BitApplicationPortUdh(destPort_, origPort_) };
    }
}
