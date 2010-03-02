package dkgles;

//import java.lang.String;
import javax.microedition.khronos.opengles.GL10;

public abstract class Drawable
{
	
	String 		_name;
	Transformation 	_worldTransformation;
	
	public Drawable(String name)
	{
		_name = name;
		_visible = true;
	}
	
	
	public void show()
	{
		_visible = true;
	}
	
	public void hide()
	{
		_visible = false;
	}
	
	
	public boolean visible()
	{
		return _visible;
	}
	
	
	public void setWorldTransformation(Transformation worldTransformation)
	{
		_worldTransformation = worldTransformation;
	}
	
	
	public void render(GL10 gl)
	{
		if (!_visible)
			return;
		
		gl.glLoadMatrixf(_worldTransformation._matrix, 0);
		renderImpl(gl);
	}
	
	
	public abstract void renderImpl(GL10 gl);
	
	protected boolean _visible;
	
}