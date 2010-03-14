package dkgles.manager;

import java.io.InputStream;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;
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
	
	public void initialize(GL10 gl)
	{
		_gl = gl;
		
		_initialized = true;
	}
	
	public Texture create(GL10 gl, String name, InputStream is)
	{
		Log.d(TAG, name);
		Texture t = TextureFactory.create(gl, is);
		_textures.put(name, t);
		return t;
	}
	
	/**
	 * release all texture resources
	 */
	public void releaseAll()
	{
		//FIX ME
		//for (Texture t : _textures)
		//	t.release();
	}
	
	
	public void releaseTexture(int[] id)
	{
		_gl.glDeleteTextures(1, id, 0);
	}
	
	public Texture get(String name)
	{
		return _textures.get(name);
	}
	
	private TextureManager()
	{
		_textures = new HashMap<String, Texture>();
	}
	
	private GL10 			_gl;
	private boolean 		_initialized;
	
	private HashMap<String, Texture> _textures;
	
	private static TextureManager _instance;
	private final static String TAG = "TEXTURE_MANAGER";
}