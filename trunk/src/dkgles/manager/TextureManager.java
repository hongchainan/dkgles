package dkgles.manager;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Set;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
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
	
	public void initialize(Context context, GL10 gl)
	{
		_context = context;
		_gl = gl;
		_initialized = true;
	}
	
	public boolean initialized()
	{
		return _initialized;
	}
	
	/**
	 *
	 */
	public Texture create(String name, int rsc_id)
	{
		if (_textures.containsKey(rsc_id))
		{
			return _textures.get(rsc_id);
		}
		
		//TODO: add try-catch block to handle IO exception
		Bitmap bitmap = BitmapFactory.decodeStream(
			_context.getResources().openRawResource(rsc_id));
			
		Log.d(TAG, ":" + bitmap);
		//Log.d(TAG, ":" + bitmap.getConfig());
	
		_gl.glEnable(GL10.GL_TEXTURE_2D);
		
		int[] id = new int[1];
		_gl.glGenTextures(1, id, 0);
        
		// 	Set default parameters
		_gl.glBindTexture(GL10.GL_TEXTURE_2D, id[0]);

		_gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_LINEAR);
		_gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_LINEAR);
		_gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
        		GL10.GL_REPEAT);
		_gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
        		GL10.GL_REPEAT);
		_gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
        		GL10.GL_MODULATE);
        
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		bitmap.recycle();
		
		Texture t = new Texture(name, id[0]); 	
		_textures.put(rsc_id, t);
     
		return t; 
	}
	
	public void release(int rsc_id)
	{
		Texture t = _textures.remove(rsc_id);
		
		if (t==null)
			return;
		
		int [] id = new int[1];
		id[0] = t.glID();
			
		_gl.glDeleteTextures(1, id, 0);
		
		// mark this reference is no more used.
		id = null;
		t = null;
	}
	
	public void releaseAll()
	{
		Set<Integer> set = _textures.keySet();
		
		for (int k : set)
		{
			release(k);
		}
		
		_textures.clear();
	}
	
	
	public Texture get(int rsc_id)
	{
		return _textures.get(rsc_id);
	}
	
	
	private TextureManager()
	{
		_textures = new HashMap<Integer, Texture>();
	}
	
	private Context			_context;
	private GL10 			_gl;
	private boolean 		_initialized;
	
	private HashMap<Integer, Texture> _textures;
	
	private static TextureManager _instance;
	private final static String TAG = "TEXTURE_MANAGER";
}