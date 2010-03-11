package dkgles.primitive;

import javax.microedition.khronos.opengles.GL10;

import dkgles.Material;
import dkgles.Mesh;

public class Rectangle extends Mesh
{
	public Rectangle(String name, float width, float height, Material material)
	{
		super(name);
		
		_width = width;
		_height = height;
		_material = material;
		
		initVerticesBuffer();
		initTexcoordsBuffer();
	}
	
	
	public void renderImpl(GL10 gl)
	{
		if (_material != null)
		{
			_material.apply(gl);
		}
		renderByDrawArray(gl, 4);
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
				0.0f, 0.0f,
				1.0f, 0.0f,
				0.0f, 1.0f,
				1.0f, 1.0f
		};
		
		setTexcoords(texcoords);
	}
	
	
	protected Material _material;
	protected float _width;
	protected float _height;
}
