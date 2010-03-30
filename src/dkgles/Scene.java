package dkgles;

import android.util.Log;
import dkgles.render.RenderQueue;

public class Scene
{
	public Scene(String name, RenderQueue renderQueue)
	{
		_name = name;
		_root = new Movable("Root in " + _name, this);
		attachRenderQueue(renderQueue);
	}
	
	public void bindCamera(Camera camera)
	{
		_camera = camera;
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
	
	
	public String toString()
	{
		return "Scene: " + _name;
	}
	
	private RenderQueue		_renderQueue;
	private Movable			_root;
	private Camera 			_camera;
	private final String 	_name;
	private final static String TAG = "Scene";
}