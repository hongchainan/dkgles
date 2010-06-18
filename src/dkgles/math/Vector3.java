package dkgles.math;

import android.util.FloatMath;

public class Vector3 implements Cloneable
{
	public float x;
	public float y;
	public float z;
	
	public Vector3()
	{
		x = 0.0f;
		y = 0.0f;
		z = 0.0f;
	}
	
	
	public Vector3(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;	
	}
	
	
	public Vector3 add(Vector3 rhs)
	{
		return new Vector3(
				this.x + rhs.x, this.y + rhs.y, this.z + rhs.z);
	}
	
	public Vector3 sub(Vector3 rhs)
	{
		return new Vector3(
				this.x - rhs.x, this.y - rhs.y, this.z - rhs.z);
	}
	
	public void normalize()
	{
		float l = length();
		
		x /= l;
		y /= l;
		z /= l;
	}
	
	public float length()
	{
		return FloatMath.sqrt(x*x + y*y + z*z);
	}
	
	
	public Vector3 mul(float fScaler)
	{
		return new Vector3(
				x*fScaler, y*fScaler, z*fScaler);
	}
	
	public void copy(Vector3 rhs)
	{
		x = rhs.x;
		y = rhs.y;
		z = rhs.z;
	}
	
	
	public void mulSelf(float fScaler)
	{
		x *= fScaler;
		y *= fScaler;
		z *= fScaler;
	}
	
	
	public Vector3 crossProduct(Vector3 rhs)
	{
		return new Vector3(
			y*rhs.z - z*rhs.y,
			z*rhs.x - x*rhs.z,
            x*rhs.y - y*rhs.x);
	}
	
	public float dot(Vector3 rhs)
	{
		return x*rhs.x + y*rhs.y + z*rhs.z;
	}
	
	public Object clone() 
	{
		return new Vector3(x, y, z);
	}
	
	
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("(" + x + ", " + y + ", " + z + ")" );
		return result.toString();
	}
	
	
	
	
}