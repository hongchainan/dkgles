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
	 *@param numSubMeshes total account of sub meshes.
	 */
	public Mesh(String name, int numSubMeshes)
	{
		name_ 			= name;
		numSubMeshes_ 	= numSubMeshes;
		subMeshes_ 		= new SubMesh[numSubMeshes];
	}

	public void release()
	{
		//super.release();
		if (subMeshes_!=null)
		{
			for (int i=0;i<numSubMeshes_;i++)
			{
				subMeshes_[i].release();
				subMeshes_[i] = null;
			}
			subMeshes_ = null;
		}
	}
	
	/**
	 * Set Material by given index
	 * @param index
	 * @param material
	 */
	public void setMaterial(int index, Material material)
	{
		subMeshes_[index].setMaterial(material);
	}
	
	public Material getMaterial(short index)
	{
		return subMeshes_[index].getMaterial();
	}
	
	public int numSubMeshes()
	{
		return numSubMeshes_;
	}
	
	/**
	 * Set sub mesh by index
	 */
	protected void setSubMesh(int index, SubMesh sm)
	{
		if (index<0||index>=numSubMeshes_)
		{
			return;
		}
		
		subMeshes_[index] = sm;
	}
	
	public void renderImpl(GL10 gl)
	{
		for (int i=0;i<numSubMeshes_;i++)
		{
			subMeshes_[i].renderImpl(gl);
		}
	}
	
	public String name()
	{
		return name_;
	}
	
	protected String name_;
	
	private SubMesh[]	subMeshes_;
	private int 		numSubMeshes_;		
	
	public static DummyMesh getDummy()
	{
		return dummyMesh_;
	}
	
	private static DummyMesh dummyMesh_ = new DummyMesh();
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
