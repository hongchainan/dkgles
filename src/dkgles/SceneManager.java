package dkgles;

import dkgles.render.RenderQueue;

/**
 *@autuor doki
 */
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
	
	/**
	 *Register scene to scene manager and return an ID
	 *@return scene ID
	 */
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

	/**
	 *Get scene by ID
	 *@return scene
	 */
	public Scene get(int id)
	{
		return _scenes[id];
	}
	

	/**
	 *Remove scene by ID
	 */
	public void remove(int id)
	{
		if (_scenes[id]==null)
			return;
		
		_scenes[id].release();
		_scenes[id] = null;
	}

	/**
	 *Remove all scenes
	 */
	public void removeAll()
	{
		for (int i=0;i<MAX_SCENE;i++)
		{
			remove(i);
		}
	}

	/**
	 */
	public void release()
	{
		removeAll();
		_scenes = null;
	}
	
	/**
	 *Update scenes
	 */
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
	
	
	/**
	 *Called by GC
	 */
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
		_scenes = new Scene[MAX_SCENES];
	}
	
	Scene[]	_scenes;
	
	public static final int MAX_SCENES = 8;
	
	static SceneManager _instance = new SceneManager();
}
