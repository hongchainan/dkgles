package dkgles;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLException;
import android.util.Log;

/**
 *Abstract Drawable class
 *@author doki lin
 */
public class Drawable
{
	
	public Drawable(String name)
	{
		_name = name;
		_visible = true;
		_groupID = 0;
		_worldTransformation = Transformation.identity();
		_mesh = Mesh.getDummy();
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
			_mesh.renderImpl(gl);
			//renderImpl(gl);
		}
		catch(GLException e)
		{
			Log.e(TAG, "catch a GLException:" + e.getMessage());
			throw e;
		}
	}
	
	public void setMesh(Mesh mesh)
	{
		if (mesh==null)
		{
			_mesh = Mesh.getDummy();
		}
		else
		{
			_mesh = mesh;
		}
	}
	
	public String name()
	{
		return _name;
	}
	
	
	public String toString()
	{
		return _name + ", group ID:" + _groupID; 
	}
	
	boolean			_visible;
	int				_groupID;
	String 			_name;
	Mesh			_mesh;
	Transformation 	_worldTransformation;
	static final String TAG = "Drawable";
	
}
