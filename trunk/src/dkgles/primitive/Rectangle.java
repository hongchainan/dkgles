package dkgles.primitive;

import javax.microedition.khronos.opengles.GL10;

import dkgles.Material;
import dkgles.Mesh;

public class Rectangle extends Mesh
{
	public Rectangle(String name, float width, float height)
	{
		super(name);
		
		_width = width;
		_height = height;
		
		initVerticesBuffer();
		initTexcoordsBuffer();
	}
	
	
	public void renderImpl(GL10 gl)
	{
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _verticesBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, _texcoordsBuffer);
		
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0,4); 
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
	}
	
	private void initVerticesBuffer()
	{
		float hx = _width / 2;
		float hy = _height / 2;
		
		float[] vertices = new float[]{
				-hx,  hy, 0,
				 hx,  hy, 0,
				-hx, -hy, 0,
				 hx, -hy, 0
		};
		
		setVertices(vertices);	                             
	}
	
	
	private void initTexcoordsBuffer()
	{
		float[] texcoords = new float[]{
				0.0f, 1.0f,
				1.0f, 1.0f,
				0.0f, 0.0f,
				1.0f, 0.0f
		};
		
		setTexcoords(texcoords);
	}
	
	
	
	protected float _width;
	protected float _height;
}
