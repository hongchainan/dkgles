package dkgles;

import java.util.ArrayList;
import java.util.List;
import dkgles.math.MathUtils;

public class Movable
{
	public static final int PARENT 	= 0;
	public static final int LOCAL 	= 1;
	
	
	
	
	public Movable(String name, Scene scene)
	{
		_name = name;
		_parent = null;
		_scene = scene;
		_childList = new ArrayList<Movable>();
		_localTransformation = new Transformation();
		_worldTransformationCache = new Transformation();
	}
	
	/**
	 * Rotate this movable scene node
	 * @param angle in radian
	 * @param x
	 * @param y
	 * @param z
	 * @param space Movable.LOCAL / Movable.PARENT
	 */
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
	
	/**
	 * Set position
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void position(float x, float y, float z)
	{
		_localTransformation._matrix[12] = x;
		_localTransformation._matrix[13] = y;
		_localTransformation._matrix[14] = z;
		
		_dirty = true;
	}
	
	public void scale(float x, float y, float z)
	{
		_localTransformation._matrix[0] = x;
		_localTransformation._matrix[5] = y;
		_localTransformation._matrix[10] = z;
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
	
	/**
	 * Rotate around local axis Z
	 * @param angle
	 */
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
		//TODO
	}
	
	/**
	 * Create a new child of this node
	 * @param name
	 * @return child
	 */
	public Movable createChild(String name)
	{
		Movable child = new Movable(name, _scene);
		addChild(child);
		return child;
	}
	
	//TODO
	public Movable removeChild(String name)
	{
		return null;
	}
	
	
	public void addChild(Movable child)
	{
		child.setParent(this);
		_childList.add(child);
	}
	
	
	public void setDrawable(Drawable drawable)
	{
		_drawable = drawable;
		
		_scene.getRenderQueue().addDrawble(drawable);
		
		// force scene node write self transformation to drawable
		_dirty = true;
		_scene.root().updateTransformation(Transformation.identity(), false);
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
	
	protected Transformation	_localTransformation;
	protected Transformation 	_worldTransformationCache;
	protected boolean			_dirty;
	
	private String 			_name;
	private List<Movable> 	_childList;
	private Movable			_parent;
	private Drawable		_drawable;
	private Scene			_scene;
}
