package dkgles;

import java.util.ArrayList;

public class Immovable
{
	public Immovable(String name, Transformation transformation, Scene scene)
	{
		_name 	= name;
		_scene 	= scene;
		_transformation = transformation;
	}
	
	public void release()
	{
		_drawable = null;
		_name = null;
		
		if (_transformation!=null)
		{
			_transformation.release();
		}
		
		if (_movables!=null)
		{
			_movables.clear();
			_movables = null;
		}
	}
	
	public void setDrawable(Drawable drawable)
	{
		_drawable = drawable;
		_drawable.setWorldTransformation(_transformation);
		_scene.getRenderQueue().addDrawble(drawable);
	}
	
	public Drawable drawable()
	{
		return _drawable;
	}
	
	public String name()
	{
		return _name;
	}
	
	public void addMovable(Movable movable)
	{
		if (_movables==null)
		{
			_movables = new ArrayList<Movable>();
		}
		
		_movables.add(movable);
	}
	
	Drawable				_drawable;
	String					_name;
	Scene					_scene;
	final Transformation	_transformation;
	ArrayList<Movable>		_movables;
}