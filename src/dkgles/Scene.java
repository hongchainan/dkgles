package dkgles;

import java.util.ArrayList;
import java.util.Iterator;

import android.util.Log;
import dkgles.render.RenderQueue;

public class Scene
{
	public Scene(String name, RenderQueue renderQueue)
	{
		_name = name;
		_root = new Movable("Root in " + _name, this);
		attachRenderQueue(renderQueue);
		
		//synchronized(Scene.class)
		//{
		//	_sceneList.add(this);
		//}
	}
	
	public void release()
	{
		visibility(false);
		_renderQueue.release();
		
		//synchronized(Scene.class)
		//{		
//			_sceneList.remove(this);
	//	}
	}

	public void bindCamera(Camera camera)
	{
		_renderQueue.bindCamera(camera);
	}
	
	public void visibility(boolean b)
	{
		_renderQueue.visibility(b);
	}
	
	public void attachRenderQueue(RenderQueue renderQueue)
	{
		Log.v(TAG, "attach render queue:" + renderQueue);
		_renderQueue = renderQueue;
	}
	
	public RenderQueue getRenderQueue()
	{
		return _renderQueue;
	}
	
	public Movable root()
	{
		return _root;
	}
	
	public synchronized void update()
	{
		_root.updateTransformation(Transformation.identity(), false);
	}
	
	
	public String toString()
	{
		return "Scene: " + _name;
	}
	/*
	public synchronized static void updateAll()
	{
		try
		{   
			Iterator<Scene> iterator = _sceneList.iterator();   
	        while (iterator.hasNext())
	        {   
	        	Scene s = (Scene)iterator.next();
	        	s.update();
	        }  
	    }
		catch (Exception e)
	    {   
			// java.lang.IllegalStateException   
	        e.printStackTrace();   
	    }   
	}*/
	
	//static ArrayList<Scene>	_sceneList = new ArrayList<Scene>();
	
	private RenderQueue		_renderQueue;
	private Movable			_root;
	private Camera 			_camera;
	private final String 	_name;
	private final static String TAG = "Scene";
}