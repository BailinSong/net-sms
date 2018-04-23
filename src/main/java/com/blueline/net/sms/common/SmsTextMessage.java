package com.blueline.net.sms.common;

import com.blueline.net.sms.common.util.CMPPCommonUtil;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * Represents a text message.
 * <p>
 * The text can be sent in unicode (max 70 chars/SMS), 8-bit (max 140 chars/SMS)
 * or GSM encoding (max 160 chars/SMS).
 *
 * @author Markus Eriksson
 * @version $Id$
 */
public class SmsTextMessage extends SmsConcatMessage implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -2655575183111164853L;
	private String text_;
    private SmsDcs dcs_;
    
    /**
     * Creates an SmsTextMessage with the given dcs.
     * 
     * @param msg The message
     * @param dcs The data coding scheme
     */
    public SmsTextMessage(String msg, SmsDcs dcs)
    {
        setText(msg, dcs);
    }
    
    /**
     * Creates an SmsTextMessage with the given alphabet and message class.
     *
     * @param msg The message
     * @param alphabet The alphabet
     * @param messageClass The messageclass
     */
    public SmsTextMessage(String msg, SmsAlphabet alphabet, SmsMsgClass messageClass)
    { 
        this(msg, SmsDcs.getGeneralDataCodingDcs(alphabet, messageClass));
    }

    /**
     * Creates an SmsTextMessage with default 7Bit GSM Alphabet
     *
     * @param msg The message
     */
    public SmsTextMessage(String msg)
    {
    	if(haswidthChar(msg))
    		 setText(msg, SmsDcs.getGeneralDataCodingDcs(SmsAlphabet.UCS2, SmsMsgClass.CLASS_UNKNOWN));
    	else
    		 setText(msg, SmsDcs.getGeneralDataCodingDcs(SmsAlphabet.ASCII, SmsMsgClass.CLASS_UNKNOWN));
    }
    
    /**
     * Returns the text message. 
     */
    public String getText()
    {
        return text_;
    }
    
    /**
     * Sets the text.
     * 
     * @param text
     */
    public void setText(String text)
    {
        if (text == null)
        {
            throw new IllegalArgumentException("Text cannot be null, use an empty string instead.");
        }
        
        text_ = text;
    }

    /**
     * Sets the text.
     * 
     * @param text
     */
    public void setText(String text, SmsDcs dcs)
    {
        // Check input for null
        if (text == null)
        {
        	 text_ = "";
        }
        
        if (dcs == null)
        {
            throw new IllegalArgumentException("dcs cannot be null.");
        }
        
        text_ = text;
        dcs_ = dcs;
    }
    
    /**
     * Returns the dcs.
     */
    public SmsDcs getDcs()
    {
        return dcs_;
    }

    /**
     * Returns the user data.
     * @return user data
     */
    public SmsUserData getUserData()
    {
        SmsUserData ud;
        
        switch (dcs_.getAlphabet())
        {
        case GSM:
        	byte[] bs = SmsPduUtil.getSeptets(text_);
            ud = new SmsUserData(bs, bs.length*8/7, dcs_);
            break;
        case ASCII:
        case LATIN1:
            ud = new SmsUserData(text_.getBytes(CMPPCommonUtil.switchCharset(SmsAlphabet.LATIN1)), text_.length(), dcs_);
            break;

        case UCS2:
            ud = new SmsUserData(text_.getBytes(CMPPCommonUtil.switchCharset(SmsAlphabet.UCS2)), text_.length() * 2, dcs_);
            break;

        default:
            ud = new SmsUserData(text_.getBytes(CMPPCommonUtil.switchCharset(SmsAlphabet.UCS2)), text_.length() * 2, SmsDcs.getGeneralDataCodingDcs(SmsAlphabet.UCS2, SmsMsgClass.CLASS_UNKNOWN));
            break;
        }

        return ud;
    }

    /**
     * Returns null.
     */
    public SmsUdhElement[] getUdhElements()
    {
        return null;
    }
    
	
	private static boolean haswidthChar(String content) {
		if (StringUtils.isEmpty(content))
			return false;

		byte[] bytes = content.getBytes();
		for (int i = 0; i < bytes.length; i++) {
			// 判断最高位是否为1
			if ((bytes[i] & (byte) 0x80) == (byte) 0x80) {
				return true;
			}
		}
		return false;
	}
}
