package dkgles.manager;

import java.io.InputStream;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

import dkgles.Texture;
import dkgles.TextureFactory;

public class TextureManager
{
	public static TextureManager instance()
	{
		if (_instance==null)
		{
			_instance = new TextureManager();
		}
		
		return _instance;
	}
	
	public Texture create(GL10 gl, String name, InputStream is)
	{
		Texture t = TextureFactory.create(gl, is);
		_textures.put(name, t);
		return t;
	}
	
	/**
	 * release all texture resources
	 */
	public void releaseAll()
	{
		//TODO
	}
	
	public Texture get(String name)
	{
		return _textures.get(name);
	}
	
	private TextureManager()
	{
		_textures = new HashMap<String, Texture>();
	}
	
	private HashMap<String, Texture> _textures;
	
	private static TextureManager _instance;
}