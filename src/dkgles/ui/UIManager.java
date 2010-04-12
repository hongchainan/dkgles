package dkgles.ui;

import java.util.ArrayList;

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
	public Touchable createTouchable(String name, float width, float height, Material material, Movable node)
	{
		Touchable t = new Touchable(name, width, height, material);
		addTouchable(t);
		
		if (node!=null)
		{
			node.setDrawable(t);	
		}
		
		return t;
	}
	
	public void addTouchable(Touchable touchable)
	{
		_touchables.add(touchable);
	}
	
	
	public void touch(float x, float y)
	{
		/*float _x = (x - _width/2)/ _height;
		float _y = -(y - _height/2) / _height;
		
		Log.d(CLASS_TAG, "touch:" + _x + ", " + _y);
		
		for (Touchable touchable : _touchables)
		{
			if (touchable.hit(_x, _y))
			{
				ServiceManager.instance().vibrator().vibrate(100);
				touchable.touch();
			}
		}*/
	}
	
	/**
	 * Called by Android View.onTocch framework
	 */
	public boolean onTouch(View v, MotionEvent event)
	{
		float x = transformX(event.getX());
		float y = transformY(event.getY());
		int action = event.getAction();
		
		if (action == MotionEvent.ACTION_DOWN)
		{
			Log.v(CLASS_TAG, "action down!!" + x + ", " + y);
			
			for (Touchable touchable : _touchables)
			{
				if (touchable.hit(x, y))
				{
					touchable.touch();
					ServiceManager.instance().vibrator().vibrate(50);
				}
			}
		}
		else if (action == MotionEvent.ACTION_UP)
		{
			Log.v(CLASS_TAG, "action up");
			
			for (Touchable touchable : _touchables)
			{
				if (touchable.beTouched())
				{
					touchable.unTouch();
				}
				
				/*if (touchable.hit(x, y))
				{
					touchable.unTouch();
				}*/
			}
		}
		
		return false;
	}
	
	private float transformX(float x)
	{
		return (x / _height) - _halfAsr;
	}
	
	private float transformY(float y)
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
	
	
	
	public static UIManager instance()
	{	
		if (_instance==null)
		{
			_instance = new UIManager();
		}
		
		return _instance;
	}
	
	private UIManager()
	{
		super("UIScene", new OrthoRenderQueue("UIRenderQueue", 5, RenderQueue.UI_LAYER));
		_touchables = new ArrayList<Touchable>();
		_halfAsr = 0.5f;
	}
	
	
	ArrayList<Touchable> 	_touchables;
	float _width;
	float _height;
	float _halfAsr;	// Half value of aspect ratio
	
	static UIManager	_instance;
	static final String CLASS_TAG = "UIManager";
	
}