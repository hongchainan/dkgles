package dkgles.manager;

import java.lang.reflect.Field;
import javax.microedition.khronos.opengles.GL10;
import lost.kapa.ContextHolder;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;
import dkgles.Texture;

/**
 *Use resource id (i.e. R.drawable.my_texture) as refer key
 *Usage:
 *	int TexID = R.drawable.my_texture;
 *	TextureManager.instance().create("MY_TEXTURE", TexID, null);
 *	Texture t = TextureManager.instance().get(TexID);
 *	TextureManager.instance().destroy(TexID);
 *TODO:
 *	Make sure local event handler stay in UI Thread
 *@author doki lin
 */
public enum TextureManager
{
	INSTANCE;
	
	/**
	 *Set GL context
	 */
	public void setGL(GL10 gl)
	{
		_gl = gl;
	}
	
	/**
	 * Set GLSurfaceView. Since we have to submit jobs to GLThreads
	 * See: http://developer.android.com/reference/android/opengl/GLSurfaceView.html#queueEvent(java.lang.Runnable)
	 * @see create
	 * @see createAsync
	 * @see destroy
	 */
	public void setGLSurfaceView(GLSurfaceView glSurfaceView)
	{
		_glSurfaceView = glSurfaceView;
	}
	
	/**
	 * 
	 */
	public boolean initialized()
	{
		if (_glSurfaceView!=null&&_gl!=null)
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Create a texture resource
	 * @param name texture name for debugging issue
	 * @param rsc_id resource id, EX: R.id.my_texture
	 * @return Texture ID
	 */
	public int create(String name, int resId)
	{
		// future pattern
		TextureHolder textureHolder = createImpl(name, resId);
		
		if (textureHolder==null)
			return -1;
		
		return register(textureHolder.get());
	}
	
	public int register(Texture texture)
	{
		for (int i=0;i<MAX_TEXTURES;i++)
		{
			if (_textures[i]==null)
			{
				_textures[i] = texture;
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * 
	 * @author doki
	 *
	 */
	class TextureHolder
	{
		public synchronized void set(Texture texture)
		{
			if (_ready)
				return;
			
			_texture = texture;
			_ready = true;
			notifyAll();
		}
		
		public synchronized Texture get()
		{
			while(!_ready)
			{
				try
				{
					wait();
				}
				catch(InterruptedException e)
				{
					
				}
			}
			return _texture;
		}
		
		boolean _ready;
		Texture _texture;
	}

	/**
	 * Implementation of creating texture
	 * @param name
	 * @param rscId
	 * @param listener
	 */
	TextureHolder createImpl(final String name, final int resId)
	{	
		try
		{
			final Bitmap bitmap = BitmapFactory.decodeStream(
					ContextHolder.INSTANCE.get().getResources().openRawResource(resId));
			
			// may decode fail
			if (bitmap==null)
			{
				Log.e(TAG, "failed to decode bitmap: " + resId);
				return null;
			}
			
			// issue a queued event to Renderer Thread
			TextureHolder holder = new TextureHolder();
			_glSurfaceView.queueEvent(new GLCreateTextureRequest(name, bitmap, resId, holder));
			return holder;
		}
		catch(Resources.NotFoundException e)
		{
			Log.e(TAG, "resource not found, id: " + resId);
		}
		catch(GLException e)
		{
			Log.e(TAG, "catch a GLException:" + e.getMessage());
		}
		return null;
	}

	
	/**
	 *Destroy texture by given resource ID
	 */
	public synchronized void destroy(int id)
	{
		if (_textures[id]==null)
			return;
		
		_glSurfaceView.queueEvent(new GLDeleteTextureRequest(_textures[id]));
		_textures[id].release();
		_textures[id] = null;
	}
	
	public void destroyAll()
	{
		for (int i=0;i<MAX_TEXTURES;i++)
		{
			destroy(i);
		}
	}
	
	public void release()
	{
		destroyAll();
	}
	
	/**
	 *	Get texture by givenID
	 */
	public synchronized Texture get(int id)
	{
		return _textures[id];
	}
	
	
	TextureManager()
	{
		_textures 	= new Texture[MAX_TEXTURES];
		_rconverter = new RConverter();
	}
	
	/**
	 *Called by GC
	 */
	protected void finalize() throws Throwable 
	{
		Log.v(TAG, "finalize()");
		
	    try
	    {
	    	release();
	    }
	    finally
	    {
	        super.finalize();
	    }
	}
	
	GL10 			_gl;
	GLSurfaceView	_glSurfaceView;

	// deprecated
	boolean 		_initialized;
	
	//	
	Texture[]	_textures;
	
	
	final static int MAX_TEXTURES = 64;
	
	final static String TAG = "TextureManager";

	/**
	 */
	class GLCreateTextureRequest implements Runnable
	{
		GLCreateTextureRequest(final String name, final Bitmap bitmap, int rscId, TextureHolder holder)
		{
			_name 	= name;
			_bitmap = bitmap;
			_rscId 	= rscId;
			_holder = holder;
		}

		public void run()
		{
			int[] id = new int[1];
       		_gl.glEnable(GL10.GL_TEXTURE_2D);
			_gl.glGenTextures(1, id, 0);
        			
			if (id[0]==0)
			{
				Log.e(TAG, "GL gens invalid id for:" + _name);
				_holder.set(null);
				return;
			}
        	        
       		// Set default parameters
       		_gl.glBindTexture(GL10.GL_TEXTURE_2D, id[0]);

       		_gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
       		_gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
       		_gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
       		_gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,	GL10.GL_REPEAT);
       		_gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
        	        
        	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, _bitmap, 0);
        	_bitmap.recycle();    	
        	_holder.set(new Texture(_name, id[0]));
        	
		}

		final String 			_name;
		final Bitmap			_bitmap;
		final int				_rscId;
		final TextureHolder 	_holder;
	}

	/**
	 * 
	 */
	class GLDeleteTextureRequest implements Runnable
	{
		public GLDeleteTextureRequest(final Texture texture)
		{
			_texture = texture;
		}
	
		public void run()
		{
			int[] id = new int[1];
			id[0] = _texture.glID();
        	_gl.glDeleteTextures(1, id, 0);
		}
		
		final Texture _texture;
	}
		
	public int getRscIdByString(String str)
	{
		return _rconverter.getRscIdByString(str);	
	}
	
	class RConverter
	{
		public RConverter()
		{
			initReflection();
		}
		
		int getRscIdByString(String str)
		{
			int id = -1;
			try {
				Field field = _R_drawableClass.getField(str);
				id = field.getInt(_R_drawableObject);	
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				id = -1;
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				id = -1;
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				id = -1;
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				id = -1;
			}
			return id;
		}
		
		void initReflection()
		{	
			try {
				Class c = Class.forName("lost.kapa.R");
				Class[] innerClasses = c.getDeclaredClasses();
				
				for (int i=0;i<innerClasses.length;i++)
				{
					if (innerClasses[i].getName().equals("lost.kapa.R$drawable"))
					{
						_R_drawableClass = innerClasses[i]; 
						_R_drawableObject = innerClasses[i].newInstance();
						break;
					}
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Object 	_R_drawableObject;
		Class	_R_drawableClass;	
	}
	
	RConverter _rconverter;
}














