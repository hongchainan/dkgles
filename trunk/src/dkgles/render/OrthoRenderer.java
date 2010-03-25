package dkgles.render;

import javax.microedition.khronos.opengles.GL10;

import dkgles.Drawable;
import dkgles.Texture;

public final class OrthoRenderer
{
	public static OrthoRenderer instance()
	{
		if (_instance==null)
		{
			_instance = new OrthoRenderer();
		}	
		return _instance;
	}
	
	
	public boolean initialized()
	{
		return _initialized;
	}
	
	
	public void initialize(GL10 gl)
	{
		if (_initialized)
		{
			return;
		}
		
		_gl = gl;
		_initialized = true;
	}
	
	public void render(GL10 gl, RenderQueue renderQueue)
	{
		beginRender(gl);
		renderQueue.render(gl);
		endRender(gl);
	}
	
	public final void render(GL10 gl)
	{
		beginRender(gl);
		renderImpl(gl);
		endRender(gl);
	}
	
	private final void renderImpl(GL10 gl)
	{
		_renderQueue.render(gl);
	}
	
	private final void beginRender(GL10 gl)
	{
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glOrthof(-1.0f, 1.0f, -1.0f, 1.0f, 1, 100);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
	private void endRender(GL10 gl)
	{
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL10.GL_MODELVIEW);
	}
	
	
	public void setRenderQueue(RenderQueue q)
	{
		_renderQueue = q; 
	}
	
	private OrthoRenderer()
	{
		_nearPlane 	= 1.0f;
		_farPlane 	= 100.0f;
		_initialized = false;
		
		//_drawables = new ArrayList<Drawable>();
	}
	
	private RenderQueue	_renderQueue;
	private GL10	_gl;
	private boolean _initialized;
	private Texture _texture;
	
	private float _nearPlane;
	private float _farPlane;
	
	
	//private ArrayList<Drawable> _drawables;
	private Drawable _drawable;
	private static OrthoRenderer _instance;
	
}