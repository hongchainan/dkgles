package dkgles.manager;

import java.util.HashMap;

import dkgles.Material;
import dkgles.Texture;

public class MaterialManager
{
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
	}
	
	/**
	 * release all material resources
	 */
	public void releaseAll()
	{
		//TODO
		_materials.clear();
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
	
	public Material get(int mid)
	{
		Material m = _materials.get(mid); 
		return m;
	}
	
	private HashMap<Integer, Material> _materials;
	private static MaterialManager _instance;
	private final static String TAG = "MATERIAL_MANAGER";
}