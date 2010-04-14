package dkgles;


import javax.microedition.khronos.opengles.GL10;

/**
 *An implementation of Drawable
 *@author doki lin
 */
public class Mesh extends Drawable
{
	/**
	 *@param name a human readable string for debugging
	 &@param subMeshCount total account of sub meshes.
	 */
	public Mesh(String name, int subMeshCount)
	{
		super(name);
		_SubMeshCount = subMeshCount;
		_subMeshes = new SubMesh[subMeshCount];
	}

	public void release()
	{
		super.release();
		
	}
	
	/**
	 *Set sub mesh by index
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
	
	SubMesh[]	_subMeshes;
	int 		_SubMeshCount;
		
}
