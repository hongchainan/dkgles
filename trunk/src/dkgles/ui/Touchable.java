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
		_handler = _nilTouchEventHandler;
		_enable = true;
	}
	
	/**
	 * Bind a touch event handler
	 * @param handler
	 */
	public void bindTouchEventHandler(ITouchEventHandler handler)
	{
		if (handler!=null)
		{
			_handler = handler;
		}
		else
		{
			_handler = _nilTouchEventHandler;
		}
	}
	
	/**
	 * Enable/ Disable this touchable
	 * @param val
	 */
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
	
	/**
	 * Query whether is it touched
	 * @return
	 */
	public boolean beTouched()
	{
		return _touched; 
	}
	
	/**
	 * Should be called by UI manager always
	 * Do not call it directly.
	 */
	public synchronized void touch()
	{
		if (!_enable)
			return;
		
		_touched = true;
		_handler.touch(0);
		Log.v(TAG, _name + " touch!!");
	}
	
	/**
	 * Should be called by UI manager always
	 * Do not call it directly.
	 */
	public synchronized void unTouch()
	{
		if (!_enable)
			return;
		_handler.unTouch(0);
		_touched = false;
		Log.v(TAG, _name + " untouch!!");
	}
	
	//@override from Drawable
	public void setWorldTransformation(Transformation worldTransformation)
	{
		super.setWorldTransformation(worldTransformation);
		worldTransformation.getTranslation(_position);
	}
	
	final static String TAG = "Touchable";
	boolean _enable;
	boolean _touched;
	ITouchEventHandler _handler;
	static NilTouchEventHandler _nilTouchEventHandler = new NilTouchEventHandler();
	
	protected float[] _position;
	
}

/**
 * A dummy touchable
 * @author doki
 *
 */
class NilTouchEventHandler implements Touchable.ITouchEventHandler
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
