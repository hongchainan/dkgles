package dkgles.primitive;

import dkgles.Material;
import dkgles.Mesh;
import dkgles.SubMesh;


public class Rectangle extends Mesh
{
	public Rectangle(String name, float width, float height, Material material)
	{
		super(name, 1);
		
		_width 	= width;
		_height = height;
		_subMesh = new SubMesh("SubMesh_" + name, SubMesh.DRAW_ARRAY, material);
		setSubMesh(0, _subMesh);
		
		initVerticesBuffer();
		initTexcoordsBuffer();
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
		
		_subMesh.setVertices(vertices);	                             
	}
	
	
	private void initTexcoordsBuffer()
	{
		float[] texcoords = new float[]{
				0.0f, 0.0f,
				1.0f, 0.0f,
				0.0f, 1.0f,
				1.0f, 1.0f
		};
		
		_subMesh.setTexcoords(texcoords);
	}
	
	protected SubMesh	_subMesh;
	protected float 	_width;
	protected float 	_height;
}
