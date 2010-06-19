package dkgles;

import java.util.ArrayList;
import java.util.List;

import dkgles.math.Vector3;


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
		name_ = name;
		parent_ = parent;
		scene_ = scene;
		children_ = new ArrayList<Movable>();
		localTransformation_ = new Transformation();
		worldTransformationCache_ = new Transformation();
		
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
			localTransformation_.rotateInLocalSpace(angle, x, y, z);
		}
		else if (space == PARENT)
		{
			localTransformation_.rotateInParentSpace(angle, x, y, z);
		}
		else
		{
			throw new IllegalArgumentException();
		}
		
		dirty_ = true;
	}
	
	public void setRotate(float angle, float x, float y, float z, int space)
	{
		float tx, ty, tz;
		
		tx = localTransformation_.matrix[12];
		ty = localTransformation_.matrix[13];
		tz = localTransformation_.matrix[14];
		
		localTransformation_.setIdentity();
		
		rotate(angle, x, y, z, space);
		
		
	}
	
	/**
	 * face direction is -Z;
	 * 
	 * @return
	 */
	public Vector3 faceDirection()
	{
		return new Vector3(
				-worldTransformationCache_.matrix[8],
				-worldTransformationCache_.matrix[9],
				-worldTransformationCache_.matrix[10]);
	}
	
	/**
	 * 
	 * @return
	 */
	public Vector3 worldPosition()
	{
		return new Vector3(
				worldTransformationCache_.matrix[12],
				worldTransformationCache_.matrix[13],
				worldTransformationCache_.matrix[14]);
	}
	
	/**
	 * right is X 
	 * @return
	 */
	public Vector3 rightDirection()
	{
		Vector3 right = new Vector3(
				worldTransformationCache_.matrix[0],
				worldTransformationCache_.matrix[1],
				worldTransformationCache_.matrix[2]);
		
		right.normalize();
		
		return right;	
	}
	
	public synchronized void setPosition(Vector3 position)
	{
		localTransformation_.matrix[12] = position.x;
		localTransformation_.matrix[13] = position.y;
		localTransformation_.matrix[14] = position.z;
		
		dirty_ = true;
	}
	
	public Vector3 position()
	{
		return new Vector3(
				localTransformation_.matrix[12], 
				localTransformation_.matrix[13],
				localTransformation_.matrix[14]
				);
	}
	
	public synchronized void translate(float x, float y, float z, int space)
	{
		if (space == LOCAL)
		{
			localTransformation_.translateInLocalSpace(x, y, z);
		}
		else if (space == PARENT)
		{
			localTransformation_.translateInParentSpace(x, y, z);
		}
		else
		{
			throw new IllegalArgumentException();
		}
		
		dirty_ = true;
	}
	
	/**
	 * Set position
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public synchronized void setPosition(float x, float y, float z)
	{
		localTransformation_.matrix[12] = x;
		localTransformation_.matrix[13] = y;
		localTransformation_.matrix[14] = z;
		dirty_ = true;
	}
	
	public synchronized void scale(float x, float y, float z)
	{
		localTransformation_.matrix[0] = x;
		localTransformation_.matrix[5] = y;
		localTransformation_.matrix[10] = z;
		dirty_ = true;
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
	
	public void setYaw(float angle)
	{
		
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
		Movable child = new Movable(name, this, scene_);
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
		parent_ = null;
		scene_ = null;
		
		if (localTransformation_!=null)
		{
			localTransformation_.release();
			localTransformation_ = null;
			
		}
		
		if (worldTransformationCache_!=null)
		{
			worldTransformationCache_.release();
			worldTransformationCache_ = null;
		}
			
		if (children_!=null)
		{
			for (Movable m : children_)
			{
				if (m!=null)
				{
					m.release();
				}
			}
			children_.clear();
			children_ = null;
		}

		if (drawable_!=null)
		{
			drawable_.release();
			drawable_ = null;
		}
	}
	
	/**
	 *@deprecated
	 */
	public void addChild(Movable child)
	{
		child.setParent(this);
		children_.add(child);
	}
	
	/**
	 *set drawable to this movable
	 *@param drawable drawable object
	 */
	public void setDrawable(Drawable drawable)
	{
		drawable_ = drawable;
		
		scene_.getRenderQueue().addDrawble(drawable);
		
		// force scene node write self transformation to drawable
		dirty_ = true;
		scene_.root().updateTransformation(Transformation.identity(), false);
	}
	
	public Drawable drawable()
	{
		return drawable_;
	}
	
	
	void setParent(Movable parent)
	{
		parent_ = parent;
	}

	/**
	 * Set transformation to Movable Object
	 * @throws CloneNotSupportedException 
	 */
	public synchronized void setTransformation(Transformation transformation)
	{
		// TODO use clone function
		localTransformation_.copy(transformation);
		dirty_ = true;
	}
	
	public void copyLocalTransformation(Transformation transformation)
	{
		transformation.copy(localTransformation_);
	}
	
	public Transformation getLocalTransformation()
	{
		return (Transformation)localTransformation_.clone();
	}
	
	public void getLocalTransformation(Transformation transtormation)
	{
		transtormation.copy(localTransformation_);
	}
	
	public synchronized void mulTransformation(Transformation transformation)
	{
		localTransformation_.mul(transformation);
		dirty_ = true;
	}
	
	/**
	 *Update world transformation by world = parent x local
	 *@param parentTransformation transformation from parent node
	 *@param parentDirty indicate if parent transformation is dirty since last update
	 */
	public synchronized void updateTransformation(Transformation parentTransformation, boolean parentDirty)
	{
		if (dirty_||parentDirty)
		{
			worldTransformationCache_.mul(parentTransformation, localTransformation_);
			
			if (drawable_ != null)
			{
				drawable_.setWorldTransformation(worldTransformationCache_);
			}
		}
			
		for (Movable m : children_)
		{
			m.updateTransformation(worldTransformationCache_, dirty_ | parentDirty);
		}
		
		dirty_ = false;
	}

	public String toString()
	{
		return name_;
	}
	
	public String name()
	{
		return name_;
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
	
	protected Transformation	localTransformation_;
	protected Transformation 	worldTransformationCache_;
	protected boolean			dirty_;
	
	final String				name_;
	
	List<Movable> 	children_;
	Movable			parent_;
	Drawable		drawable_;
	Scene			scene_;
}
