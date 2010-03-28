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
	
	public void setGL(GL10 gl)
	{
		_gl = gl;
	}
	
	public void setGLSurfaceView(GLSurfaceView glSurfaceView)
	{
		_glSurfaceView = glSurfaceView;
	}
	
	public void setContext(Context context)
	{
		_context = context;
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
			
			_listeners.put(rsc_id, listener);
			_texLoaded = false;
			
			// issue a queued event to Renderer Thread
			_glSurfaceView.queueEvent(new Runnable()
			{
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
        			
        			// notify job is done
        			Message msg = new Message();
        			msg.what = GL_TEXTURE_GENERATION;
        			msg.arg1 = rsc_id;
        			msg.arg2 = id[0];
        			msg.obj = name;
        			_handler.sendMessage(msg);
                }
            });//End of queueEvnet of Surface View	 			
		}
		catch(Resources.NotFoundException e)
		{
			Log.e(TAG, "resource not found, id: " + rsc_id);
		}
		catch(GLException e)
		{
			Log.e(TAG, "catch a GLException:" + e.getMessage());
		}
		
		/*while (!_texLoaded)
		{
			try
			{
				Thread.sleep(1);
			}
			catch (InterruptedException e)
			{
				
			}
		}*/
	}
	
	public synchronized void release(final int rsc_id)
	{
		final Texture t = _textures.remove(rsc_id);
		
		if (t==null)
			return;
		
		final int [] id = new int[1];
		id[0] = t.glID();
		
		_glSurfaceView.queueEvent(new Runnable()
		{
            public void run()
            {
            	_gl.glDeleteTextures(1, id, 0);
            	
            	Message msg = new Message();
    			msg.what = GL_TEXTURE_DELETION;
    			msg.obj = t;
    			msg.arg1 = rsc_id;
    			_handler.sendMessage(msg);
            }
		});
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
		_textures 	= new HashMap<Integer, Texture>();
		_listeners 	= new HashMap<Integer, EventListener>();
		_handler 	= new TextureManagerHandler();
		
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
	
	private final static int GL_TEXTURE_GENERATION 	= 0;
	private final static int GL_TEXTURE_DELETION 	= 1;
	
	private TextureManagerHandler	_handler;
	
	private Context			_context;
	private GL10 			_gl;
	private GLSurfaceView	_glSurfaceView;
	private boolean 		_initialized;
	private boolean 		_texLoaded;
	
	private HashMap<Integer, Texture> 		_textures;
	private HashMap<Integer, EventListener>	_listeners;
	
	private static TextureManager _instance;
	private final static String TAG = "TextureManager";
	
	/**
	 * 
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
	        		
	        		listener = _listeners.get(msg.arg1);
	        		
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