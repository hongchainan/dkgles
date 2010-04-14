package dkgles;

import android.opengl.Matrix;
import android.util.Log;

//import javax.microedition.khronos.opengles.GL10;


public class Camera extends Movable
{
	public static final int PERSPECTIVE = 0;
	
	public Camera(String name, Scene scene)
	{
		super(name, null, scene);
		_nearPlane = 0.1f;
		_farPlane = 10000.0f;
		_fov = 45.0f;
		_viewMatrix = new float[16];
		Matrix.setIdentityM(_viewMatrix, 0);
	}
	
	public void updateTransformation(Transformation parentTransformation, boolean parentDirty)
	{
		if (_dirty||parentDirty)
		{
			//Log.v(TAG, "dirty- update");
			_worldTransformationCache.mul(parentTransformation, _localTransformation);
			_worldTransformationCache.getViewMatrix(_viewMatrix);
		}
	}
	
	
	public final float nearPlane()
	{
		return _nearPlane;
	}
	
	
	public final void nearPlane(float np)
	{
		_nearPlane = np;
	}
	
	
	public final float farPlane()
	{
		return _farPlane;
	}
	
	
	public final void farPlane(float fp)
	{
		_farPlane = fp;
	}
	
	/**
	 * Get field of view
	 * @return
	 */
	public final float fov()
	{
		return _fov;
	}
	
	
	public final void fov(float f)
	{
		_fov = f;
	}
	
	public float[] viewMatrix()
	{
		return _viewMatrix;
	}
	
	private float[] _viewMatrix;
	private float _nearPlane;
	private float _farPlane;
	private float _fov;
	
	private final static String TAG = "Camera"; 
	
}
