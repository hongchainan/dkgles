package dkgles;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;

public enum GLHost
{
	INSTANCE;
	
	public GL10 get()
	{
		return _gl;
	}
	
	public void set(GL10 gl)
	{
		_gl = gl;
	}
	
	public void setGLSurfaceView(GLSurfaceView surfaceView)
	{
		_glSurfaceView = surfaceView;
	}
	
	public void request(Runnable request)
	{
		_glSurfaceView.queueEvent(request);
		
		// wait here
	}
	
	public void requestAsync(Runnable request)
	{
		_glSurfaceView.queueEvent(request);
	}
	
	
	
	GLHost()
	{
		
	}
	
	GLSurfaceView	_glSurfaceView;
	GL10 _gl;
	
}