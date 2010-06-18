package dkgles;

import java.util.HashMap;

import lost.kapa.R;
import dkgles.render.OrthoRenderQueue;
import dkgles.ui.UIManager;

public enum FontManager
{
	INSTANCE;
	
	public void initialize()
	{
		fontSetMap_ = new HashMap<String, FontSet>();
		fontSetMap_.put("MyFont", new FontSet("MyFont", R.raw.myfont));
		
		messageBuffers_ = new MessageBuffer[MAX_MESSAGE_BUFFERS];
		transformation_ = new Transformation();
		
		renderQueue_ = new OrthoRenderQueue("RDQ_Font", 8, 100); 
	}
	
	public int createMessageBuffer(String fontType, int bufferLen, float fontSize, int depth)
	{
		for (int i=0;i<MAX_MESSAGE_BUFFERS;++i)
		{
			if (messageBuffers_[i]==null)
			{
				messageBuffers_[i] = new MessageBuffer(bufferLen, fontSetMap_.get(fontType), fontSize, depth);
				renderQueue_.addDrawble(messageBuffers_[i]);
				return i;
			}
		}
		return -1;
	}
	
	public MessageBuffer getMessageBuffer(int i)
	{
		return messageBuffers_[i];
	}
	
	public void print(int bufferId, float x, float y, String message)
	{		
		transformation_.setIdentity();
		transformation_.matrix[12] = x;
		transformation_.matrix[13] = y;
		transformation_.matrix[14] = -1;
		
		messageBuffers_[bufferId].setWorldTransformation(transformation_);
		messageBuffers_[bufferId].setMessage(message);
		messageBuffers_[bufferId].setVisibility(true);
	}
	
	public void flush()
	{
		for (int i=0;i<messageBuffers_.length;++i)
		{
			if (messageBuffers_[i]!=null)
			{
				messageBuffers_[i].setVisibility(false);
			}
		}
	}
	
	private Transformation transformation_;
	
	public final static int MAX_MESSAGE_BUFFERS = 10;
	private MessageBuffer[]	messageBuffers_;
	private HashMap<String, FontSet>	fontSetMap_;
	private OrthoRenderQueue renderQueue_;
}





