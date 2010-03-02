package dkgles;

//import javax.microedition.khronos.opengles.GL10;


public class Camera extends Movable
{
	
	public static final int PERSPECTIVE = 0;
	
	private float _nearPlane;
	private float _farPlane;
	private float _fov;
	
	public Camera(String name)
	{
		super(name);
		_nearPlane = 0.1f;
		_farPlane = 10000.0f;
		_fov = 45.0f;
	}
	
	
	public void updateTransformation(Transformation parentTransformation, boolean parentDirty)
	{
		if (_dirty||parentDirty)
		{
			_worldTransformationCache.mul(parentTransformation, _localTransformation);
			
		}
	}
	
	
	public float nearPlane()
	{
		return _nearPlane;
	}
	
	
	public void nearPlane(float np)
	{
		_nearPlane = np;
	}
	
	
	public float farPlane()
	{
		return _farPlane;
	}
	
	
	public void farPlane(float fp)
	{
		_farPlane = fp;
	}
	
	
	public float fov()
	{
		return _fov;
	}
	
	
	public void fov(float f)
	{
		_fov = f;
	}
	
	
	//public FloatBuffer getViewMatrix()
	//{
	//	FloatBuffer fb = FloatBuffer.allocate(16);
	//}
	
	
	
}