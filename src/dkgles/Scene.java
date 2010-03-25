package dkgles;

public class Scene
{
	public Scene(String name)
	{
		_name = name;
		_root = new Movable("Root in " + _name, this);
	}
	
	
	public void attachRenderQueue(RenderQueue renderQueue)
	{
		Log.v(TAG, "attach render queue:" + renderQueue);
		_renderQueue = renderQueue;
	}
	
	public void getRenderQueue()
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
	private final String 	_name;
	private final static String TAG = "Scene";
}