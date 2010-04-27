package dkgles.manager;

import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import lost.kapa.ContextHolder;
import lost.kapa.XmlUtil;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLException;
import android.opengl.GLUtils;
import android.util.Log;
import dkgles.GLHost;
import dkgles.Texture;

/**
 *Use resource id (i.e. R.drawable.my_texture) as refer key
 *Usage:
 *	int TexID = R.drawable.my_texture;
 *	TextureManager.instance().create("MY_TEXTURE", TexID, null);
 *	Texture t = TextureManager.instance().get(TexID);
 *	TextureManager.instance().destroy(TexID);
 *TODO:
 *	Make GLCreateTextureRequest and GLDeleteTextureRequest be a single object 
 *@author doki lin
 */
public enum TextureManager
{
	INSTANCE;
	
	public interface IListener
	{
		public void onCreated(int id, Texture texture);
	}
	
	ArrayList<IListener> _listeners;
	
	public void registerListener(IListener listener)
	{
		if (listener!=null)
		{
			_listeners.add(listener);
		}
	}
	
	public void unregisterListener(IListener listener)
	{
		if (listener!=null)
		{
			_listeners.remove(listener);
		}
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
		
		Texture texture = textureHolder.get();
		int id = register(texture);
		
		for (IListener listener : _listeners)
		{
			listener.onCreated(id, texture);
		}
		
		return id;
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
			
			GLHost.INSTANCE.request(new GLCreateTextureRequest(name, bitmap, resId, holder));
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
		
		GLHost.INSTANCE.request(new GLDeleteTextureRequest(_textures[id]));
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
	public synchronized Texture get(int id) //throws TextureNotFoundException
	{
		return _textures[id];
	}
	
	/**
	 * Get material by given name
	 */
	public Texture getByName(String name) throws TextureNotFoundException
	{
		int id = findIdByName(name);
		
		if (id!=-1)
		{
			return _textures[findIdByName(name)];
		}
		else
		{
			throw new TextureNotFoundException();
		}
	}

	/**
	 * Find material ID by given name
	 *
	 * @param name material name
	 */
	public int findIdByName(String name)
	{
		for (int i=0;i<MAX_TEXTURES;i++)
		{
			if (_textures[i]!=null)
			{
				if (_textures[i].name().equals(name))
				{
					return i;
				}
			}
		}
		return -1;
	}
	
	public void parse(int resId)
	{
		XmlUtil.parse(ContextHolder.INSTANCE.get(), new TextureDefHandler(), resId);
	}
	
	
	TextureManager()
	{
		_textures 	= new Texture[MAX_TEXTURES];
		_rconverter = new RConverter();
		_listeners = new ArrayList<IListener>();
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
	
	// deprecated
	boolean 		_initialized;
	
	//	
	Texture[]	_textures;
	
	
	final static int MAX_TEXTURES = 64;
	final static String TAG = "TextureManager";
	
	
	public class TextureNotFoundException extends RuntimeException
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
	}

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
			GL10 gl = GLHost.INSTANCE.get();
       		gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glGenTextures(1, id, 0);
        			
			if (id[0]==0)
			{
				Log.e(TAG, "GL gens invalid id for:" + _name);
				_holder.set(null);
				return;
			}
        	        
       		// Set default parameters
       		gl.glBindTexture(GL10.GL_TEXTURE_2D, id[0]);

       		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
       		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
       		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
       		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,	GL10.GL_REPEAT);
       		gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
        	        
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
			GLHost.INSTANCE.get().glDeleteTextures(1, id, 0);
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

class TextureDefHandler extends DefaultHandler
{
	@Override
	public void startDocument() throws SAXException 
	{
	}
	
	@Override
	public void endDocument() throws SAXException 
	{
		// Nothing to do
	}
	
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException 
	{
		//Log.v(TAG, "startElement");
		if (localName.equals("texture"))
		{
			// get resource ID by its' name
			int resId = TextureManager.INSTANCE.getRscIdByString(
					XmlUtil.parseString(atts, "rsc_id", "N/A"));
			
			TextureManager.INSTANCE.create(
					XmlUtil.parseString(atts, "name", "N/A"),
					resId);
		}
		else
		{
			//skip this TAG
		}
	}
	 
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException 
	{
		//Log.v(TAG, "endElement");
	}
	final static String TAG = "TextureDefHandler";
}














