package dkgles.manager;

import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
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
public class MaterialManager implements TextureManager.EventListener
{
	
	public final static int BLACK_ID = 0;
	
	
	/**
	 * Create a material and return material ID
	 *@param name a human readable string for debugging
	 *@param texture A texture that can bind to or a null reference if you need flat color only.
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
	 *	TextureManager.instance().create(texName, rscId);
	 *	MaterialManager.instance().create(matName, TextureManager.instance().get(rscId));
	 *@param name A human readable name for material
	 *@param texName name for texture
	 *@param rscID resource ID for texture
	 */
	public int create(final String name, final String texName, final int rscId)
	{
		Material material = new Material(name);
		int id = register(material); 
		_waitedTexs.put(texName, id);
		TextureManager.instance().create(texName, rscId, this);
		
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
		return _materials[getByName(name)];
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
			if (_materials[i].name().equals(name))
			{
				return i;
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
			MaterialDefHandler handler = new MaterialDefHandler();
			xr.setContentHandler(handler);
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
	
	/**
	 *@deprecated
	 */
	private int findKey()
	{
		//int id = 0;
		//while (_materials.containsKey(id))
		//{
		//	id++;
		//}
		
		return 0;
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
			m.bindTexture(TextureManager.instance().get(rscId));
			Log.v(TAG, m.name() + " bind texture:" + name);
		}
	}

	public static MaterialManager instance()
	{
		return _instance;
	}

	MaterialManager()
	{
		_materials = new Material[MAX_MATERIALS];
		_waitedTexs = new HashMap<String, Integer>();
	}
	
	

	public final static int MAX_MATERIALS = 16;
	
	Material[] _materials;
	
	HashMap<String, Integer>	_waitedTexs;
	static MaterialManager _instance = new MaterialManager();
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
			Material m = new Material(
				parseString(atts, "name", "N/A"),
				parseFloat(atts, "red", 1.0f),
				parseFloat(atts, "green", 1.0f),
				parseFloat(atts, "blue", 1.0f),
				parseFloat(atts, "alpha", 1.0f)
			);
		}
		else if (localName.equals("texture"))
		{
			
		}
		
		_name = atts.getValue("name");
		_red = parseFloat(atts, "red", 1.0f);
		_green = parseFloat(atts, "green", 1.0f);
		_blue = parseFloat(atts, "blue", 1.0f);
		_alpha = parseFloat(atts, "alpha", 1.0f);
	}
	
	float parseFloat(Attributes atts, String name, float defaultVal)
	{
		float val;
		
		try
		{
			val = Float.parseFloat(atts.getValue(name));
		}
		catch(Exception e)
		{
			val = defaultVal;
		}
		
		return val;
	}
	 
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException 
	{
		Log.v(TAG, "endElement");
    	
		Material m = new Material(_name, _red, _green, _blue, _alpha);
		MaterialManager.instance().register(m);
	}
    
	String _name;
	float	_alpha;
	float	_red;
	float	_green;
	float	_blue;
    
	final static String TAG = "MaterialDefHandler";
}























