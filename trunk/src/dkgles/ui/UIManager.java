package dkgles.ui;

import java.util.ArrayList;

import android.util.Log;
import dkgles.Material;
import dkgles.Movable;
import dkgles.android.wrapper.ServiceManager;
import dkgles.render.OrthoRenderer;
import dkgles.render.RenderQueue;

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
	
	
	public void render(GL10 gl)
	{
		_orthoRenderer.render(gl, _scene.getRenderQueue());	
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
				touchable.touch();
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
		_width	= width;
		_height = height;
	}
	
	public Movable root()
	{
		return _scene.root();
	}
	
	private UIManager()
	{
		_touchables = new ArrayList<Touchable>();
		_scene 		= new Scene("UIScene", new RenderQueue("UIRenderQueue", 5));
	}
	
	private Scene					_scene;
	private ArrayList<Touchable> 	_touchables;
	private float _width;
	private float _height;
	
	private static UIManager 		_instance;
	private static final String CLASS_TAG = "UIManager";
}