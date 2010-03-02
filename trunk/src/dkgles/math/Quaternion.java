package dkgles.math;

import android.util.FloatMath;
//On one platform, FloatMath.sqrt(100) executes in one third of the time required by java.lang.Math.sqrt(100).

public class Quaternion implements Cloneable
{
	public float _w;
	public float _x;
	public float _y;
	public float _z;
	
	public Quaternion()
	{
		_w = 1.0f;
		_x = _y = _z = 0.0f;	
	}
	
	public Quaternion(float w, float x, float y, float z)
	{
		_w = w;
		_x = x;
		_y = y;
		_z = z;
	}
	
	
	public void fromAngleAxis(float angle, float x, float y, float z)
	{
		// assert:  axis[] is unit length
        //
        // The quaternion representing the rotation is
        // q = cos(A/2)+sin(A/2)*(x*i+y*j+z*k)

        float halfAngle = 0.5f*angle;
        float fSin = FloatMath.sin(halfAngle);
        _w = FloatMath.cos(halfAngle);
        _x = fSin*x;
        _y = fSin*y;
        _z = fSin*z;
	}
	
	
	public Quaternion mul(Quaternion rhs)
	{
		// NOTE:  Multiplication is not generally commutative, so in most
        // cases p*q != q*p.

        return new Quaternion
        (
        		_w * rhs._w - _x * rhs._x - _y * rhs._y - _z * rhs._z,
            	_w * rhs._x + _x * rhs._w + _y * rhs._z - _z * rhs._y,
            	_w * rhs._y + _y * rhs._w + _z * rhs._x - _x * rhs._z,
            	_w * rhs._z + _z * rhs._w + _x * rhs._y - _y * rhs._x
        );
	}
	
	
	public Vector3 xAxis()
	{
		//Real fTx  = 2.0*x;
        	float fTy  = 2.0f*_y;
        	float fTz  = 2.0f*_z;
        	float fTwy = fTy*_w;
        	float fTwz = fTz*_w;
        	float fTxy = fTy*_x;
        	float fTxz = fTz*_x;
        	float fTyy = fTy*_y;
        	float fTzz = fTz*_z;

        	return new Vector3(1.0f-(fTyy+fTzz), fTxy+fTwz, fTxz-fTwy);
	}
	
	
    	public Vector3 yAxis()
    	{
        	float fTx  = 2.0f*_x;
        	float fTy  = 2.0f*_y;
        	float fTz  = 2.0f*_z;
        	float fTwx = fTx*_w;
        	float fTwz = fTz*_w;
        	float fTxx = fTx*_x;
        	float fTxy = fTy*_x;
        	float fTyz = fTz*_y;
        	float fTzz = fTz*_z;

        	return new Vector3(fTxy-fTwz, 1.0f-(fTxx+fTzz), fTyz+fTwx);
    	}
    	
    	
    	public Vector3 zAxis()
    	{
        	float fTx  = 2.0f*_x;
        	float fTy  = 2.0f*_y;
        	float fTz  = 2.0f*_z;
        	float fTwx = fTx*_w;
        	float fTwy = fTy*_w;
        	float fTxx = fTx*_x;
        	float fTxz = fTz*_x;
        	float fTyy = fTy*_y;
        	float fTyz = fTz*_y;

        	return new Vector3(fTxz+fTwy, fTyz-fTwx, 1.0f-(fTxx+fTyy));
    	}
	
	
	
	
	//public Quaternion operator*=(Quaternion rhs)
	//{	
	//}
	
	
	public Vector3 mul(Vector3 rhs)
	{
		// nVidia SDK implementation
        Vector3 qvec = new Vector3(_x, _y, _z);
        Vector3 uv = qvec.crossProduct(rhs);
        Vector3 uuv = qvec.crossProduct(uv);
        uv = uv.mul(2.0f * _w);
        uuv = uuv.mul(2.0f);

        return rhs.add(uv).add(uuv);
	}
	
	
	public Object clone() throws CloneNotSupportedException 
	{
		return super.clone();
	}
	
	
}
