package dkgles;

//import dkgles.math.Vector3;
//import dkgles.math.Quaternion;
import android.opengl.Matrix;

public class Transformation implements Cloneable
{
	//private Vector3 	_translatation;
	//private Quaternion 	_orientation;
	
	public float[] matrix; 
	
	public Transformation()
	{
		matrix = new float[16];
		setIdentity();
	}
	
	public void setIdentity()
	{
		Matrix.setIdentityM(matrix, 0);
	}
	
	/**
	 * 
	 * @return
	 */
	public static Transformation identity()
	{
		if (_identity==null)
		{
			_identity = new Transformation(); 
		}	
		return _identity;
	}
	
	public void release()
	{
		matrix = null;
	}
	
	public void rotateInLocalSpace(float angle, float x, float y, float z)
	{
		float tx = matrix[12];
		float ty = matrix[13];
		float tz = matrix[14];
		
		matrix[12] = 0.0f;
		matrix[13] = 0.0f;
		matrix[14] = 0.0f;
		
		Matrix.rotateM(matrix, 0, angle, x, y, z);
		
		matrix[12] = tx;
		matrix[13] = ty;
		matrix[14] = tz;
	}
	
	public void rotateInParentSpace(float angle, float x, float y, float z)
	{
		float[] lhs = new float[16];
		float[] rhs = new float[16];
		
		Matrix.setIdentityM(lhs, 0);
		Matrix.rotateM(lhs, 0, angle, x, y, z);
		
		System.arraycopy(matrix, 0, rhs, 0, 16);
		
		Matrix.multiplyMM(matrix, 0, lhs, 0, rhs, 0);	
	}
	
	public void translateInLocalSpace(float x, float y, float z)
	{
		float tx = x*matrix[0] + y*matrix[4] + z*matrix[8];
		float ty = x*matrix[1] + y*matrix[5] + z*matrix[9];
		float tz = x*matrix[2] + y*matrix[6] + z*matrix[10];
		
		matrix[12] += tx;
		matrix[13] += ty;
		matrix[14] += tz;
	}
	
	
	public void translateInParentSpace(float x, float y, float z)
	{
		matrix[12] += x;
		matrix[13] += y;
		matrix[14] += z;		
	}
	
	public void getTranslation(float[] t)
	{
		t[0] = matrix[12];
		t[1] = matrix[13];
		t[2] = matrix[14];
	}
	
	
	//public Transformation mul(Transformation rhs)
	//{
		//Quaternion q = _orientation.mul(rhs._orientation);
		//Vector3    t = _orientation.mul(rhs._translatation).add(_translatation);
		
		//return new Transformation(q, t);
	//}
	
	public void mul(Transformation rhs)
	{
		Matrix.multiplyMM(matrix, 0, matrix, 0, rhs.matrix, 0);
	}
	
	public final void mul(Transformation lhs, Transformation rhs)
	{
		Matrix.multiplyMM(matrix, 0, lhs.matrix, 0, rhs.matrix, 0);
	}
	
	
	public void getViewMatrix(float[] view)
	{
		Matrix.invertM(view, 0, matrix, 0);
	}
	
	public void copy(Transformation t)
	{
		for (int i=0;i<16;i++)
		{
			matrix[i] = t.matrix[i];
		}
	}
	
	public Object clone() 
	{
		Transformation t = new Transformation();

		for (int i=0;i<16;i++)
		{
			t.matrix[i] = matrix[i];
		}		
		
		return t;
	}
	
	private static Transformation _identity;
	
}
