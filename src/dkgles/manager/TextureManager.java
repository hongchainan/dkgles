package dkgles.manager;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;

import javax.microedition.khronos.opengles.GL10;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import lost.kapa.R;
import lost.kapa.R.drawable;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

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
import dkgles.Material;
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
	public synchronized void create(final String name, final int rscId, final EventListener listener)
	{
		if (_textures.containsKey(rscId))
		{
			Log.v(TAG, "found exist texture, name" + name + ",id: " + rscId);
			return;
		}
		
		try
		{
			final Bitmap bitmap = BitmapFactory.decodeStream(
					_context.getResources().openRawResource(rscId));
			
			// may decode fail
			if (bitmap==null)
			{
				Log.e(TAG, "failed to decode bitmap: " + rscId);
				return;
			}
			
			if (listener!=null)
			{
				_listeners.put(rscId, listener);
			}
			
			// issue a queued event to Renderer Thread
			_glSurfaceView.queueEvent(new GLCreateTextureRequest(name, bitmap, rscId));	 			
		}
		catch(Resources.NotFoundException e)
		{
			Log.e(TAG, "resource not found, id: " + rscId);
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
		final Texture t = _textures.remove(rscId);
		
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
       			Log.e(TAG, "GL gens invalid id for:" + _name);
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
        			
        	// notify job is done
        	Message msg = new Message();
        	msg.what = GL_TEXTURE_GENERATION;
        	msg.arg1 = _rscId;
        	msg.arg2 = id[0];
        	msg.obj = _name;
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
	
	public void parse(Context context, int rscId)
	{
		InputStream istream = null;
		try {
			istream = context.getResources().openRawResource(rscId);

			// Get a SAXParser from the SAXPArserFactory. 
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			// 	Get the XMLReader of the SAXParser we created. 
			XMLReader xr = sp.getXMLReader();
			// 	Create a new ContentHandler and apply it to the XML-Reader
			xr.setContentHandler(new TextureDefHandler());
			//Log.v(logCat, "Calling parse() in ReadTourFromLocal: "+filename);
			// 	Parse the xml-data from our URL. 
			InputSource is = new InputSource(istream); 

			xr.parse(is);
		}
		catch(Exception e)
		{
			Log.e(TAG, e.getMessage());
		}
	}
}

class TextureDefHandler extends DefaultHandler
{
	public TextureDefHandler()
	{
		initReflection();
	}
	
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException 
	{
		Log.v(TAG, "startElement");
		
		_name = atts.getValue("name");
		_rscId = atts.getValue("rsc_id");
		
		//_name = localName;
	}
	
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException 
	{
		Log.v(TAG, "endElement");
		TextureManager.instance().create(_name, getRscIdByName(_rscId), null);
	}
	
	public void endDocument() throws SAXException 
    	{
		// Nothing to do
	}
	
	int getRscIdByName(String name)
	{
		int id = -1;
		try {
			Field field = _R_drawableClass.getField(name);
			id = field.getInt(_R_drawableObject);	
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}
	
	void initReflection()
	{	
		String n;
		try {
			Class c = Class.forName("lost.kapa.R");
			Class[] innerClasses = c.getDeclaredClasses();
			
			for (int i=0;i<innerClasses.length;i++)
			{
				n = innerClasses[i].getName();
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
	
	String _name;
	String _rscId;
	
	final static String TAG = "TextureDefHandler";
}












