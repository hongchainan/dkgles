package dkgles;

import java.util.HashMap;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import lost.kapa.R;

import android.opengl.GLException;
import android.util.Log;
import dkgles.primitive.Rectangle;
import dkgles.ui.UIManager;

public enum FontManager
{
	INSTANCE;
	
	public void initialize()
	{
		_fontSetMap = new HashMap<String, FontSet>();
		_fontSetMap.put("MyFont", new FontSet("MyFont", R.raw.myfont));
		
		_messageBuffers = new MessageBuffer[MAX_MESSAGE_BUFFERS];
		_transformation = new Transformation();
	}
	
	public int createMessageBuffer(String fontType, int bufferLen, float fontSize)
	{
		for (int i=0;i<MAX_MESSAGE_BUFFERS;++i)
		{
			if (_messageBuffers[i]==null)
			{
				_messageBuffers[i] = new MessageBuffer(bufferLen, _fontSetMap.get(fontType), fontSize);
				UIManager.INSTANCE.getScene().getRenderQueue().addDrawble(_messageBuffers[i]);
				return i;
			}
		}
		return -1;
	}
	
	public MessageBuffer getMessageBuffer(int i)
	{
		return _messageBuffers[i];
	}
	
	public void print(int bufferId, float x, float y, String message)
	{		
		_transformation.setIdentity();
		_transformation._matrix[12] = x;
		_transformation._matrix[13] = y;
		_transformation._matrix[14] = -1;
		
		_messageBuffers[bufferId].setWorldTransformation(_transformation);
		_messageBuffers[bufferId].setMessage(message);
		_messageBuffers[bufferId].visibility(true);
	}
	
	public void flush()
	{
		for (int i=0;i<_messageBuffers.length;++i)
		{
			if (_messageBuffers[i]!=null)
			{
				_messageBuffers[i].visibility(false);
			}
		}
	}
	
	private Transformation _transformation;
	
	public final static int MAX_MESSAGE_BUFFERS = 10;
	private MessageBuffer[]	_messageBuffers;
	private HashMap<String, FontSet>	_fontSetMap;
}





