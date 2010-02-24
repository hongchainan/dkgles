package dkgles;

import dkgles.math.Vector3;
import dkgles.math.Quaternion;


public class Transformation implements Cloneable
{
	private Vector3 	_translatation;
	private Quaternion 	_orientation;
	
	public Transformation()
	{
		_translatation = new Vector3(0.0f, 0.0f, 0.0f);
		_orientation   = new Quaternion();
	}
	
	
	public Transformation(Quaternion orientation, Vector3 translatation)
	{
		_orientation = orientation;
		_translatation = translatation;
	}
	
	
	public void rotateLocal(float angle, float x, float y, float z)
	{
		Quaternion ort = new Quaternion();
		ort.fromAngleAxis(angle, x, y, z);
		
		_orientation = _orientation.mul(ort);
	}
	
	
	public void rotateGlobal(float angle, float x, float y, float z)
	{
		Quaternion ort = new Quaternion();
		ort.fromAngleAxis(angle, x, y, z);
		
		_orientation = ort.mul(_orientation);
	}
	
	
	public void translateLocal(float x, float y, float z)
	{
		Vector3 xAxis = _orientation.xAxis();
		Vector3 yAxis = _orientation.yAxis();
		Vector3 zAxis = _orientation.zAxis();
		
		xAxis.mulSelf(x);
		yAxis.mulSelf(y);
		zAxis.mulSelf(z);
		
		_translatation._x += (xAxis._x + yAxis._x + zAxis._x); 
		_translatation._y += (xAxis._y + yAxis._y + zAxis._y); 
		_translatation._z += (xAxis._z + yAxis._z + zAxis._z); 
	}
	
	
	public void translateGlobal(float x, float y, float z)
	{
		_translatation._x += x;
		_translatation._y += y;
		_translatation._z += z;		
	}
	
	
	public Transformation mul(Transformation rhs)
	{
		Quaternion q = _orientation.mul(rhs._orientation);
		Vector3    t = _orientation.mul(rhs._translatation).add(_translatation);
		
		return new Transformation(q, t);
	}
	
	public Object clone() throws CloneNotSupportedException 
	{
		Transformation t 	= (Transformation)super.clone();
		t._orientation 		= (Quaternion)_orientation.clone();
		t._translatation 	= (Vector3)_translatation.clone();
		
		return t;
	}
}