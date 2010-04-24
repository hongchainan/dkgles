package dkgles;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;
import android.opengl.Matrix;

//import javax.microedition.khronos.opengles.GL10;


public class Camera extends Movable
{
	public static final int PERSPECTIVE = 0;
	
	public Camera(String name, Movable parent, Scene scene)
	{
		super(name, parent, scene);
		_nearPlane = 0.1f;
		_farPlane = 10000.0f;
		_fov = 45.0f;
		_aspectRatio = 1.5f;
		_viewMatrix = new float[16];
		_skyboxTransformation = new Transformation();

		if (scene!=null)
		{
			scene.bindCamera(this);
		}
		
		Matrix.setIdentityM(_viewMatrix, 0);
	}
	
	public void release()
	{
		super.release();
		_viewMatrix = null;
	}
	
	public void updateTransformation(Transformation parentTransformation, boolean parentDirty)
	{
		if (_dirty||parentDirty)
		{
			//Log.v(TAG, "dirty- update");
			_worldTransformationCache.mul(parentTransformation, _localTransformation);
			_worldTransformationCache.getViewMatrix(_viewMatrix);
			
			// write transformation to skybox?
			if (_drawable!=null)
			{
				_skyboxTransformation._matrix[12] = _worldTransformationCache._matrix[12];
				_skyboxTransformation._matrix[13] = _worldTransformationCache._matrix[13];
				_skyboxTransformation._matrix[14] = _worldTransformationCache._matrix[14];
				
				_drawable.setWorldTransformation(_skyboxTransformation);
			}
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
	
	
	public final void fov(float fov)
	{
		_fov = fov;
		_updateProjectionRQ.setCamera(this);
		GLHost.INSTANCE.request(_updateProjectionRQ);
	}
	
	public float aspectRatio()
	{
		return _aspectRatio;
	}
	
	public float[] viewMatrix()
	{
		return _viewMatrix;
	}
	
	Transformation _skyboxTransformation;
	
	float[] _viewMatrix;
	float _nearPlane;
	float _farPlane;
	float _fov;
	float _aspectRatio;
	
	private final static String TAG = "Camera";
	
	static GLUpdateProjectionRequest _updateProjectionRQ = new GLUpdateProjectionRequest();	
}

class GLUpdateProjectionRequest implements Runnable
{
	public GLUpdateProjectionRequest()
	{
	}
	
	public void setCamera(Camera camera)
	{
		_camera = camera;
	}
	
	public void run()
	{
		GL10 gl = GLHost.INSTANCE.get();
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluPerspective(gl, _camera.fov(), _camera.aspectRatio(), _camera.nearPlane(), _camera.farPlane());
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
	Camera _camera;
}


















