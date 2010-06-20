package dkgles.ui;

import java.util.ArrayList;

import dkgles.Drawable;
import dkgles.Material;
import dkgles.Mesh;
import dkgles.Transformation;
import dkgles.primitive.Rectangle;

public class Touchable extends Drawable
{
	public interface ITouchEventListener
	{
		public void touch(final int id);
		public void unTouch(final int id);
		public void enable(final boolean b);
	}
	
	public Touchable(String name, Rectangle rectangle)
	{
		super(name);
		setMesh(rectangle);
		
		position_ 	= new float[3];
		listeners_ 	= new ArrayList<ITouchEventListener>();
		enable_ 	= true;
		width_		= rectangle.width();
		height_		= rectangle.height();
		
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
		
		position_ 	= new float[3];
		listeners_ 	= new ArrayList<ITouchEventListener>();
		enable_ 	= true;
		width_		= width;
		height_		= height;
	}
	
	public void release()
	{
		super.release();
		
		if (listeners_!=null)
		{
			listeners_.clear();
			listeners_ = null;
		}
		
		if (position_!=null)
		{
			position_ = null;
		}
	}
	
	/**
	 * Bind a touch event handler
	 * @param listener
	 */
	public void addTouchEventListener(ITouchEventListener listener)
	{
		if (listener!=null)
		{
			listeners_.add(listener);
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
	public void setEnable(boolean val)
	{
		enable_ = val;
		
		for (ITouchEventListener handler : listeners_)
		{
			handler.enable(enable_);
		}
		//_handler.enable(_enable);
		
	}
	
	public boolean enable()
	{
		return enable_;
	}
	
	/**
	 * Test if hitted
	 * @param x: x coordinate 
	 * @param y
	 * @return
	 */
	public boolean hit(float x, float y)
	{
		float rx = StrictMath.abs(position_[0] - x)*2;
		float ry = StrictMath.abs(position_[1] - y)*2;
		
		if (rx > width_ || ry > height_)
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
		if (!enable_)
			return;
		
		_touched = true;
		//_handler.touch(0);
		
		for (ITouchEventListener handler : listeners_)
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
		if (!enable_)
			return;
		
		//_handler.unTouch(0);
		
		for (ITouchEventListener handler : listeners_)
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
		worldTransformation.getTranslation(position_);
	}
	
	final static String TAG = "Touchable";
	boolean enable_;
	boolean _touched;
	float	width_;
	float	height_;
	//ITouchEventHandler _handler;
	ArrayList<ITouchEventListener> listeners_;
	static NilTouchEventHandler _nilTouchEventHandler = new NilTouchEventHandler();
	
	protected float[] position_;
	
}

/**
 * A dummy touchable
 * @author doki
 *
 */
class NilTouchEventHandler implements Touchable.ITouchEventListener
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
