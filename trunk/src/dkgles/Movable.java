package dkgles;

import java.util.ArrayList;
import java.util.List;
import dkgles.math.MathUtils;

public class Movable
{
	public static final int PARENT 	= 0;
	public static final int LOCAL 	= 1;
	
	
	private String 			_name;
	private List<Movable> 	_childList;
	private Movable			_parent;
	protected Transformation	_localTransformation;
	protected Transformation 	_worldTransformationCache;
	private Drawable		_drawable;
	protected boolean		_dirty;
	
	public Movable(String name)
	{
		_name = name;
		_parent = null;
		_childList = new ArrayList<Movable>();
		_localTransformation = new Transformation();
		_worldTransformationCache = new Transformation();
	}
	
	
	public void rotate(float angle, float x, float y, float z, int space)
	{
		if (space == LOCAL)
		{
			_localTransformation.rotateInLocalSpace(angle, x, y, z);
		}
		else if (space == PARENT)
		{
			_localTransformation.rotateInParentSpace(angle, x, y, z);
		}
		else
		{
			// throw exception
		}
		
		_dirty = true;
	}
	
	
	public void translate(float x, float y, float z, int space)
	{
		if (space == LOCAL)
		{
			_localTransformation.translateInLocalSpace(x, y, z);
		}
		else if (space == PARENT)
		{
			_localTransformation.translateInParentSpace(x, y, z);
		}
		else
		{
			// throw exception
		}
		
		_dirty = true;
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
	
	
	public void roll(float angle)
	{
		rotate(angle, 0.0f, 0.0f, 1.0f, LOCAL);
	}
	
	
	public void yaw(float angle)
	{
		rotate(angle, 0.0f, 1.0f, 0.0f, LOCAL);
	}
	
	
	public void pitch(float angle)
	{
		rotate(angle, 1.0f, 0.0f, 0.0f, LOCAL);
	}
	
	
	public void lookAt(float x, float y, float z)
	{
	}
	
	
	public void addChild(Movable child)
	{
		child.setParent(this);
		_childList.add(child);
	}
	
	
	public void setDrawable(Drawable drawable)
	{
		_drawable = drawable;
	}
	
	
	private void setParent(Movable parent)
	{
		_parent = parent;
	}
	
	
	public void updateTransformation(Transformation parentTransformation, boolean parentDirty)
	{
		if (_dirty||parentDirty)
		{
			_worldTransformationCache.mul(parentTransformation, _localTransformation);
			
			if (_drawable != null)
			{
				_drawable.setWorldTransformation(_worldTransformationCache);
				//_drawable.render();
			}
			
		}
			
		for (Movable m : _childList)
		{
			m.updateTransformation(_worldTransformationCache, _dirty);
		}
		
		_dirty = false;
	}
	
	
	
	
	
	
	
}
