package dkgles;

import java.util.ArrayList;
import java.util.List;

/**
 *@author doki lin
 *TODO add synchronized modifier to let this class thread-safe
 */
public class Movable
{
	public static final int PARENT 	= 0;
	public static final int LOCAL 	= 1;
	
	
	
	/**
	 *@param name a human readable string for debugging
	 *@param scene a scene that this movable stayin.
	 */
	public Movable(String name, Movable parent, Scene scene)
	{
		_name = name;
		_parent = parent;
		_scene = scene;
		_children = new ArrayList<Movable>();
		_localTransformation = new Transformation();
		_worldTransformationCache = new Transformation();
		
		if (parent!=null)
		{
			parent.addChild(this);
		}
	}
	
	/**
	 * Rotate this movable scene node
	 * @param angle in radian
	 * @param x
	 * @param y
	 * @param z
	 * @param space Movable.LOCAL / Movable.PARENT
	 */
	public void rotate(float angle, float x, float y, float z, int space) throws IllegalArgumentException 
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
			throw new IllegalArgumentException();
		}
		
		_dirty = true;
	}
	
	
	public synchronized void translate(float x, float y, float z, int space)
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
			throw new IllegalArgumentException();
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
	public synchronized void position(float x, float y, float z)
	{
		_localTransformation._matrix[12] = x;
		_localTransformation._matrix[13] = y;
		_localTransformation._matrix[14] = z;
		_dirty = true;
	}
	
	public synchronized void scale(float x, float y, float z)
	{
		_localTransformation._matrix[0] = x;
		_localTransformation._matrix[5] = y;
		_localTransformation._matrix[10] = z;
		_dirty = true;
	}
	
	/**
	 *Move forward in local space
	 *@param dist the distance to move
	 */
	public void moveForward(float dist)
	{
		translate(.0f, .0f, -dist, LOCAL);
	}
	
	/**
	 *Move right in local space
	 */
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
	
	/**
	 * Rotate around local Y axis
	 * @param angle
	 */
	public void yaw(float angle)
	{
		rotate(angle, 0.0f, 1.0f, 0.0f, LOCAL);
	}
	
	/**
	 * Rotate around local X axis
	 * @param angle
	 */
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
		Movable child = new Movable(name, this, _scene);
		addChild(child);
		return child;
	}
	
	//TODO
	public Movable removeChild(String name)
	{
		return null;
	}

	/**
	 *Release children and drawable
	 */
	public void release()
	{
		_parent = null;
		_scene = null;
		
		if (_localTransformation!=null)
		{
			_localTransformation.release();
			_localTransformation = null;
			
		}
		
		if (_worldTransformationCache!=null)
		{
			_worldTransformationCache.release();
			_worldTransformationCache = null;
		}
			
		if (_children!=null)
		{
			for (Movable m : _children)
			{
				if (m!=null)
				{
					m.release();
				}
			}
			_children.clear();
			_children = null;
		}

		if (_drawable!=null)
		{
			_drawable.release();
			_drawable = null;
		}
	}
	
	/**
	 *@deprecated
	 */
	public void addChild(Movable child)
	{
		child.setParent(this);
		_children.add(child);
	}
	
	/**
	 *set drawable to this movable
	 *@param drawable drawable object
	 */
	public void setDrawable(Drawable drawable)
	{
		_drawable = drawable;
		
		_scene.getRenderQueue().addDrawble(drawable);
		
		// force scene node write self transformation to drawable
		_dirty = true;
		_scene.root().updateTransformation(Transformation.identity(), false);
	}
	
	
	void setParent(Movable parent)
	{
		_parent = parent;
	}

	/**
	 * Set transformation to Movable Object
	 * @throws CloneNotSupportedException 
	 */
	public synchronized void setTransformation(Transformation transformation) throws CloneNotSupportedException
	{
		// TODO use clone function
		_localTransformation = (Transformation) transformation.clone();
		_dirty = true;
	}
	
	/**
	 *Update world transformation by world = parent x local
	 *@param parentTransformation transformation from parent node
	 *@param parentDirty indicate if parent transformation is dirty since last update
	 */
	public synchronized void updateTransformation(Transformation parentTransformation, boolean parentDirty)
	{
		if (_dirty||parentDirty)
		{
			_worldTransformationCache.mul(parentTransformation, _localTransformation);
			
			if (_drawable != null)
			{
				_drawable.setWorldTransformation(_worldTransformationCache);
			}
		}
			
		for (Movable m : _children)
		{
			m.updateTransformation(_worldTransformationCache, _dirty);
		}
		
		_dirty = false;
	}

	public String toString()
	{
		return _name;
	}
	
	public String name()
	{
		return _name;
	}

	protected void finalize() throws Throwable
	{
		try
		{
			release();
		}
		finally
		{
			super.finalize();
		}
	}
	
	protected Transformation	_localTransformation;
	protected Transformation 	_worldTransformationCache;
	protected boolean	_dirty;
	
	final String	_name;
	
	List<Movable> 	_children;
	Movable		_parent;
	Drawable	_drawable;
	Scene		_scene;
}
