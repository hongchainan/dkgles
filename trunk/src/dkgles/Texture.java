package dkgles;

import javax.microedition.khronos.opengles.GL10;

//import android.util.Log;


/**
 *Basic texture class for OpenGL ES
 *@author doki lin
 */
public class Texture
{
	public Texture(String name, int gid)
	{
		_gid = gid;
		_name = name;
		//Log.v(TAG, "created, id = " + _id[0]);
	}
	
	/**
	 * Bind texture to current render state
	 * Called this function in GLThread
	 */
	public void bind(GL10 gl)
	{
		gl.glBindTexture(GL10.GL_TEXTURE_2D, _gid);
	}
	
	public String name()
	{
		return _name;
	}

	public void release()
	{
		//TODO
	}
	
	/**
	 *@deprecated
	 */
	public void release(GL10 gl)
	{
		int [] id = new int[1];
		id[0] = _gid;
		gl.glDeleteTextures(1, id, 0);
	}
	
	/**
	 * 
	 * @return id for OpenGL
	 */
	public int glID()
	{
		return _gid;
	}
	
	public String toString()
	{
		return _name + " gid: " + _gid;
	}
	
	public static Texture GetDummyTexture()
	{
		return _dummy;
	}
	
	int 	_gid;// GL texture ID
	final String _name;
	
	final static Texture _dummy = new DummyTexture();
	final static String TAG = "Texture";
	final static int INVALID_ID = -1;
}

class DummyTexture extends Texture
{
	DummyTexture()
	{
		super("TEX_DUMMY", Texture.INVALID_ID);
	}
	
	public void bind(GL10 gl)
	{
		// do nothing
	}
	
	public void release(GL10 gl)
	{
		// do nothing
	}
}
