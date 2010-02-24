package dkgles;

import java.lang.String;

public abstract class Drawable
{
	
	String 		_name;
	Transformation 	_worldTransformation;
	
	public Drawable(String name)
	{
		_name = name;
	}
	
	
	public void setWorldTransformation(Transformation worldTransformation)
	{
		try
		{
			_worldTransformation = (Transformation)worldTransformation.clone();
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	public void render()
	{
		//DrawList.instance().add(this);
	}
	
	
	public abstract void renderImpl();
	
}