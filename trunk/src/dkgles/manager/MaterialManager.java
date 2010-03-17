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
		_materialMap = new HashMap<int, Material>();
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
	public int create(String name, Texture)
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
		return _materials.get(mid);
	}
	
	private HashMap<int, Material> _materials;
	private static MaterialManager _instance;
}