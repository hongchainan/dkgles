package dkgles.manager;

import java.util.HashMap;
import java.util.Iterator;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLException;
import android.opengl.GLUtils;
import android.util.Log;
import dkgles.Texture;

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
	
	public synchronized void initialize(Context context, GL10 gl)
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
	public synchronized Texture create(String name, int rsc_id)
	{
		if (_textures.containsKey(rsc_id))
		{
			Log.v(TAG, "found exist texture:" + rsc_id);
			return _textures.get(rsc_id);
		}
		
		try
		{
			Bitmap bitmap = BitmapFactory.decodeStream(
					_context.getResources().openRawResource(rsc_id));
			
			// may decode fail
			if (bitmap==null)
			{
				Log.e(TAG, "failed to decode bitmap: " + rsc_id);
				return null;
			}
			
			int[] id = new int[1];
			_gl.glGenTextures(1, id, 0);
			
			if (id[0]==0)
			{
				Log.e(TAG, "GL gens invalid id for:" + name);
				return null;
			}
	        
			// 	Set default parameters
			_gl.glEnable(GL10.GL_TEXTURE_2D);
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
			_textures.put(new Integer(rsc_id), t);
			
			Log.d(TAG, "create texture: " + t);
			
			return t; 			
		}
		catch(Resources.NotFoundException e)
		{
			Log.e(TAG, "resource not found, id: " + rsc_id);
			return null;
		}
		catch(GLException e)
		{
			Log.e(TAG, "catch a GLException:" + e.getMessage());
			return null;
		}
	}
	
	public synchronized void release(int rsc_id)
	{
		Texture t = _textures.remove(rsc_id);
		
		if (t==null)
			return;
		
		int [] id = new int[1];
		id[0] = t.glID();
		
		_gl.glDeleteTextures(1, id, 0);
		
		Log.v(TAG, "release texture:" + t);
		
		// mark this reference is no more used.
		id = null;
		t = null;
	}
	
	public synchronized void releaseAll()
	{
		Log.v(TAG, "releaseAll()");
		
		Iterator<Texture> iter = _textures.values().iterator();
		
		while (iter.hasNext())
		{
			Texture t = (Texture)iter.next();
			Log.v(TAG, "release texture:" + t);
			t.release(_gl);
		}
		
		_textures.clear();
	}
	
	
	public synchronized Texture get(int rsc_id)
	{
		return _textures.get(rsc_id);
	}
	
	
	private TextureManager()
	{
		_textures = new HashMap<Integer, Texture>();
	}
	
	protected void finalize() throws Throwable 
	{
		Log.v(TAG, "finalize()");
		
	    try
	    {
	    	releaseAll();
	    }
	    finally
	    {
	        super.finalize();
	    }
	}
	
	private Context			_context;
	private GL10 			_gl;
	private boolean 		_initialized;
	
	private HashMap<Integer, Texture> _textures;
	
	private static TextureManager _instance;
	private final static String TAG = "TextureManager";
}