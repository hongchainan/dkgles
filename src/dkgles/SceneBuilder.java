package dkgles;

import java.util.Stack;

import lost.kapa.XmlUtil;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import dkgles.manager.MaterialManager;
import dkgles.primitive.Rectangle;
import dkgles.render.OrthoRenderQueue;
import dkgles.render.PerspectiveRenderQueue;
import dkgles.render.RenderQueue;

/**
 * Build scene from xml config file
 * @author doki lin
 */
public class SceneBuilder extends DefaultHandler
{
	public class Listener
	{
		public Listener()
		{
			
		}
		
		
		public void onCameraCreated(Camera camera)
		{
		}

		public void onMovableCreated(Movable movable)
		{
		}

		public void onRectangleCreated(Rectangle rectangle)
		{
		}
	}

	public SceneBuilder(Scene scene, Listener listener)
	{
		_scene = scene;
		registerListener(listener);
	}

	public void registerListener(Listener listener)
	{
		if (listener!=null)
		{
			_listener = listener;
		}
		else
		{
			_listener = new Listener();
		}
	}	

	@Override
	public void startDocument() throws SAXException 
	{
		_movableStack = new Stack<Movable>();
		//_movable = null;
		//_scene = null;
		_listener = new Listener();
	}
	
	@Override
	public void endDocument() throws SAXException 
	{
		_movableStack = null;
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException 
	{
		if (localName.equals("scene"))
		{
			if (_scene==null)
			{
				_scene = new Scene(XmlUtil.parseString(atts, "name", "Scene:N/A"), null);
			}
			else
			{
				_scene.name(XmlUtil.parseString(atts, "name", "Scene:N/A"));
			}

			_movableStack.clear();
			_movableStack.push(_scene.root());	
		}
		else if (localName.equals("render_queue"))
		{
			RenderQueue renderQueue;
			String type = XmlUtil.parseString(atts, "type", "perspective");
			
			if (type.equals("perspective"))
			{
				renderQueue = new PerspectiveRenderQueue(
					XmlUtil.parseString(atts, "name", "N/A"),
					XmlUtil.parseInt(atts, "count", 10),
					XmlUtil.parseInt(atts, "layer", 3)
				);
			}
			else
			{
				renderQueue = new OrthoRenderQueue(
					XmlUtil.parseString(atts, "name", "N/A"),
					XmlUtil.parseInt(atts, "count", 10),
					XmlUtil.parseInt(atts, "layer", 3)
				);
			}			

			_scene.attachRenderQueue(renderQueue);			
		}
		else if (localName.equals("movable"))
		{
			Movable movable = new Movable(
				XmlUtil.parseString(atts, "name", "Movable:N/A"),
				_movableStack.peek(),
				_scene);
			
			parseMovableOptionalParam(movable, atts);

			_movableStack.push(movable);

			// notify movable created
			_listener.onMovableCreated(_movableStack.peek());
			
		}
		else if (localName.equals("rectangle"))
		{
			Rectangle rectangle = new Rectangle(
				XmlUtil.parseString(atts, "name", "Rectangle:N/A"),
				XmlUtil.parseFloat(atts, "width", 0.0f),
				XmlUtil.parseFloat(atts, "height", 0.0f),
				MaterialManager.instance().getByName(XmlUtil.parseString(atts, "material", "N/A"))
			);
			
			parseDrawableOptionalParam(rectangle, atts);

			_movableStack.peek().setDrawable(rectangle);
		}
		else if (localName.equals("camera"))
		{
			Camera camera = new Camera(
				XmlUtil.parseString(atts, "name", "Camera:N/A"),
				_movableStack.peek(),
				_scene
			);

			parseMovableOptionalParam(camera, atts);
			
			//_scene.bindCamera(camera);
			_listener.onCameraCreated(camera);
		}
	}
	
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException 
	{
		//Log.v(TAG, "endElement");
		
		if (localName.equals("scene"))
		{
			//SceneManager.instance().register(_scene);
			_movableStack.clear();
			_scene = null;
		}
		else if (localName.equals("render_queue"))
		{
			//_scene.attachRenderQueue(_renderQueue);
			//_renderQueue = null;
		}
		else if (localName.equals("movable"))
		{
			_movableStack.pop();
			/*
			if (_movableStack.empty())
			{
				_movable = _scene.root();
			}
			else
			{
				_movable = _movableStack.pop();
			}*/
		}
	}
	
	void parseDrawableOptionalParam(Drawable drawable, Attributes atts)
	{
		int group_id = XmlUtil.parseInt(atts, "group_id", 0);
		drawable.groupID(group_id);
	}

	void parseMovableOptionalParam(Movable movable, Attributes atts)
	{
		float x = XmlUtil.parseFloat(atts, "x", 0.0f);
		float y = XmlUtil.parseFloat(atts, "y", 0.0f);
		float z = XmlUtil.parseFloat(atts, "z", 0.0f);

		movable.position(x, y, z);

		float pitch = XmlUtil.parseFloat(atts, "pitch", 0.0f);
		movable.pitch(pitch);
	}

	
	
	Stack<Movable> _movableStack;
	Scene _scene;

	Listener _listener;

	static Listener _defaultListener;// = new Listener();
	static final String TAG = "SceneBuilder";
}













