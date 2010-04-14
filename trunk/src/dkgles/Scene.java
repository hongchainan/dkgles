package dkgles;

import java.util.ArrayList;
import java.util.Iterator;

import android.util.Log;
import dkgles.render.RenderQueue;


/**
 *@autohor doki
 *
 */
public class Scene
{
	/**
	 *@param name A human readable string for debugging.
	 *@param renderQueue used to place drawables in this scene.
	 */
	public Scene(String name, RenderQueue renderQueue)
	{
		_name = name;
		_root = new Movable("Root in " + _name, null, this);
		attachRenderQueue(renderQueue);
	}
	
	/**
	 *Release self
	 */
	public void release()
	{
		visibility(false);
		_renderQueue.release();
		_root.release();

		_renderQueue = null;
		_root = null;
	}

	/**
	 *
	 */
	public void bindCamera(Camera camera)
	{
		_renderQueue.bindCamera(camera);
	}

	
	/**
	 *Called by GC
	 */
	protected void finalize() throws Throwable 
	{
		try
		{
			release();
		}
		finally
		{
			super.finalize();
		}
	}
	
	/**
	 *Set visibility of this scene
	 *@param val true for visible and otherwise.
	 */
	public void visibility(boolean b)
	{
		_renderQueue.visibility(b);
	}
	
	/**
	 *@param renderQueue used to place drawables in this scene
	 */
	public void attachRenderQueue(RenderQueue renderQueue)
	{
		Log.v(TAG, "attach render queue:" + renderQueue);
		_renderQueue = renderQueue;
	}
	
	/**
	 *@return current render queue
	 */
	public RenderQueue getRenderQueue()
	{
		return _renderQueue;
	}
	
	/**
	 *@return root
	 */
	public Movable root()
	{
		return _root;
	}
	
	/**
	 *Update scene graph
	 */
	public synchronized void update()
	{
		_root.updateTransformation(Transformation.identity(), false);
	}
	
	
	public String toString()
	{
		return "Scene: " + _name;
	}
	
	
	RenderQueue		_renderQueue;
	Movable			_root;
	Camera 			_camera;
	final String 	_name;
	final static String TAG = "Scene";
}
