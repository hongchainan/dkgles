package dkgles.manager;

import java.util.HashMap;

import lost.kapa.ContextHolder;
import lost.kapa.XmlUtil;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;
import dkgles.Material;
import dkgles.Texture;

/**
 *Usage:
 *	int texID = R.drawable.my_texture
 *	int mid = MaterialManager.instance().create("MAT_NAME", "TEX_NAME", texID);
 *	Material m = MaterialManager.instance().get(mid);
 *	MaterialManager.instance().destroy(mid);
 */
public enum MaterialManager
{
	INSTANCE;
	
	public final static int BLACK_ID = 0;
	
	public interface IListener
	{
		public void onCreated(Material material);
	}
	
	private IListener listener_;
	
	public void registerListener(IListener listener)
	{
		listener_ = listener;
	}
	
	
	/**
	 * Create a material and return material ID
	 * @param name a human readable string for debugging
	 * @param texture A texture that can bind to or a null reference if you need flat color only.
	 */
	public int create(String name, Texture texture)
	{
		Material material = new Material(name);
		material.bindTexture(texture);
		
		return register(material);
	}

	/**
	 *An utility let you create material and texture at sametime
	 *This method is equal to:
	 *	TextureManager.INSTANCE.create(texName, rscId);
	 *	MaterialManager.instance().create(matName, TextureManager.INSTANCE.get(rscId));
	 *@param name A human readable name for material
	 *@param texName name for texture
	 *@param rscID resource ID for texture
	 */
	public int create(final String name, final String texName, final int resId)
	{
		int id = TextureManager.INSTANCE.create(texName, resId);
		return create(name, TextureManager.INSTANCE.get(id));
	}

	/**
	 *Register a material object and return its ID
	 */
	public int register(Material material)
	{
		for (int i=0;i<MAX_MATERIALS;i++)
		{
			if (materials_[i]==null)
			{
				materials_[i] = material;
				
				if (listener_!=null)
				{
					listener_.onCreated(material);
				}
				
				return i;
			}
		}
		return -1;
	}

	/**
	 *Get material by given ID
	 */
	public Material get(int id)
	{
		if (id<0||id>=MAX_MATERIALS)
		{
			return null;
		}
		return materials_[id];
	}

	/**
	 * Get material by given name
	 */
	public Material getByName(String name)
	{
		return get(findIdByName(name));
	}

	/**
	 * Find material ID by given name
	 *
	 * @param name material name
	 */
	public int findIdByName(String name)
	{
		for (int i=0;i<MAX_MATERIALS;i++)
		{
			if (materials_[i]!=null)
			{
				if (materials_[i].name().equals(name))
				{
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * Destroy material by given id
	 */
	public void destroy(int mid)
	{
		if (materials_[mid]!=null)
		{
			materials_[mid].release();
			materials_[mid] = null;
		}
	}
	
	/**
	 * 
	 */
	public void destroyAll()
	{
		for (int i=0;i<MAX_MATERIALS;i++)
		{
			destroy(i);
		}
	}

	/**
	 * Release material manager
	 */
	public void release()
	{
		destroyAll();
		materials_ = null;
	}
	
	/**
	 * Parse material from xml 
	 */
	public void parse(int resId)
	{
		XmlUtil.parse(ContextHolder.INSTANCE.get(), new MaterialDefHandler(), resId);
	}
	
	private MaterialManager()
	{
		materials_ = new Material[MAX_MATERIALS];
	}
	
	private Material[] materials_;

	public final static int MAX_MATERIALS = 64;
	public final static String TAG = "MaterialManager";
}

class MaterialDefHandler extends DefaultHandler
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
		if (localName.equals("material"))
		{
			material_ = new Material(atts);
			Log.v(TAG, "create material: " + material_);
		}
		else if (localName.equals("texture"))
		{
			// 1. try to find texture by name, if failed, load texture by rsc_id	
			int id = TextureManager.INSTANCE.findIdByName(
					XmlUtil.parseString(atts, "name", ""));
			
			if (id<0)
			{
				// get resource ID by its' name
				int resId = TextureManager.INSTANCE.getRscIdByString(
						XmlUtil.parseString(atts, "rsc_id", "N/A"));
			
				id = TextureManager.INSTANCE.create(
					XmlUtil.parseString(atts, "name", "N/A"),
					resId);
			}
				
			material_.bindTexture(
					TextureManager.INSTANCE.get(id));
		}
		else
		{
			//skip this TAG
		}
	}
	 
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException 
	{
		if (localName.equals("material"))
		{
			MaterialManager.INSTANCE.register(material_);
			material_ = null;
		}
		else if (localName.equals("texture"))
		{
			
		}
	}

	private Material material_ = null;
	public final static String TAG = "MaterialDefHandler";
}























