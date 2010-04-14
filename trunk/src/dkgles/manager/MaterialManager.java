package dkgles.manager;

import java.util.HashMap;

import android.util.Log;
import dkgles.Material;
import dkgles.Texture;

/**
 *Usage:
 *	int texID = R.drawable.my_texture
 *	int mid = MaterialManager.instance().create("MAT_NAME", "TEX_NAME", texID);
 *	Material m = MaterialManager.instance().get(mid);
 *	MaterialManager.instance().destroy(mid);
 */
public class MaterialManager implements TextureManager.EventListener
{
	
	public final static int BLACK_ID = 0;
	
	
	/**
	 * Create a material and return material ID
	 *@param name a human readable string for debugging
	 *@param texture A texture that can bind to or a null reference if you need flat color only.
	 */
	public int create(String name, Texture texture)
	{
		Material material = new Material(name);
		m.bindTexture(texture);
		return register(material);
	}

	/**
	 *An utility let you create material and texture at sametime
	 *This method is equal to:
	 *	TextureManager.instance().create(texName, rscId);
	 *	MaterialManager.instance().create(matName, TextureManager.instance().get(rscId));
	 *@param name A human readable name for material
	 *@param texName name for texture
	 *@param rscID resource ID for texture
	 */
	public int create(final String name, final String texName, final int rscId)
	{
		Material material = new Material(name);
		_waitedTexs.put(texName, id);
		TextureManager.instance().create(texName, rscId, this);
		
		return register(material);
	}

	/**
	 *Register a material object and return its ID
	 */
	public int register(Material material)
	{
		for (int i=0;i<MAX_MATERIALS;i++)
		{
			if (_materials[i]==null)
			{
				_materials[i] = material;
				return i;
			}
		}
		return -1;
	}

	/**
	 *Get material by given ID
	 */
	public Material get(int mid)
	{
		return _materials[mid];
	}

	/**
	 *Destroy material by given id
	 */
	public void destroy(int mid)
	{
		if (_materials[mid]!=null)
		{
			_materials[mid].release();
			_materials[mid] = null;
		}
	}

	/**
	 *Release material manager
	 */
	public void release()
	{
		for (Material m : _materials)
		{
			if (m!=null)
			{
				m.release();
			}
		}

		_materials = null;
	}
	
	/**
	 *@deprecated
	 */
	private int findKey()
	{
		//int id = 0;
		//while (_materials.containsKey(id))
		//{
		//	id++;
		//}
		
		return 0;
	}
	
	
	/**
	 *@see TextureManager.EventListener
	 *@see TextureManager#create
	 */
	@override
	public void onTextureDeleted(String name, int rscId)
	{
		Log.v(TAG, "onTextureDeleted:" + name);
	}

	/**
	 *@see TextureManager.EventListener
	 *@see TextureManager#create
	 */
	@override
	public void onTextureLoaded(String name, int rscId)
	{
		Integer key = _waitedTexs.get(name);
		Material m = _materials.get(key);
		
		if (m!=null)
		{
			m.bindTexture(TextureManager.instance().get(rscId));
			Log.v(TAG, m.name() + " bind texture:" + name);
		}
	}

	public static MaterialManager instance()
	{
		return _instance;
	}

	MaterialManager()
	{
		_materials = new Material[MAX_MATERIALS];
		_waitedTexs = new HashMap<String, Integer>();
	}
	
	

	public final static int MAX_MATERIALS = 16;
	
	Material[] _materials;
	
	HashMap<String, Integer>	_waitedTexs;
	static MaterialManager _instance = new MaterialManager();
	final static String TAG = "MaterialManager";

}
