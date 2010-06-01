package dkgles;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

public class UVAnimation
{
	public UVAnimation()
	{
		_velocity 	= new Vector2(0.0f, 0.0f);
		_shift 		= new Vector2(0.0f, 0.0f);
		
		_animations.add(this);
	}
	
	public void release()
	{
		_animations.remove(this);
	}
	
	public void enable(boolean b)
	{
		_enable = b;
	}
	
	public boolean enable()
	{
		return _enable;
	}
	
	public void setVelocity(final Vector2 velocity)
	{
		_velocity = (Vector2)velocity.clone();
	}
	
	public void pre(GL10 gl)
	{
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glTranslatef(_velocity.x, _velocity.y, 0.0f);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		
	}
	
	public void operate(GL10 gl)
	{
		
	}
	
	public void post(GL10 gl)
	{
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glPopMatrix();	
		gl.glMatrixMode(GL10.GL_MODELVIEW);
	}
	
	public void update(long deltaTime)
	{
		_shift.x += _velocity.x * deltaTime;
		_shift.y += _velocity.y * deltaTime;
	}
	
	public static void updateAll(long deltaTime)
	{
		for (UVAnimation uv : _animations)
		{
			if (uv.enable()==true)
			{
				uv.update(deltaTime);
			}
		}
	}
	
	private Vector2 _velocity;
	private Vector2 _shift;
	private boolean _enable;
	
	static ArrayList<UVAnimation> _animations = new ArrayList<UVAnimation>();
}