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
	public Drawable(Mesh mesh, int groupId)
	{
		_mesh = mesh;
		visible_ = true;
		_groupID = groupId;
		worldTransformation_ = new Transformation();
	}
	
	public Drawable(String name)
	{
		_name = name;
		visible_ = true;
		_groupID = 0;
		worldTransformation_ = new Transformation();
		_mesh = Mesh.getDummy();
	}

	
	public void release()
	{
		worldTransformation_.release();
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

	public void setVisibility(boolean val)
	{
		visible_ = val;
	}
	
	public boolean visible()
	{
		return visible_;
	}
	
	/**
	 * Usually called in GameThread
	 * To avoid race condition between GameThread and GLThread
	 */
	public synchronized void setWorldTransformation(Transformation worldTransformation)
	{
		worldTransformation_.copy(worldTransformation);
	}
	
	/**
	 *Should always called in GLThread
	 */
	public synchronized void render(GL10 gl)
	{
		if (!visible_)
			return;
		
		try
		{
			gl.glMultMatrixf(worldTransformation_.matrix, 0);
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
	
	boolean			visible_;
	int				_groupID;
	String 			_name;
	Mesh			_mesh;
	Transformation 	worldTransformation_;
	static final String TAG = "Drawable";
	
}
