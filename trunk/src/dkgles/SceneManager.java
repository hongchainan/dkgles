package dkgles;

import dkgles.render.RenderQueue;

/**
 *@autuor doki
 */
public enum SceneManager
{
	INSTANCE;
	
	public class TooManyScenesException extends RuntimeException
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public TooManyScenesException(String name)
		{
			super(name);
		}
	}
	
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
			if (scenes_[i]==null)
			{
				scenes_[i] = scene;
				return i;
			}
		}	
		
		throw new TooManyScenesException("");
	}

	/**
	 * Get scene by ID
	 * @return scene
	 */
	public Scene get(int id)
	{
		return scenes_[id];
	}
	
	public Scene getByName(String name)
	{
		int id = getIdByName(name);
		
		if (id>=0)
		{
			return scenes_[id];
		}
		else
		{
			return null;
		}
	}
	
	public int getIdByName(String name)
	{
		for (int i=0;i<MAX_SCENES;i++)
		{
			if (scenes_[i]==null)
				continue;
			
			if (scenes_[i].name().equals(name))
			{
				return i;
			}
		}
		
		return -1;
	}
	

	/**
	 *Remove scene by ID
	 */
	public synchronized void destroy(int id)
	{
		if (scenes_[id]==null)
			return;
		
		scenes_[id].release();
		scenes_[id] = null;
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
		scenes_ = null;
	}
	
	public void parse(int rsc_id)
	{
		sceneBuilder_.build(rsc_id);
	}
	
	public void registerSceneComponentCreatedListener(IBuildSceneListener listener)
	{
		sceneBuilder_.registerListener(listener);
	}
	
	public void unregisterSceneComponentCreatedListener(IBuildSceneListener listener)
	{
		sceneBuilder_.unregisterListener(listener);
	}
	
	public void unregisterAllSceneComponentCreatedListener()
	{
		sceneBuilder_.unregisterAllListener();
	}
	
	/**
	 *Update scenes
	 */
	public void updateSceneGraph()
	{
		for (int i=0;i<MAX_SCENES;i++)
		{
			if (scenes_[i]!=null)
			{
				scenes_[i].updateSceneGraph();
			}
		}
	}
	
	public void updateHandler(long deltaTime)
	{
		for (int i=0;i<MAX_SCENES;i++)
		{
			if (scenes_[i]!=null)
			{
				scenes_[i].updateHandler(deltaTime);
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
		
	private SceneManager()
	{
		scenes_ = new Scene[MAX_SCENES];
		sceneBuilder_ = new SceneBuilder(null);
	}
	
	private Scene[]			scenes_;
	private SceneBuilder	sceneBuilder_;
	
	public static final int MAX_SCENES = 8;
	
	//static SceneManager _instance = new SceneManager();
}
