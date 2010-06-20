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
	
	ArrayList<IListener> listeners_;
	
	public void registerListener(IListener listener)
	{
		if (listener!=null)
		{
			listeners_.add(listener);
		}
	}
	
	public void unregisterListener(IListener listener)
	{
		if (listener!=null)
		{
			listeners_.remove(listener);
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
		
		if (texture==null)
			return -1;
		
		int id = register(texture);
		
		for (IListener listener : listeners_)
		{
			listener.onCreated(id, texture);
		}
		
		Log.v(TAG, "create texture: " + name + "(" + id + ")");
		
		return id;
	}
	
	public int register(Texture texture)
	{
		for (int i=0;i<MAX_TEXTURES;i++)
		{
			if (textures_[i]==null)
			{
				textures_[i] = texture;
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
			if (ready_)
				return;
			
			texture_ = texture;
			ready_ = true;
			notifyAll();
		}
		
		public synchronized Texture get()
		{
			while(!ready_)
			{
				try
				{
					wait();
				}
				catch(InterruptedException e)
				{
					
				}
			}
			return texture_;
		}
		
		private boolean ready_ 	 = false;
		private Texture texture_ = null;
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
			Log.e(TAG, "resource(" + name + ") not found, id: " + resId);
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
		if (textures_[id]==null)
			return;
		
		GLHost.INSTANCE.request(new GLDeleteTextureRequest(textures_[id]));
		textures_[id].release();
		textures_[id] = null;
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
		if (id<0||id>=MAX_TEXTURES)
			return null;
		
		return textures_[id];
	}
	
	/**
	 * Get material by given name
	 */
	public Texture getByName(String name) throws TextureNotFoundException
	{
		return get(findIdByName(name));
		//int id = findIdByName(name);
		
		//if (id!=-1)
		//{
			//return _textures[findIdByName(name)];
		//}
		//else
		//{
			//throw new TextureNotFoundException(name);
		//}
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
			if (textures_[i]!=null)
			{
				if (textures_[i].name().equals(name))
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
	
	
	private TextureManager()
	{
		textures_ 	= new Texture[MAX_TEXTURES];
		rconverter_ = new RConverter();
		listeners_ 	= new ArrayList<IListener>();
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
	private Texture[]	textures_;
	
	public final static int MAX_TEXTURES = 64;
	public final static String TAG = "TextureManager";
	
	
	public class TextureNotFoundException extends RuntimeException
	{
		public TextureNotFoundException(String name)
		{
			super(name);
		}

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
			name_ 	= name;
			bitmap_ = bitmap;
			rscId_ 	= rscId;
			holder_ = holder;
		}

		public void run()
		{
			int[] id = new int[1];
			GL10 gl = GLHost.INSTANCE.get();
       		gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glGenTextures(1, id, 0);
        			
			if (id[0]==0)
			{
				Log.e(TAG, "GL gens invalid id for:" + name_);
				holder_.set(null);
				return;
			}
        	        
       		// Set default parameters
       		gl.glBindTexture(GL10.GL_TEXTURE_2D, id[0]);

       		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
       		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
       		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
       		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,	GL10.GL_REPEAT);
       		gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
        	        
        	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_, 0);
        	bitmap_.recycle();    	
        	holder_.set(new Texture(name_, id[0]));
        	
		}

		private final String 			name_;
		private final Bitmap			bitmap_;
		private final int				rscId_;
		private final TextureHolder 	holder_;
	}

	/**
	 * 
	 */
	class GLDeleteTextureRequest implements Runnable
	{
		public GLDeleteTextureRequest(final Texture texture)
		{
			texture_ = texture;
		}
	
		public void run()
		{
			int[] id = new int[1];
			id[0] = texture_.glID();
			GLHost.INSTANCE.get().glDeleteTextures(1, id, 0);
		}
		
		private final Texture texture_;
	}
		
	public int getRscIdByString(String str)
	{
		return rconverter_.getRscIdByString(str);	
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
			try
			{
				Field field = _R_drawableClass.getField(str);
				id = field.getInt(_R_drawableObject);	
			}
			catch (SecurityException e)
			{
				e.printStackTrace();
				id = -1;
			}
			catch (NoSuchFieldException e)
			{
				e.printStackTrace();
				id = -1;
			}
			catch (IllegalArgumentException e)
			{
				e.printStackTrace();
				id = -1;
			}
			catch (IllegalAccessException e)
			{
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
		
		private Object 	_R_drawableObject;
		private Class	_R_drawableClass;	
	}
	
	private RConverter rconverter_;
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
