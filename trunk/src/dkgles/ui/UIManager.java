package dkgles.ui;

import java.util.ArrayList;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import dkgles.Material;
import dkgles.Movable;
import dkgles.android.wrapper.ServiceManager;

public class UIManager
{
	
	public static UIManager instance()
	{	
		if (_instance==null)
		{
			_instance = new UIManager();
		}
		
		return _instance;
	}
	
	public Touchable createTouchable(String name, float width, float height, Material material)
	{
		Touchable t = new Touchable(name, width, height, material);
		
		addTouchable(t);
	
		return t;
	}
	
	public void addTouchable(Touchable touchable)
	{
		_touchables.add(touchable);
	}
	
	public void touch(float x, float y)
	{
		float _x = (x - _width/2)/ _height;
		float _y = -(y - _height/2) / _height;
		
		Log.d(CLASS_TAG, "touch:" + _x + ", " + _y);
		
		for (Touchable touchable : _touchables)
		{
			if (touchable.hit(_x, _y))
			{
				ServiceManager.instance().vibrator().vibrate(100);
				touchable.onTouch();
			}
		}
	}
	
	
	public void untouch(float x, float y)
	{
		for (Touchable touchable : _touchables)
		{
			if (touchable.touch())
			{
				touchable.untouch();
			}
		}
	}
	
	
	public void onSize(float width, float height)
	{
		Log.v(CLASS_TAG, "onSize Event" + width + ", " + height);
		_width = width;
		_height = height;
	}
	
	public Movable root()
	{
		return _root;
	}
	
	private UIManager()
	{
		_root = new Movable("UIROOT");
		_touchables = new ArrayList<Touchable>();
		
	}
	
	private static UIManager _instance;
	private ArrayList<Touchable> 	_touchables;
	private float _width;
	private float _height;
	private Movable _root;
	
	private static final String CLASS_TAG = "UIManager";
}