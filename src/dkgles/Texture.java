package dkgles;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;
import dkgles.manager.TextureManager;


public class Texture implements Resource
{
	public Texture(int id)
	{
		_id = new int[1];
		_id[0] = id;
		
		Log.v(TAG, "created, id = " + _id[0]);
	}
	
	public void acquire()
	{
		
	}
	
	
	public void bind(GL10 gl)
	{
		gl.glBindTexture(GL10.GL_TEXTURE_2D, _id[0]);
	}
	
	/**
	 * Release openGL texture resouce
	 */
	public void release()
	{
		if (_id[0]==INVALID_ID)
			return;
		
		TextureManager.instance().releaseTexture(_id);
		_id[0] = INVALID_ID;
	}
	
	private final static String TAG = "Texture";
	private final static int INVALID_ID = -1;
	private int[] _id;
}
