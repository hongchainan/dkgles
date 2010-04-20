package dkgles;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;
import dkgles.manager.MaterialManager;
import dkgles.primitive.Rectangle;
import dkgles.render.OrthoRenderQueue;
import dkgles.render.PerspectiveRenderQueue;
import dkgles.render.RenderQueue;

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
		_movable = null;
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
		//Log.v(TAG, "startElement");

		if (localName.equals("scene"))
		{
			if (_scene==null)
			{
				_scene = new Scene(parseString(atts, "name", "Scene:N/A"), null);
			}
			else
			{
				_scene.name(parseString(atts, "name", "Scene:N/A"));
			}

			_movable = _scene.root();	
		}
		else if (localName.equals("render_queue"))
		{
			RenderQueue renderQueue;
			String type = parseString(atts, "type", "perspective");
			
			if (type.equals("perspective"))
			{
				renderQueue = new PerspectiveRenderQueue(
					parseString(atts, "name", "N/A"),
					parseInt(atts, "count", 10),
					parseInt(atts, "layer", 3)
				);
			}
			else
			{
				renderQueue = new OrthoRenderQueue(
					parseString(atts, "name", "N/A"),
					parseInt(atts, "count", 10),
					parseInt(atts, "layer", 3)
				);
			}			

			_scene.attachRenderQueue(renderQueue);			
		}
		else if (localName.equals("movable"))
		{
			Movable movable = new Movable(parseString(atts, "name", "Movable:N/A"), _movable, _scene);

			float x = parseFloat(atts, "x", 0.0f);
			float y = parseFloat(atts, "y", 0.0f);
			float z = parseFloat(atts, "z", 0.0f);

			float pitch = parseFloat(atts, "pitch", 0.0f);

			movable.position(x, y, z);
			movable.pitch(pitch);

			_movable = movable;
			_movableStack.push(movable);

			// notify movable created
			_listener.onMovableCreated(movable);
			
		}
		else if (localName.equals("rectangle"))
		{
			Rectangle rectangle = new Rectangle(
				parseString(atts, "name", "Rectangle:N/A"),
				parseFloat(atts, "width", 0.0f),
				parseFloat(atts, "height", 0.0f),
				MaterialManager.instance().getByName(parseString(atts, "material", "N/A"))
			);

			_movable.setDrawable(rectangle);
		}
		else if (localName.equals("camera"))
		{
			Camera camera = new Camera(
				parseString(atts, "name", "Camera:N/A"),
				_movable,
				_scene
			);
			
			float x = parseFloat(atts, "x", 0.0f);
			float y = parseFloat(atts, "y", 0.0f);
			float z = parseFloat(atts, "z", 0.0f);
			
			camera.position(x, y, z);
			_scene.bindCamera(camera);

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
			_scene = null;
		}
		else if (localName.equals("render_queue"))
		{
			//_scene.attachRenderQueue(_renderQueue);
			//_renderQueue = null;
		}
		else if (localName.equals("movable"))
		{
			if (_movableStack.empty())
			{
				_movable = _scene.root();
			}
			else
			{
				_movable = _movableStack.pop();
			}
		}
	}

	int parseInt(Attributes atts, String name, int defaultVal)
	{
		int val;
		
		try
		{
			val = Integer.parseInt(atts.getValue(name));
		}
		catch(Exception e)
		{
			val = defaultVal;
		}
		
		return val;
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

	String parseString(Attributes atts, String name, String defaultStr)
	{
		String val;
		
		try
		{
			val = atts.getValue(name);
		}
		catch(Exception e)
		{
			val = defaultStr;
		}
		
		return val;
	}
	
	Stack<Movable> _movableStack;
	Movable _movable;
	Scene _scene;

	Listener _listener;

	static Listener _defaultListener;// = new Listener();
}













