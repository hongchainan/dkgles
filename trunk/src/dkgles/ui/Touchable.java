package dkgles.ui;

import android.util.Log;
import dkgles.Material;
import dkgles.Transformation;
import dkgles.primitive.Rectangle;

public class Touchable extends Rectangle
{
	public interface ITouchEventHandler
	{
		public void touch(final int id);
		public void unTouch(final int id);
		public void enable(final boolean b);
	}
	
	public Touchable(String name, float width, float height, Material material)
	{
		super(name, width, height, material);
		_position = new float[3];
		_handler = GetNullTouchEventHandler();
	}
	
	public void bindTouchEventHandler(ITouchEventHandler handler)
	{
		if (handler!=null)
		{
			_handler = handler;
		}
		else
		{
			_handler = GetNullTouchEventHandler();
		}
	}
	
	public void enable(boolean val)
	{
		_enable = val;
		_handler.enable(_enable);
		
	}
	
	public boolean enable()
	{
		return _enable;
	}
	
	/**
	 * Test if hitted
	 * @param x: x coordinate 
	 * @param y
	 * @return
	 */
	public boolean hit(float x, float y)
	{
		float rx = StrictMath.abs(_position[0] - x)*2;
		float ry = StrictMath.abs(_position[1] - y)*2;
		
		if (rx > _width || ry > _height)
		{
			return false;
		}
		
		return true;
	}
	
	public void touch()
	{
		if (_enable)
		_handler.touch(0);
		Log.v(TAG, _name + " touch!!");
	}
	
	public void unTouch()
	{
		_handler.unTouch(0);
		Log.v(TAG, _name + " untouch!!");
	}
	
	//@override from Drawable
	public void setWorldTransformation(Transformation worldTransformation)
	{
		super.setWorldTransformation(worldTransformation);
		worldTransformation.getTranslation(_position);
	}
	
	private static ITouchEventHandler GetNullTouchEventHandler()
	{
		if (_nilTouchEventHandler==null)
		{
			_nilTouchEventHandler = new NullTouchEventHandler();
		}
		return _nilTouchEventHandler;
	}
	
	private final static String TAG = "Touchable";
	private boolean _enable;
	private boolean _touched;
	private ITouchEventHandler _handler;
	private static NullTouchEventHandler _nilTouchEventHandler;
	
	protected float[] _position;
	
	class NullTouchEventHandler implements ITouchEventHandler
	{
		public void touch(final int id)
		{
			
		}
		
		public void unTouch(final int id)
		{
			
		}
		
		public void enable(final boolean b)
		{
			
		}
	}
}
