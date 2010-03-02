package dkgles;

//import dkgles.math.Vector3;
//import dkgles.math.Quaternion;
import android.opengl.Matrix;

public class Transformation implements Cloneable
{
	//private Vector3 	_translatation;
	//private Quaternion 	_orientation;
	
	public float[] _matrix; 
	
	public Transformation()
	{
		//_translatation = new Vector3(0.0f, 0.0f, 0.0f);
		//_orientation   = new Quaternion();
		_matrix = new float[16];
		Matrix.setIdentityM(_matrix, 0);
	}
	
	
	//public Transformation(Quaternion orientation, Vector3 translatation)
	//{
		//_orientation = orientation;
		//_translatation = translatation;
	//}
	
	
	public void rotateInLocalSpace(float angle, float x, float y, float z)
	{
		float tx = _matrix[12];
		float ty = _matrix[13];
		float tz = _matrix[14];
		
		_matrix[12] = 0.0f;
		_matrix[13] = 0.0f;
		_matrix[14] = 0.0f;
		
		Matrix.rotateM(_matrix, 0, angle, x, y, z);
		
		_matrix[12] = tx;
		_matrix[13] = ty;
		_matrix[14] = tz;
	}
	
	public void rotateInParentSpace(float angle, float x, float y, float z)
	{
		float[] lhs = new float[16];
		float[] rhs = new float[16];
		
		Matrix.setIdentityM(lhs, 0);
		Matrix.rotateM(lhs, 0, angle, x, y, z);
		
		System.arraycopy(_matrix, 0, rhs, 0, 16);
		
		Matrix.multiplyMM(_matrix, 0, lhs, 0, rhs, 0);
		
	}
	
	
	public void translateInLocalSpace(float x, float y, float z)
	{
		float tx = x*_matrix[0] + y*_matrix[4] + z*_matrix[8];
		float ty = x*_matrix[1] + y*_matrix[5] + z*_matrix[9];
		float tz = x*_matrix[2] + y*_matrix[6] + z*_matrix[10];
		
		_matrix[12] += tx;
		_matrix[13] += ty;
		_matrix[14] += tz;
	}
	
	
	public void translateInParentSpace(float x, float y, float z)
	{
		_matrix[12] += x;
		_matrix[13] += y;
		_matrix[14] += z;		
	}
	
	public void getTranslation(float[] t)
	{
		t[0] = _matrix[12];
		t[1] = _matrix[13];
		t[2] = _matrix[14];
	}
	
	
	//public Transformation mul(Transformation rhs)
	//{
		//Quaternion q = _orientation.mul(rhs._orientation);
		//Vector3    t = _orientation.mul(rhs._translatation).add(_translatation);
		
		//return new Transformation(q, t);
	//}
	
	public final void mul(Transformation lhs, Transformation rhs)
	{
		Matrix.multiplyMM(_matrix, 0, lhs._matrix, 0, rhs._matrix, 0);
	}
	
	
	public void getViewMatrix(float[] m16)
	{
		
	}
	
	public Object clone() throws CloneNotSupportedException 
	{
		Transformation t 	= (Transformation)super.clone();
		return t;
	}
}