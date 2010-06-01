package dkgles;

public class Vector2 implements Cloneable
{
	public Vector2()
	{
		x = y = 0.0f;
	}
	
	public Vector2(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Object clone()
	{
		Vector2 target = new Vector2(x, y);
		
		return target;
	}
	
	public float x;
	public float y;
}
