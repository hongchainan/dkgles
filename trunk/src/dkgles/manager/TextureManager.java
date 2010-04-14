package dkgles.manager;

import java.util.HashMap;
import java.util.Iterator;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Handler;
import android.os.Message;
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
	
	/**
	 *Set GL context
	 */
	public void setGL(GL10 gl)
	{
		_gl = gl;
	}
	
	/**
	 *Set GLSurfaceView. Since we have to schedual jobs to GLThreads
	 *See: http://developer.android.com/reference/android/opengl/GLSurfaceView.html#queueEvent(java.lang.Runnable)
	 *@see create
	 *@see destroy
	 */
	public void setGLSurfaceView(GLSurfaceView glSurfaceView)
	{
		_glSurfaceView = glSurfaceView;
	}
	
	/**
	 *Set context reference
	 */
	public void setContext(Context context)
	{
		_context = context;
	}
	
	/**
	 *@deprecated
	 */
	public synchronized void initialize(Context context, GL10 gl)
	{
		_context = context;
		_gl = gl;
		_initialized = true;
	}
	
	/**
	 *@deprecated
	 */
	public boolean initialized()
	{
		return _initialized;
	}
	
	/**
	 *Create a texture resource
	 *Note: this operation is asynchronous. if you want to know whether the texture is ready, provide TextureManager.EventListener
	 *@param name texture name for debugging issue
	 *@param rsc_id resource id, EX: R.id.my_texture
	 *@param listener if you want to know whether the texture you request is ready for use. or you can set it to null if you don't care
	 */
	public synchronized void create(final String name, final int rsc_id, final EventListener listener)
	{
		if (_textures.containsKey(rsc_id))
		{
			Log.v(TAG, "found exist texture, name" + name + ",id: " + rsc_id);
			return;
		}
		
		try
		{
			final Bitmap bitmap = BitmapFactory.decodeStream(
					_context.getResources().openRawResource(rsc_id));
			
			// may decode fail
			if (bitmap==null)
			{
				Log.e(TAG, "failed to decode bitmap: " + rsc_id);
				return;
			}
			
			if (listener!=null)
			{
				_listeners.put(rsc_id, listener);
			}
			
			// issue a queued event to Renderer Thread
			_glSurfaceView.queueEvent(new GLCreateTextureRequest(name, rscId, bitmap));	 			
		}
		catch(Resources.NotFoundException e)
		{
			Log.e(TAG, "resource not found, id: " + rsc_id);
		}
		catch(GLException e)
		{
			Log.e(TAG, "catch a GLException:" + e.getMessage());
		}
		
	}

	
	/**
	 *Destroy texture by given resource ID
	 */
	public synchronized void destroy(final int rscId)
	{
		final Texture t = _textures.remove(rsc_id);
		
		if (t==null)
			return;
			
		_glSurfaceView.queueEvent(new GLDeleteTextureRequest(t, rscId));
	}
	
	/**
	 *@deprecated
	 */
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
	
	/**
	 *Retrive texture by given resource ID
	 */
	public synchronized Texture get(int rsc_id)
	{
		return _textures.get(rsc_id);
	}
	
	
	TextureManager()
	{
		_textures 	= new HashMap<Integer, Texture>();
		_listeners 	= new HashMap<Integer, EventListener>();
		_handler 	= new TextureManagerHandler();
		
	}
	
	/**
	 *Called by GC
	 */
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
	
	final static int GL_TEXTURE_GENERATION 	= 0;
	final static int GL_TEXTURE_DELETION 	= 1;
	
	TextureManagerHandler	_handler;
	
	Context		_context;
	GL10 		_gl;
	GLSurfaceView	_glSurfaceView;

	// deprecated
	boolean 		_initialized;
	boolean 		_texLoaded;
	
	//	
	HashMap<Integer, Texture> 		_textures;
	HashMap<Integer, EventListener>	_listeners;
	
	static TextureManager _instance;
	final static String TAG = "TextureManager";

	/**
	 */
	class GLCreateTextureRequest implements Runnable
	{
		GLCreateTextureRequest(final String name, final Bitmap bitmap, int rscId)
		{
			_name = name;
			_bitmap = bitmap;
			_rscId = rscId;
		}

		public void run()
                {
	               	int[] id = new int[1];
       			_gl.glEnable(GL10.GL_TEXTURE_2D);
			_gl.glGenTextures(1, id, 0);
        			
       			if (id[0]==0)
       			{
       				Log.e(TAG, "GL gens invalid id for:" + name);
       				return;
       			}
        	        
       			// Set default parameters
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
        			
        		// notify job is done
        		Message msg = new Message();
        		msg.what = GL_TEXTURE_GENERATION;
        		msg.arg1 = rsc_id;
        		msg.arg2 = id[0];
        		msg.obj = name;
        		_handler.sendMessage(msg);
                }

		final String 	_name;
		final Bitmap	_bitmap;
		final int	_rscId;
	}

	/**/
	class GLDeleteTextureRequest implements Runnable
	{
		public GLDeleteTextureRequest(final Texture texture, int rscId)
		{
			_texture = texture;
			_rscId = rscId;
		}
	
		public void run()
        	{
			int[] id = new int[1];
			id[0] = _texture.glID();
        		_gl.glDeleteTextures(1, id, 0);
            	
			Message msg = new Message();
    			msg.what = GL_TEXTURE_DELETION;
    			msg.obj = _texture.name();
    			msg.arg1 = _rscId;
    			_handler.sendMessage(msg);
            	}

		final Texture _texture;
		int _rscId;
	}
	
	/**
	 * A listener used to notify whether texture created or deleted
	 * @author doki
	 *
	 */
	public interface EventListener
	{
		public void onTextureLoaded(final String name, int rsc_id);
		
		public void onTextureDeleted(final String name, int rsc_id);
	}
	
	/**
	 * 
	 * @author doki
	 *
	 */
	public class TextureManagerHandler extends Handler
	{
		public void handleMessage(Message msg)
		{  
			EventListener listener;
			String name;
			int rsc_id;
			
			switch (msg.what)
			{  
	        	case GL_TEXTURE_GENERATION:
	        		name = (String)msg.obj;
	        		rsc_id = msg.arg1;
	        		
	        		Texture t = new Texture(name, msg.arg2);
	        		_textures.put(new Integer(rsc_id), t);
	        		
	        		// try to find out registered listener
	        		listener = _listeners.get(rsc_id);
	        		if (listener!=null)
	        		{
	        			listener.onTextureLoaded(name, rsc_id);
	        		}
	        		_texLoaded = true;
	        		Log.v(TAG, "create texture: " + t);
	        		break;
	        	case GL_TEXTURE_DELETION:
	        		name = (String)msg.obj;
	        		rsc_id = msg.arg1;
	        		
	        		listener = _listeners.get(rsc_id);
	        		
	        		if (listener!=null)
	        		{
	        			listener.onTextureDeleted(name, rsc_id);
	        		}
	        		
	        		Log.v(TAG, "release texture:" + name);
	        		break;
	        }  
	        super.handleMessage(msg);  
		}//End of handleMessage
	}
}
