package dkgles;

public class Skybox extends Mesh
{
	public Skybox(String name, Material material, float size)
	{
		super(name, 1);
		
		// draw skybox first
		// groupID(0);
		
		_size = size;
		_subMesh = new SubMesh("SubMesh_" + name, SubMesh.DRAW_ARRAY, material);
		setSubMesh(0, _subMesh);
		
		initVerticesBuffer();
		initTexcoordBuffer();
		initFaceList();
	}
	
	public void release()
	{
		super.release();
		if (_subMesh!=null)
		{
			_subMesh.release();
			_subMesh = null;
		}
	}
	
	void initFaceList()
	{
		int[] flist = new int[]{
			0,	// 	+X
			4,	// 	-X
			8, 	// 	+Y
			12,	//	_Y
			//16,	//	+Z
			20, //	-Z
		};
		
		_subMesh.setFaceList(flist);
	}
	
	void initVerticesBuffer()
	{
		float x = _size/2;
        float y = _size/2;
        float z = _size/2;
		
		float[] vertices = new float[]{
				// +X
		         x,  y, -z,
		         x,  y,  z,
		         x, -y, -z,
		         x, -y,  z,

		        // -X
		        -x,  y,  z,
		        -x,  y, -z,
		        -x, -y,  z,
		        -x, -y, -z,

		        // +Y
		        -x,  y,  z,
		         x,  y,  z,
		        -x,  y, -z,
		         x,  y, -z,

		        // -Y
		        -x, -y, -z,
		         x, -y, -z,
		        -x, -y,  z,
		         x, -y,  z,

		        // +Z
		        -x, -y, z,
		        x, -y, z,
		        x, y, z,
		        -x, y, z,

		        // -Z
		        -x,  y, -z,
		         x,  y, -z,
		        -x, -y, -z,
		         x, -y, -z
		};
		
		_subMesh.setVertices(vertices);
	}
	
	void initTexcoordBuffer()
	{
		float[] texcoords = new float[]{
				// +X
	            .66f, .25f,
	            1.0f, .25f,
	            .66f, .50f,
	            1.0f, .50f,

	            // -X
	            0.0f, .25f,
	            .33f, .25f,
	            0.0f, .50f,
	            .33f, .50f,

	            // +Y
	           .33f, .0f,
	           .66f, .0f,
	           .33f, .25f,
	           .66f, .25f,

	            // -Y
	           .66f, .75f,
	           .33f, .75f,
	           .33f, .5f,
	           .66f, .5f,

	            // +Z
	           .33f, .75f,
	           .66f, .75f,
	           .66f, 1.0f,
	           .33f, 1.0f,

	            // -Z
	           .33f, .25f,
	           .66f, .25f,
	           .33f, .50f,
	           .66f, .50f
		};
		
		_subMesh.setTexcoords(texcoords);
	}
	
	protected SubMesh	_subMesh;
	protected float 	_size;
	
}