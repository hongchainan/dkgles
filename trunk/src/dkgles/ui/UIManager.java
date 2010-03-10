package dkgles.ui;

import java.util.ArrayList;

import android.util.Log;
import dkgles.Material;
import dkgles.Movable;

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
		return new Touchable(name, width, height, material);
	}
	
	public void addTouchable(Touchable touchable)
	{
		_touchables.add(touchable);
	}
	
	public void touch(float x, float y)
	{
		for (Touchable touchable : _touchables)
		{
			if (touchable.hit(x/_width, y/_height))
			{
				touchable.onTouch();
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