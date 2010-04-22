package dkgles.ui;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import dkgles.Material;
import dkgles.Movable;
import dkgles.Scene;
import dkgles.android.wrapper.ServiceManager;
import dkgles.render.OrthoRenderQueue;
import dkgles.render.RenderQueue;

public class UIManager extends Scene implements OnTouchListener
{	
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
			if (_touchables[i]==null)
			{
				_touchables[i] = touchable;
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
		return _touchables[id];
	}
	
	/**
	 * Destroy touchable by given id
	 * @param id
	 */
	public void destroy(int id)
	{
		if (_touchables[id]!=null)
		{
			_touchables[id].release();
			_touchables[id] = null;
		}
	}
	
	public void destroyAll()
	{
		for (int i=0;i<MAX_TOUCHABLES;i++)
		{
			destroy(i);
		}
	}
	
	/**
	 * @deprecated
	 * @param touchable
	 */
	public void addTouchable(Touchable touchable)
	{
		//_touchables.add(touchable);
	}
		
	/**
	 * Called by Android View.onTocch framework
	 */
	public boolean onTouch(View v, MotionEvent event)
	{
		if (!_enable)
			return false;
		
		float x = transformX(event.getX());
		float y = transformY(event.getY());
		int action = event.getAction();
		
		if (action == MotionEvent.ACTION_DOWN)
		{
			Log.v(CLASS_TAG, "action down!!" + x + ", " + y);
			ServiceManager.instance().vibrator().vibrate(70);
			
			for (Touchable touchable : _touchables)
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
			Log.v(CLASS_TAG, "action up");
			
			for (Touchable touchable : _touchables)
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
	float transformX(float x)
	{
		return (x / _height) - _halfAsr;
	}
	
	/**
	 * Internal use
	 * @param y
	 * @return
	 */
	float transformY(float y)
	{
		return 0.5f - (y / _height);
	}
	
	
	public void onSize(float width, float height)
	{
		Log.v(CLASS_TAG, "onSize Event" + width + ", " + height);
		_width	= width;
		_height = height;
		_halfAsr = (_width / _height)/2.0f;
	}
	
	public void enable(boolean val)
	{
		_enable = val;
	}
	
	
	
	public static UIManager instance()
	{	
		return _instance;
	}
	
	UIManager()
	{
		super("UIScene", new OrthoRenderQueue("UIRenderQueue", 5, RenderQueue.UI_LAYER));
		//_touchables = new ArrayList<Touchable>();
		_touchables = new Touchable[MAX_TOUCHABLES];
		_halfAsr = 0.5f;
		_enable = true;
	}
	
	public final static int MAX_TOUCHABLES = 8;
	Touchable[]	_touchables;
	
	boolean _enable;
	//ArrayList<Touchable> 	_touchables;
	float _width;
	float _height;
	float _halfAsr;	// Half value of aspect ratio
	
	static UIManager	_instance = new UIManager();
	static final String CLASS_TAG = "UIManager";
	
}