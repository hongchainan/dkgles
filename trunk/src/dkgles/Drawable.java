package dkgles;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLException;
import android.util.Log;

/**
 *Abstract Drawable class
 *@author doki lin
 */
public abstract class Drawable
{
	
	public Drawable(String name)
	{
		_name = name;
		_visible = true;
		_groupID = 0;
	}

	
	public void release()
	{
		//TODO
	}
	
	/**
	 * Set render group id
	 */
	public void groupID(int id)
	{
		_groupID = id;
	}
	
	/**
	 * Get render group id
	 */
	public int groupID()
	{
		return _groupID;
	}

	public void visibility(boolean val)
	{
		_visible = val;
	}
	
	public boolean visible()
	{
		return _visible;
	}
	
	/**
	 * Usually called in GameThread
	 * To avoid race condition between GameThread and GLThread
	 */
	public synchronized void setWorldTransformation(Transformation worldTransformation)
	{
		_worldTransformation = worldTransformation;
	}
	
	/**
	 *Should always called in GLThread
	 */
	public synchronized void render(GL10 gl)
	{
		if (!_visible)
			return;
		
		try
		{
			gl.glMultMatrixf(_worldTransformation._matrix, 0);
			renderImpl(gl);
		}
		catch(GLException e)
		{
			Log.e(TAG, "catch a GLException:" + e.getMessage());
			throw e;
		}
	}
	
	
	public String toString()
	{
		return _name + ", group ID:" + _groupID; 
	}
	
	
	public abstract void renderImpl(GL10 gl);
	
	protected boolean 			_visible;
	protected int				_groupID;
	protected String 			_name;
	protected Transformation 	_worldTransformation;
	static final String TAG = "Drawable";
	
}
