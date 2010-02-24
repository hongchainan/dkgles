package dkgles;

import java.util.ArrayList;
import java.util.List;



public class Movable
{
	public static final int GLOBAL 	= 0;
	public static final int LOCAL 	= 1;
	
	
	private String 			_name;
	private List<Movable> 	_childList;
	private Movable			_parent;
	private Transformation	_localTransformation;
	private Drawable		_drawable;
	
	public Movable(String name)
	{
		_name = name;
		_parent = null;
		_childList = new ArrayList<Movable>();
		_localTransformation = new Transformation();
	}
	
	
	public void rotate(float degree, float x, float y, float z, int space)
	{
		if (space == LOCAL)
		{
			_localTransformation.rotateLocal(MathUtils.DegreeToRadian(degree), x, y, z);
		}
		else if (space == GLOBAL)
		{
			_localTransformation.rotateGlobal(MathUtils.DegreeToRadian(degree), x, y, z);
		}
		else
		{
			// throw exception
		}
	}
	
	
	public void translate(float x, float y, float z, int space)
	{
		if (space == LOCAL)
		{
			_localTransformation.translateLocal(x, y, z);
		}
		else if (space == GLOBAL)
		{
			_localTransformation.translateGlobal(x, y, z);
		}
		else
		{
			// throw exception
		}
	}
	
	
	public void moveForward(float dist)
	{
		translate(.0f, .0f, -dist, LOCAL);
	}
	
	
	public void moveRight(float dist)
	{
		translate(dist, .0f, .0f, LOCAL);
	}
	
	
	public void moveUp(float dist)
	{
		translate(.0f, dist, .0f, LOCAL);
	}
	
	
	public void roll(float degree)
	{
		rotate(degree, 0.0f, 0.0f, 1.0f, LOCAL);
	}
	
	
	public void yaw(float degree)
	{
		rotate(degree, 0.0f, 1.0f, 0.0f, LOCAL);
	}
	
	
	public void pitch(float degree)
	{
		rotate(degree, 1.0f, 0.0f, 0.0f, LOCAL);
	}
	
	
	public void lookAt(float x, float y, float z)
	{
	}
	
	
	public void addChild(Movable child)
	{
		child.setParent(this);
		_childList.add(child);
	}
	
	
	public void setParent(Movable parent)
	{
		_parent = parent;
	}
	
	
	public void updateTransformation(Transformation parentTransformation)
	{
		Transformation worldTransformation = parentTransformation.mul(_localTransformation);
		
		if (_drawable != null)
		{
			_drawable.setWorldTransformation(worldTransformation);
			_drawable.render();
		}
		
		for (Movable m : _childList)
		{
			m.updateTransformation(worldTransformation);
		}
	}
	
	
	
	
	
	
	
}
