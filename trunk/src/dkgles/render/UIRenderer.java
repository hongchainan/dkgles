package dkgles.render;

import javax.microedition.khronos.opengles.GL10;

import dkgles.Drawable;
import dkgles.Texture;

public final class UIRenderer
{
	public static UIRenderer instance()
	{
		if (_instance==null)
		{
			_instance = new UIRenderer();
		}
		
		return _instance;
	}
	
	
	public void onSize(float width, float height)
	{
		_width = width;
		_height = height;
	}
	
	
	public final void render(GL10 gl)
	{
		beginRender(gl);
		renderImpl(gl);
		endRender(gl);
	}
	
	private final void renderImpl(GL10 gl)
	{
		if (_drawable != null)
		{
			gl.glEnable(GL10.GL_TEXTURE_2D);
			_texture.bind(gl);
			_drawable.render(gl);
			gl.glDisable(GL10.GL_TEXTURE_2D);
		}
		
		//for (Drawable drawable : _drawables)
		//{
		//	if (drawable != null)
		//	{
		//		drawable.render(gl);
		//	}
		//}
	}
	
	private final void beginRender(GL10 gl)
	{
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glPushMatrix();
		
		gl.glLoadIdentity();
		gl.glOrthof(-_width/2, _width/2, -_height/2, _height/2, _nearPlane, _farPlane);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
	private void endRender(GL10 gl)
	{
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL10.GL_MODELVIEW);
	}
	
	
	public void addDrawable(Drawable drawable, Texture texture)
	{
		_texture = texture;
		_drawable = drawable;
	}
	
	
	private UIRenderer()
	{
		_nearPlane 	= 1.0f;
		_farPlane 	= 100.0f;
		
		//_drawables = new ArrayList<Drawable>();
	}
	
	private Texture _texture;
	
	private float _width;
	private float _height;
	private float _nearPlane;
	private float _farPlane;
	
	
	
	//private ArrayList<Drawable> _drawables;
	private Drawable _drawable;
	
	private static UIRenderer _instance;
	
}