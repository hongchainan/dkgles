package dkgles;

import dkgles.render.RenderQueue;

public class SceneManager
{
	/**
	 * Create a scene
	 * @param name
	 * @param renderQueue
	 * @return scene ID
	 */
	public int create(String name, RenderQueue renderQueue)
	{
		Scene s = new Scene(name, renderQueue);
		return register(s);
	}
	
	public int register(Scene scene)
	{
		for (int i=0;i<MAX_SCENE;i++)
		{
			if (_scenes[i]==null)
			{
				_scenes[i] = scene;
				return i;
			}
		}
		
		return -1;
		
	}
	
	public Scene get(int id)
	{
		return _scenes[id];
	}
	
	public void release(int id)
	{
		if (_scenes[id]==null)
			return;
		
		_scenes[id].release();
		_scenes[id] = null;
	}
	
	public void update()
	{
		for (int i=0;i<MAX_SCENE;i++)
		{
			if (_scenes[i]!=null)
			{
				_scenes[i].update();
			}
		}
	}
	
	public void releaseAll()
	{
		for (int i=0;i<MAX_SCENE;i++)
		{
			release(i);
		}
	}
	
	protected void finalize()
	{
		releaseAll();
	}
	
	public static SceneManager instance()
	{
		return _instance;
	}
	
	
	SceneManager()
	{
		_scenes = new Scene[MAX_SCENE];
	}
	
	Scene[]	_scenes;
	
	public static final int MAX_SCENE = 8;
	
	static SceneManager _instance = new SceneManager();
}