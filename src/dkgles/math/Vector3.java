package dkgles.math;

public class Vector3 implements Cloneable
{
	public float _x;
	public float _y;
	public float _z;
	
	
	public Vector3(float x, float y, float z)
	{
		_x = x;
		_y = y;
		_z = z;	
	}
	
	
	public Vector3 add(Vector3 rhs)
	{
		return new Vector3(
			_x + rhs._x, _y + rhs._y, _z + rhs._z);
		
	}
	
	
	public Vector3 mul(float fScaler)
	{
		return new Vector3(
				_x*fScaler, _y*fScaler, _z*fScaler);
	}
	
	
	public void mulSelf(float fScaler)
	{
		_x *= fScaler;
		_y *= fScaler;
		_z *= fScaler;
	}
	
	
	public Vector3 crossProduct(Vector3 rhs)
	{
		return new Vector3(
			_y*rhs._z - _z*rhs._y,
			_z*rhs._x - _x*rhs._z,
            _x*rhs._y - _y*rhs._x);
	}
	
	public Object clone() throws CloneNotSupportedException 
	{
		return super.clone();
	}
	
	
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("(" + _x + ", " + _y + ", " + _z + ")" );
		return result.toString();
	}
	
	
	
	
}