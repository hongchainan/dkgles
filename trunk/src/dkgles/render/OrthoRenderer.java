package dkgles.render;

import javax.microedition.khronos.opengles.GL10;

public enum OrthoRenderer
{
	INSTANCE;
	
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
	
	public final void beginRender(GL10 gl)
	{
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glOrthof(-.75f, .75f, -.5f, .5f, _nearPlane, _farPlane);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
	public void endRender(GL10 gl)
	{
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL10.GL_MODELVIEW);
	}
	
	
	public void setRenderQueue(RenderQueue q)
	{
		_renderQueue = q; 
	}
	
	OrthoRenderer()
	{
		_nearPlane 	= 1.0f;
		_farPlane 	= 100.0f;
	}
	
	RenderQueue	_renderQueue;
	float 		_nearPlane;
	float 		_farPlane;
}