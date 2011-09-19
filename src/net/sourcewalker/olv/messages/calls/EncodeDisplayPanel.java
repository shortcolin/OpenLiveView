package net.sourcewalker.olv.messages.calls;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import net.sourcewalker.olv.messages.LiveViewCall;
import net.sourcewalker.olv.messages.MessageConstants;

public class EncodeDisplayPanel extends LiveViewCall {
	private String topText;
	private String bottomText;
	private boolean alertUser;
	private byte[] image;
	
	public EncodeDisplayPanel(String topText, String bottomText, boolean alertUser, byte[] image) {
		super(MessageConstants.MSG_DISPLAYPANEL);
		this.topText = topText;
		this.bottomText = bottomText;
		this.alertUser = alertUser;
		this.image = image;
	}

	@Override
	protected byte[] getPayload() {
		byte id = 80;
		if (alertUser == false)
			id |= 1;
		
        try {
            byte[] topArray = topText.getBytes("iso-8859-1");
            byte[] bottomArray = bottomText.getBytes("iso-8859-1");
            
            // payload = struct.pack(">BHHHBB", 0, 0, 0, 0, id, 0)     # final 0 is for plaintext vs bitmapimage (1) strings
            // payload += struct.pack(">H", len(topText)) + topText
            // payload += struct.pack(">H", 0)                         # unused string
            // payload += struct.pack(">H", len(bottomText)) + bottomText
            // payload += bitmap
            
            int size = 15 + topArray.length + bottomArray.length + image.length;
            ByteBuffer buffer = ByteBuffer.allocate(size);
            buffer.put((byte) 0);
            buffer.putShort((short) 0);
            buffer.putShort((short) 0);
            buffer.putShort((short) 0);
            buffer.put(id);
            buffer.put((byte) 0); // 0 is for plaintext vs bitmapimage (1) strings
            buffer.putShort((short) topArray.length); 
            buffer.put(topArray);
            buffer.putShort((short) 0); // Unused string
            buffer.putShort((short) bottomArray.length); 
            buffer.put(bottomArray);
            
            buffer.put(image);
            return buffer.array();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding not found: " + e.getMessage(),
                    e);
        }
	}

}
