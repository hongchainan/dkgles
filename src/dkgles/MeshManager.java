package dkgles;

import lost.kapa.ContextHolder;
import lost.kapa.XmlUtil;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import dkgles.manager.MaterialManager;
import dkgles.primitive.Rectangle;

//import android.util.Pa

public enum MeshManager
{
	INSTANCE;
	
	public class NameValuePair
	{
		public String name;
		public String value;
	}
	
	public interface ICreator
	{
		public Mesh create(NameValuePair[] args);
	}
	
	class RectangleCreator implements ICreator
	{
		public Mesh create(NameValuePair[] args) 
		{
			String name = "N/A";
			String materialName = "N/A";
			float width = 0.0f;
			float height = 0.0f;
			
			for (int i=0;i<args.length;i++)
			{
				if (args[i].name.equals("name"))
				{
					name = args[i].value; 
				}
				else if (args[i].name.equals("width"))
				{
					width = Float.parseFloat(args[i].value);
				}
				else if (args[i].name.equals("height"))
				{
					height = Float.parseFloat(args[i].value);
				}
				else if (args[i].name.equals("material_name"))
				{
					materialName = args[i].value;
				}
			}
			
			Rectangle rectangle = new Rectangle(
					name,
					width,
					height,
					MaterialManager.INSTANCE.getByName(materialName));
		
			return rectangle;
		}
	}
	
	RectangleCreator _rectangleCreator = new RectangleCreator();
	
	public static final int PRIM_RECTANGLE = 101;
	
	public int create(int type, NameValuePair[] args)
	{
		if (type==PRIM_RECTANGLE)
		{
			Mesh mesh = _rectangleCreator.create(args);
			return register(mesh);
		}
		
		return -1;
	}
	
	public Mesh getByName(String name) throws MeshNotFoundException
	{
		int id = findIdByName(name);
		
		if (id>=0)
		{
			return _meshes[findIdByName(name)];
		}
		else
		{
			throw new MeshNotFoundException(name);
		}
	}
	
	public int findIdByName(String name)
	{
		for (int i=0;i<MAX_MESHES;i++)
		{
			if (_meshes[i]!=null)
			{
				if (_meshes[i].name().equals(name))
				{
					return i;
				}
			}
		}
		
		return -1;
	}
	
	public Mesh get(int id)
	{
		return _meshes[id];
	}
	
	public void destroy(int id)
	{
		if (_meshes[id]!=null)
		{
			_meshes[id].release();
			_meshes[id] = null;
		}
	}
	
	public int register(Mesh mesh)
	{
		for (int i=0;i<MAX_MESHES;i++)
		{
			if ( _meshes[i]==null)
			{
				_meshes[i] = mesh;
				return i;
			}
		}
		
		return -1;
	}
	
	public void parse(int resId)
	{
		if (_meshDefHandler!=null)
		{
			_meshDefHandler = new MeshDefHandler();
		}
		
		XmlUtil.parse(ContextHolder.INSTANCE.get(), new MeshDefHandler(), resId);
	}
	
	MeshManager()
	{
		_meshes = new Mesh[MAX_MESHES];
	}
	
	public void release()
	{
		if (_meshes!=null)
		{
			// TODO
			_meshes = null;
		}
	}
	
	Mesh[]	_meshes;
	
	MeshDefHandler _meshDefHandler; 
	
	static final int MAX_MESHES = 16;
	
	public class MeshNotFoundException extends RuntimeException
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public MeshNotFoundException(String meshName)
		{
			super(meshName);
		}
	}
}

class MeshDefHandler extends DefaultHandler
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
		if (localName.equals("rectangle"))
		{
			
			/*
			Rectangle rectangle = new Rectangle(
					XmlUtil.parseString(atts, "name", "Rectangle:N/A"),
					XmlUtil.parseFloat(atts, "width", 0.0f),
					XmlUtil.parseFloat(atts, "height", 0.0f),
					MaterialManager.INSTANCE.getByName(XmlUtil.parseString(atts, "material", "N/A"))
			);*/
			
			Rectangle rectangle = new Rectangle(atts);
			
			MeshManager.INSTANCE.register(rectangle);
		}
		else if (localName.equals("skybox"))
		{
			Skybox skybox = new Skybox(
					XmlUtil.parseString(atts, "name", "Skybox:N/A"),
					MaterialManager.INSTANCE.getByName(XmlUtil.parseString(atts, "material", "N/A")),
					XmlUtil.parseFloat(atts, "size", 30.0f)
			);
			
			MeshManager.INSTANCE.register(skybox);
		}
	}
	
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException 
	{
	}
}
