package dkgles.android.wrapper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

public class SurfaceRenderer implements Renderer
{
	public SurfaceRenderer(Context context)
	{
		_context = context;
	}
	
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		initGLConfig(gl);
	}
	
	public void onDrawFrame(GL10 gl)
	{
		
	}
	
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
	
	}
	
	private void initGLConfig(GL10 gl)
	{
		// Set the background color to black ( rgba ).
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		// Enable Smooth Shadiangleng, default not really needed.
		gl.glShadeModel(GL10.GL_SMOOTH);
		// Depth buffer setup.
		gl.glClearDepthf(1.0f);
		// Enables depth testing.
		gl.glEnable(GL10.GL_DEPTH_TEST);
		// The type of depth testing to do.
		gl.glDepthFunc(GL10.GL_LEQUAL);
		// Really nice perspective calculations.
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		
		//
		gl.glFrontFace(GL10.GL_CCW);
		gl.glEnable(GL10.GL_CULL_FACE);
	}
	
	private Context _context;
}