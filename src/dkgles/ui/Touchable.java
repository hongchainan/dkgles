package dkgles.ui;

import dkgles.Transformation;
import dkgles.primitive.Rectangle;

public class Touchable extends Rectangle
{
	public Touchable(String name, float width, float height)
	{
		super(name, width, height);
		_position = new float[3];
	}
	
	public boolean hit(float x, float y)
	{
		float rx = StrictMath.abs(_position[0] - x);
		float ry = StrictMath.abs(_position[1] - y);
		
		if (rx > _width / 2 || ry > _height/2)
		{
			return false;
		}
		
		return true;
	}
	
	//@override from Drawable
	public void setWorldTransformation(Transformation worldTransformation)
	{
		super.setWorldTransformation(worldTransformation);
		worldTransformation.getTranslation(_position);
	}
	
	public void onTouch()
	{
		//do nothing
	}
	
	protected float[] _position;
}
