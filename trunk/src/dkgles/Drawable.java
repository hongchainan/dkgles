package dkgles;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLException;
import android.util.Log;

public abstract class Drawable
{
	
	public Drawable(String name)
	{
		_name = name;
		_visible = true;
		_groupID = 0;
	}
	
	
	public void show()
	{
		_visible = true;
	}
	
	public void hide()
	{
		_visible = false;
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
	
	public boolean visible()
	{
		return _visible;
	}
	
	
	public synchronized void setWorldTransformation(Transformation worldTransformation)
	{
		_worldTransformation = worldTransformation;
	}
	
	
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
	private static final String TAG = "Drawable";
	
}