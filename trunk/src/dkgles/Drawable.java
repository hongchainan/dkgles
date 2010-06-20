package dkgles;

import javax.microedition.khronos.opengles.GL10;

import lost.kapa.XmlUtil;

import org.xml.sax.Attributes;

import android.opengl.GLException;
import android.util.Log;

/**
 *Abstract Drawable class
 *@author doki lin
 */
public class Drawable implements Cloneable
{
	public Drawable(Mesh mesh, int groupId)
	{
		mesh_ = mesh;
		visibility_ = true;
		groupID_ = groupId;
		worldTransformation_ = new Transformation();
	}
	
	public Drawable(String name)
	{
		name_ = name;
		visibility_ = true;
		groupID_ = 0;
		worldTransformation_ = new Transformation();
		mesh_ = Mesh.getDummy();
	}
	
	public Drawable(Attributes atts)
	{
		mesh_ 		= MeshManager.INSTANCE.getByName(XmlUtil.parseString(atts, "mesh", "Mesh:N/A"));
		groupID_ 	= XmlUtil.parseInt(atts, "group_id", 0);
		visibility_ = XmlUtil.parseBoolean(atts, "visibility", true);
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
		groupID_ = id;
	}
	
	/**
	 * Get render group id
	 */
	public int groupID()
	{
		return groupID_;
	}

	public void setVisibility(boolean val)
	{
		visibility_ = val;
	}
	
	public boolean visible()
	{
		return visibility_;
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
		if (!visibility_)
			return;
		
		try
		{
			gl.glMultMatrixf(worldTransformation_.matrix, 0);
			mesh_.renderImpl(gl);
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
			mesh_ = Mesh.getDummy();
		}
		else
		{
			mesh_ = mesh;
		}
	}
	
	public Mesh mesh()
	{
		return mesh_;
	}
	
	public String name()
	{
		return name_;
	}
	
	
	public String toString()
	{
		return name_ + ", group ID:" + groupID_; 
	}
	
	public Drawable clone()
	{
		Drawable drawable = new Drawable(mesh_, groupID_);
		drawable.visibility_ = this.visibility_;
		drawable.name_ = this.name_;
		return drawable;
	}
	
	protected boolean			visibility_ = true;
	protected int				groupID_ = 0;
	protected String 			name_ = "";
	protected Mesh				mesh_ = null;
	protected Transformation 	worldTransformation_ = new Transformation();
	static final String TAG = "Drawable";
	
}
