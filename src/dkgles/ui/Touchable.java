package dkgles.ui;

import android.util.Log;
import dkgles.Material;
import dkgles.Transformation;
import dkgles.primitive.Rectangle;

public class Touchable extends Rectangle
{
	public Touchable(String name, float width, float height, Material material)
	{
		super(name, width, height, material);
		_position = new float[3];
	}
	
	public void enable(boolean val)
	{
		_enable = val;
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
	
	public boolean touch()
	{
		return _touched;
	}
	
	public void untouch()
	{
		
	}
	
	//@override from Drawable
	public void setWorldTransformation(Transformation worldTransformation)
	{
		super.setWorldTransformation(worldTransformation);
		worldTransformation.getTranslation(_position);
	}
	
	public void onTouch()
	{
		Log.v(TAG, _name + " touch!!");
	}
	
	private final static String TAG = "Touchable";
	private boolean _enable;
	private boolean _touched;
	
	protected float[] _position;
}
