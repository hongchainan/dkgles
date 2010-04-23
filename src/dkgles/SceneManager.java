package dkgles;

import dkgles.render.RenderQueue;

/**
 *@autuor doki
 */
public enum SceneManager
{
	INSTANCE;
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
	
	/**
	 *Register scene to scene manager and return an ID
	 *@return scene ID
	 */
	public int register(Scene scene)
	{
		for (int i=0;i<MAX_SCENES;i++)
		{
			if (_scenes[i]==null)
			{
				_scenes[i] = scene;
				return i;
			}
		}	
		return -1;
	}

	/**
	 * Get scene by ID
	 * @return scene
	 */
	public Scene get(int id)
	{
		return _scenes[id];
	}
	

	/**
	 *Remove scene by ID
	 */
	public synchronized void destroy(int id)
	{
		if (_scenes[id]==null)
			return;
		
		_scenes[id].release();
		_scenes[id] = null;
	}

	/**
	 *Remove all scenes
	 */
	public synchronized void destroyAll()
	{
		for (int i=0;i<MAX_SCENES;i++)
		{
			destroy(i);
		}
	}

	/**
	 */
	public void release()
	{
		destroyAll();
		_scenes = null;
	}
	
	/**
	 *Update scenes
	 */
	public void updateSceneGraph()
	{
		for (int i=0;i<MAX_SCENES;i++)
		{
			if (_scenes[i]!=null)
			{
				_scenes[i].updateSceneGraph();
			}
		}
	}
	
	public void updateHandler(long deltaTime)
	{
		for (int i=0;i<MAX_SCENES;i++)
		{
			if (_scenes[i]!=null)
			{
				_scenes[i].updateHandler(deltaTime);
			}
		}
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
		
	SceneManager()
	{
		_scenes = new Scene[MAX_SCENES];
	}
	
	Scene[]	_scenes;
	
	public static final int MAX_SCENES = 8;
	
	//static SceneManager _instance = new SceneManager();
}
