package dkgles.ui;

import java.util.ArrayList;

import dkgles.Drawable;
import dkgles.Material;
import dkgles.Mesh;
import dkgles.Transformation;
import dkgles.primitive.Rectangle;

public class Touchable extends Drawable
{
	public interface ITouchEventHandler
	{
		public void touch(final int id);
		public void unTouch(final int id);
		public void enable(final boolean b);
	}
	
	public Touchable(String name, Rectangle rectangle)
	{
		super(name);
		setMesh(rectangle);
		
		_position 	= new float[3];
		_handlers 	= new ArrayList<ITouchEventHandler>();
		_enable 	= true;
		_width		= rectangle.width();
		_height		= rectangle.height();
		
	}
	
	/**
	 * 
	 * @param name
	 * @param width
	 * @param height
	 * @param material
	 * @deprecated
	 */
	public Touchable(String name, float width, float height, Material material)
	{
		super(name);
		
		Rectangle rectangle =  new Rectangle("RTG_" + name, width, height, material);
		setMesh(rectangle);
		
		_position 	= new float[3];
		_handlers 	= new ArrayList<ITouchEventHandler>();
		_enable 	= true;
		_width		= width;
		_height		= height;
	}
	
	public void release()
	{
		super.release();
		
		if (_handlers!=null)
		{
			_handlers.clear();
			_handlers = null;
		}
		
		if (_position!=null)
		{
			_position = null;
		}
	}
	
	/**
	 * Bind a touch event handler
	 * @param handler
	 */
	public void addTouchEventHandler(ITouchEventHandler handler)
	{
		if (handler!=null)
		{
			_handlers.add(handler);
		}
		
		/*if (handler!=null)
		{
			_handler = handler;
		}
		else
		{
			_handler = _nilTouchEventHandler;
		}*/
	}
	
	/**
	 * Enable/ Disable this touchable
	 * @param val
	 */
	public void enable(boolean val)
	{
		_enable = val;
		
		for (ITouchEventHandler handler : _handlers)
		{
			handler.enable(_enable);
		}
		//_handler.enable(_enable);
		
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
		//_handler.touch(0);
		
		for (ITouchEventHandler handler : _handlers)
		{
			handler.touch(0);
		}
		
		
		//Log.v(TAG, _name + " touch!!");
	}
	
	/**
	 * Should be called by UI manager always
	 * Do not call it directly.
	 */
	public synchronized void unTouch()
	{
		if (!_enable)
			return;
		
		//_handler.unTouch(0);
		
		for (ITouchEventHandler handler : _handlers)
		{
			handler.unTouch(0);
		}
		
		
		_touched = false;
		//Log.v(TAG, _name + " untouch!!");
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
	float	_width;
	float	_height;
	//ITouchEventHandler _handler;
	ArrayList<ITouchEventHandler> _handlers;
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
