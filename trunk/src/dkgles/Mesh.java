package dkgles;


import javax.microedition.khronos.opengles.GL10;

/**
 *An implementation of Drawable
 *@author doki lin
 */
public class Mesh
{
	/**
	 *@param name a human readable string for debugging
	 *@param subMeshCount total account of sub meshes.
	 */
	public Mesh(String name, int subMeshCount)
	{
		_name = name;
		_SubMeshCount = subMeshCount;
		_subMeshes = new SubMesh[subMeshCount];
	}

	public void release()
	{
		//super.release();
		if (_subMeshes!=null)
		{
			for (int i=0;i<_SubMeshCount;i++)
			{
				_subMeshes[i].release();
				_subMeshes[i] = null;
			}
			_subMeshes = null;
		}
	}
	
	/**
	 * Set Material by given index
	 * @param index
	 * @param material
	 */
	public void setMaterial(int index, Material material)
	{
		_subMeshes[index].setMaterial(material);
	}
	
	/**
	 * Set sub mesh by index
	 */
	protected void setSubMesh(int index, SubMesh sm)
	{
		_subMeshes[index] = sm;
	}
	
	public void renderImpl(GL10 gl)
	{
		for (int i=0;i<_SubMeshCount;i++)
		{
			_subMeshes[i].renderImpl(gl);
		}
	}
	
	public String name()
	{
		return _name;
	}
	
	String _name;
	
	SubMesh[]	_subMeshes;
	int 		_SubMeshCount;		
	
	public static DummyMesh getDummy()
	{
		return _dummy;
	}
	
	static DummyMesh _dummy = new DummyMesh();
}

class DummyMesh extends Mesh
{
	public DummyMesh()
	{
		super("DummyMesh", 0);
		// TODO Auto-generated constructor stub
	}

	public void renderImpl(GL10 gl)
	{}
}
