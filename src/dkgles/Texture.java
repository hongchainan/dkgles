package dkgles;

import javax.microedition.khronos.opengles.GL10;


public class Texture implements Resource
{
	public Texture(int id)
	{
		_id = new int[1];
		_id[0] = id;
	}
	
	public void acquire()
	{
		
	}
	
	
	public void bind(GL10 gl)
	{
		gl.glBindTexture(GL10.GL_TEXTURE_2D, _id[0]);
	}
	
	
	public void release(GL10 gl)
	{
		gl.glDeleteTextures(1, _id, 0);
	}
		
	private int[] _id;

	public void release() {
		// TODO Auto-generated method stub
		
	}
}
