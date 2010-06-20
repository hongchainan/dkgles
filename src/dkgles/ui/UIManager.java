package dkgles.ui;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import dkgles.DefaultBuildSceneListener;
import dkgles.IBuildSceneListener;
import dkgles.Material;
import dkgles.Movable;
import dkgles.Scene;
import dkgles.SceneManager;
import dkgles.android.wrapper.ServiceManager;

public enum UIManager implements OnTouchListener
{	
	INSTANCE;

	public int createTouchable(String name, float width, float height, Material material, Movable node)
	{
		Touchable touchable = new Touchable(name, width, height, material);
		
		if (node!=null)
		{
			node.setDrawable(touchable);	
		}
		
		return register(touchable);
	}
	
	/**
	 * Register touchable
	 * @param touchable
	 * @return
	 */
	public int register(Touchable touchable)
	{
		for (int i=0;i<MAX_TOUCHABLES;i++)
		{
			if (touchables_[i]==null)
			{
				touchables_[i] = touchable;
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Get touchable by given id
	 * @param id
	 * @return
	 */
	public Touchable get(int id)
	{
		return touchables_[id];
	}
	
	/**
	 * Destroy touchable by given id
	 * @param id
	 */
	public void destroy(int id)
	{
		if (touchables_[id]!=null)
		{
			touchables_[id].release();
			touchables_[id] = null;
		}
	}
	
	public void destroyAll()
	{
		if (touchables_!=null)
		{
			for (int i=0;i<MAX_TOUCHABLES;i++)
			{
				destroy(i);
			}
			touchables_ = null;
		}
	}
		
	/**
	 * Called by Android View.onTocch framework
	 */
	public boolean onTouch(View v, MotionEvent event)
	{
		if (!enable_)
			return false;
		
		float x = transformX(event.getX());
		float y = transformY(event.getY());
		int action = event.getAction();
		
		if (action == MotionEvent.ACTION_DOWN)
		{
			//Log.v(CLASS_TAG, "action down!!" + x + ", " + y);
			//ServiceManager.INSTANCE.vibrator().vibrate(70);
			
			for (Touchable touchable : touchables_)
			{
				if (touchable!=null)
				{
					if (touchable.hit(x, y))
					{
						touchable.touch();			
					}
				}
			}
		}
		else if (action == MotionEvent.ACTION_UP)
		{
			//Log.v(CLASS_TAG, "action up");
			
			for (Touchable touchable : touchables_)
			{
				if (touchable!=null)
				{
					if (touchable.beTouched())
					{
						touchable.unTouch();
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Internal use.
	 * @param x
	 * @return
	 */
	private float transformX(float x)
	{
		return (x / height_) - halfAsr_;
	}
	
	/**
	 * Internal use
	 * @param y
	 * @return
	 */
	float transformY(float y)
	{
		return 0.5f - (y / height_);
	}
	
	
	public void onSize(float width, float height)
	{
		//Log.v(CLASS_TAG, "onSize Event" + width + ", " + height);
		width_	= width;
		height_ = height;
		halfAsr_ = (width_ / height_)/2.0f;
	}
	
	public void setEnable(boolean val)
	{
		enable_ = val;
		
		for (Touchable touchable : touchables_)
		{
			if (touchable!=null)
			{
				touchable.setEnable(enable_);
			}
		}
	}

	/**
	 * 
	 * @return
	 * @drepcated
	 */
	public Scene scene()
	{
		return SceneManager.INSTANCE.get(sceneId_);
	}
	
	public void print(float x, float y, String message)
	{
		
	}
	
	
	private UIManager()
	{
		reset();
		sceneCreatedListener_ = new SceneCreatedListener();
	}

	public void reset()
	{
		release();
		touchables_ 	= new Touchable[MAX_TOUCHABLES];
		halfAsr_ 	= 0.5f;
		enable_ 	= true;
		//sceneId_ 	= SceneManager.INSTANCE.create(
//			"UIScene",
	//		 new OrthoRenderQueue("UIRenderQueue", 8, RenderQueue.UI_LAYER));
		active_ = true;
		
	}
	
	public IBuildSceneListener sceneCreatedListener()
	{
		return sceneCreatedListener_;
		
	}

	public void release()
	{
		if (!active_)
			return;
		
		setEnable(false);
		destroyAll();
		SceneManager.INSTANCE.destroy(sceneId_);
		sceneId_ = -1;
		active_ = false;
	}
	
	
	private Touchable[]	touchables_;
	private int 	sceneId_;	

	private boolean enable_;
	private boolean	active_;
	private float width_;
	private float height_;
	private float halfAsr_;	// Half value of aspect ratio
	
	static final String CLASS_TAG = "UIManager";
	public final static int MAX_TOUCHABLES = 8;
	
	private SceneCreatedListener sceneCreatedListener_;
	
	class SceneCreatedListener extends DefaultBuildSceneListener
	{
		public void onSceneCreated(int id, Scene scene)
		{
			sceneId_ = id;
			active_ = true;
		}
	}
	
}
