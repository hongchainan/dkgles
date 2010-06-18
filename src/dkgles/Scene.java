package dkgles;

import lost.kapa.ContextHolder;
import lost.kapa.XmlUtil;
import android.util.Log;
import dkgles.render.RenderQueue;


/**
 *@autohor doki
 *
 */
public class Scene
{
	public interface IHandler
	{
		public void update(long deltaTime);
	}
	
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
	 * Build a scene by XML file
	 * @deprecated
	 */
	public void build(int resId, IBuildSceneListener listener)
	{
		//release();
		//XmlUtil.parse(ContextHolder.INSTANCE.get(), new SceneBuilder(this, listener), resId);
	}
	
	public String name()
	{
		return _name;
	}
	
	public void setName(String name)
	{
		_name = name;
	}
	
	/**
	 *Release self
	 */
	public void release()
	{
		visibility(false);
		
		if (_renderQueue!=null)
		{
			_renderQueue.release();
			_renderQueue = null;
		}
		
		if (_camera!=null)
		{
			_camera.release();
			_camera = null;
		}
		
		if (_root!=null)
		{
			_root.release();
			_root = null;
		}
	}

	/**
	 *
	 */
	public void bindCamera(Camera camera)
	{
		_renderQueue.bindCamera(camera);
	}
	
	public void bindHandler(IHandler handler)
	{
		_handler = handler;
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
		if (_renderQueue!=null)
		{
			_renderQueue.visibility(b);
		}
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
	public synchronized void updateSceneGraph()
	{
		_root.updateTransformation(Transformation.identity(), false);
	}
	
	public void updateHandler(long deltaTime)
	{
		if (_handler!=null)
		{
			_handler.update(deltaTime);
		}
	}
	
	
	public String toString()
	{
		return "Scene: " + _name;
	}
	
	
	RenderQueue		_renderQueue;
	Movable			_root;
	Camera 			_camera;
	IHandler		_handler;
	String 			_name;
	final static String TAG = "Scene";
}
