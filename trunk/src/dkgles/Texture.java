package dkgles;

import javax.microedition.khronos.opengles.GL10;

//import android.util.Log;



public class Texture
{
	public Texture(String name, int gid)
	{
		_gid = gid;
		_name = name;
		//Log.v(TAG, "created, id = " + _id[0]);
	}
	
	public void bind(GL10 gl)
	{
		gl.glBindTexture(GL10.GL_TEXTURE_2D, _gid);
	}
	
	public void release(GL10 gl)
	{
		int [] id = new int[1];
		id[0] = _gid;
		gl.glDeleteTextures(1, id, 0);
	}
	
	public int glID()
	{
		return _gid;
	}
	
	public String toString()
	{
		return _name + " gid: " + _gid;
	}
	
	public static DummyTexture GetDummyTexture()
	{
		return _dummy;
	}
	
	private int 	_gid;// GL texture ID
	private final String _name;
	
	private final static Texture _dummy = new DummyTexture();
	private final static String TAG = "Texture";
	private final static int INVALID_ID = -1;
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
