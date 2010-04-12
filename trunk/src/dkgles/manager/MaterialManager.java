package dkgles.manager;

import java.util.HashMap;

import android.util.Log;
import dkgles.Material;
import dkgles.Texture;

public class MaterialManager implements TextureManager.EventListener
{
	
	public final static int BLACK_ID = 0;
	
	public static MaterialManager instance()
	{
		if (_instance==null)
		{
			_instance = new MaterialManager();
		}
		
		return _instance;
	}
	
	private MaterialManager()
	{
		_materials = new HashMap<Integer, Material>();
		_waitedTexs = new HashMap<String, Integer>();
	}
	
	/**
	 * release all material resources
	 */
	public void releaseAll()
	{
		_materials.clear();
		_waitedTexs.clear();
	}
	
	
	/**
	 * return material ID
	 */
	public int create(String name, Texture texture)
	{
		Material m = new Material(name);
		m.bindTexture(texture);
		
		int id = 0;
		while (_materials.containsKey(id))
		{
			id++;
		}
		
		_materials.put(id, m);
		return id;
	}
	
	private int findKey()
	{
		int id = 0;
		while (_materials.containsKey(id))
		{
			id++;
		}
		
		return id;
	}
	
	public int create(final String name, final String texName, final int rsc_id)
	{
		Material m = new Material(name);
		int id = findKey();
		_materials.put(id, m);
		_waitedTexs.put(texName, id);
		
		TextureManager.instance().create(texName, rsc_id, this);
		
		return id;
	}
	
	public void onTextureDeleted(String name, int rscId)
	{
		Log.v(TAG, "onTextureDeleted:" + name);
	}

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
	
	public Material get(int mid)
	{
		Material m = _materials.get(mid); 
		return m;
	}
	
	private HashMap<String, Integer>	_waitedTexs;
	private HashMap<Integer, Material> _materials;
	private static MaterialManager _instance;
	private final static String TAG = "MaterialManager";

}