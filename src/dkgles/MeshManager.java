package dkgles;

public enum MeshManager
{
	INSTANCE;
	
	public interface ICreator
	{
		public Mesh create(String[] args);
	}
	
	public static final int PRIM_RECTANGLE;
	
	public void create(int type, String[] args)
	{
		
	}
	
	public Mesh get(int id)
	{
		return _meshes[id];
	}
	
	public void destroy(ind id)
	{
		if (_meshes[id]!=null)
		{
			_meshes[id].release();
			_meshes[id] = null;
		}
	}
	
	public int register(Mesh mesh)
	{
		for (int i=0;i<MAX_MESHES;i++)
		{
			if ( _meshes[i]==null)
			{
				_meshes[i] = mesh;
				return i;
			}
		}
		
		return -1;
	}
	
	MeshManager()
	{
		_meshes = new Mesh[MAX_MESHES];
	}
	
	public void release()
	{
		if (_meshes!=null)
		{
			// TODO
			_meshes = null;
		}
	}
	
	Mesh[]	_meshes;
	static final MAX_MESHES = 16;
}
