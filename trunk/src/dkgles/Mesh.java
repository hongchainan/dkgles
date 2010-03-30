package dkgles;


import javax.microedition.khronos.opengles.GL10;

public class Mesh extends Drawable
{
	public Mesh(String name, int subMeshCount)
	{
		super(name);
		_SubMeshCount = subMeshCount;
		_subMeshes = new SubMesh[subMeshCount];
	}
	
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
	
	private SubMesh[]	_subMeshes;
	private int 		_SubMeshCount;
	
	
}