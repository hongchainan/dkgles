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
		_materialMap = new HashMap<String, Material>();
	}
	
	/**
	 * release all material resources
	 */
	public void releaseAll()
	{
		//TODO
	}
	
	public Material create(String name, Texture texture)
	{
		Material m = new Material(name);
		m.bindTexture(texture);
		
		_materialMap.put(name, m);
		
		return m;	
	}
	
	public Material get(String name)
	{
		return _materialMap.get(name);
	}
	
	
	
	private HashMap<String, Material> _materialMap;
	private static MaterialManager _instance;
}