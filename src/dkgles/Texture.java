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
	
	public int glID()
	{
		return _gid;
	}
	
	public String toString()
	{
		return _name + " gid: " + _gid;
	}
	
	private int 	_gid;// GL texture ID
	private final String _name;
	
	private final static String TAG = "Texture";
	private final static int INVALID_ID = -1;
	
}
