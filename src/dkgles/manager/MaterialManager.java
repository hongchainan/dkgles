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
public enum MaterialManager implements TextureManager.EventListener
{
	INSTANCE;
	
	public final static int BLACK_ID = 0;
	
	
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
	public int create(final String name, final String texName, final int rscId)
	{
		Material material = new Material(name);
		int id = register(material); 
		//_waitedTexs.put(texName, id);
		TextureManager.INSTANCE.create(texName, rscId);
		material.bindTexture(TextureManager.INSTANCE.get(rscId));
		return id;
	}

	/**
	 *Register a material object and return its ID
	 */
	public int register(Material material)
	{
		for (int i=0;i<MAX_MATERIALS;i++)
		{
			if (_materials[i]==null)
			{
				_materials[i] = material;
				return i;
			}
		}
		return -1;
	}

	/**
	 *Get material by given ID
	 */
	public Material get(int mid)
	{
		return _materials[mid];
	}

	/**
	 * Get material by given name
	 */
	public Material getByName(String name)
	{
		return _materials[findIdByName(name)];
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
			if (_materials[i]!=null)
			{
				if (_materials[i].name().equals(name))
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
		if (_materials[mid]!=null)
		{
			_materials[mid].release();
			_materials[mid] = null;
		}
	}

	/**
	 * Release material manager
	 */
	public void release()
	{
		for (Material m : _materials)
		{
			if (m!=null)
			{
				m.release();
			}
		}

		_materials = null;
	}
	
	/**
	 * Parse material from xml 
	 */
	public void parse(int rscId)
	{
		XmlUtil.parse(ContextHolder.instance().get(), new MaterialDefHandler(), rscId);
	}
	
	/**
	 *@see TextureManager.EventListener
	 *@see TextureManager#create
	 */
	//@override
	public void onTextureDeleted(String name, int rscId)
	{
		Log.v(TAG, "onTextureDeleted:" + name);
	}

	/**
	 *@see TextureManager.EventListener
	 *@see TextureManager#create
	 */
	//@override
	public void onTextureLoaded(String name, int rscId)
	{
		Integer key = _waitedTexs.get(name);
		Material m = _materials[key];
		
		if (m!=null)
		{
			m.bindTexture(TextureManager.INSTANCE.get(rscId));
			Log.v(TAG, m.name() + " bind texture:" + name);
		}
	}

	MaterialManager()
	{
		_materials = new Material[MAX_MATERIALS];
	}
	
	

	public final static int MAX_MATERIALS = 16;
	
	Material[] _materials;
	
	HashMap<String, Integer>	_waitedTexs;
	final static String TAG = "MaterialManager";

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
		Log.v(TAG, "startElement");
		
		//_name = localName;

		if (localName.equals("material"))
		{
			_material = new Material(
				XmlUtil.parseString(atts, "name", "N/A"),
				XmlUtil.parseFloat(atts, "red", 1.0f),
				XmlUtil.parseFloat(atts, "green", 1.0f),
				XmlUtil.parseFloat(atts, "blue", 1.0f),
				XmlUtil.parseFloat(atts, "alpha", 1.0f)
			);
		}
		else if (localName.equals("texture"))
		{
			TextureManager mgr = TextureManager.INSTANCE;
			
			// get resource ID by its' name
			int rscId = mgr.getRscIdByString(XmlUtil.parseString(atts, "rsc_id", "N/A"));
			
			mgr.create(
					XmlUtil.parseString(atts, "name", "N/A"),
					rscId
			);
			
			_material.bindTexture(mgr.get(rscId));
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
			MaterialManager.INSTANCE.register(_material);
			_material = null;
		}
		else if (localName.equals("texture"))
		{
			
		}
		Log.v(TAG, "endElement");
	}

	Material _material;
    
	final static String TAG = "MaterialDefHandler";
}























