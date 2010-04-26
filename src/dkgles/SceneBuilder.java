package dkgles;

import java.util.ArrayList;
import java.util.Stack;

import lost.kapa.ContextHolder;
import lost.kapa.XmlUtil;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import dkgles.manager.MaterialManager;
import dkgles.primitive.Rectangle;
import dkgles.render.OrthoRenderQueue;
import dkgles.render.PerspectiveRenderQueue;
import dkgles.render.RenderQueue;
import dkgles.ui.Touchable;
import dkgles.ui.UIManager;

/**
 * Build scene from xml config file
 * @author doki lin
 */
public class SceneBuilder extends DefaultHandler
{
	public SceneBuilder(Scene scene)
	{
		_scene = scene;
		_listeners = new ArrayList<IBuildSceneHandler>();
		//registerListener(listener);
	}
	
	public void build(int resId)
	{
		XmlUtil.parse(ContextHolder.INSTANCE.get(), this, resId);
	}

	public void registerListener(IBuildSceneHandler listener)
	{
		_listeners.add(listener);
		/*
		if (listener!=null)
		{
			_listener = listener;
		}
		else
		{
			_listener = new BuildSceneHandler();
		}*/
	}	

	@Override
	public void startDocument() throws SAXException 
	{
		_movableStack = new Stack<Movable>();
		
		if (_scene!=null)
		{
			_movableStack.push(_scene.root());
		}
		
		//_movable = null;
		//_scene = null;
		/*if (_listener==null)
		{
			_listener = new BuildSceneHandler();
		}*/
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
			for (IBuildSceneHandler listener : _listeners)
			{
				listener.onMovableCreated(_movableStack.peek());
			}
			//_listener.
			
		}
		else if (localName.equals("rectangle"))
		{
			Rectangle rectangle = new Rectangle(
				XmlUtil.parseString(atts, "name", "Rectangle:N/A"),
				XmlUtil.parseFloat(atts, "width", 0.0f),
				XmlUtil.parseFloat(atts, "height", 0.0f),
				MaterialManager.INSTANCE.getByName(XmlUtil.parseString(atts, "material", "N/A"))
			);
			
			parseDrawableOptionalParam(rectangle, atts);

			_movableStack.peek().setDrawable(rectangle);
		}
		else if (localName.equals("touchable"))
		{
			Touchable touchable = new Touchable(
					XmlUtil.parseString(atts, "name", "Touchable:N/A"),
					XmlUtil.parseFloat(atts, "width", 0.0f),
					XmlUtil.parseFloat(atts, "height", 0.0f),
					MaterialManager.INSTANCE.getByName(XmlUtil.parseString(atts, "material", "N/A"))
			);
			
			UIManager.INSTANCE.register(touchable);
			_movableStack.peek().setDrawable(touchable);
			
			for (IBuildSceneHandler listener : _listeners)
			{
				listener.onTouchableCreated(touchable);
			}
			
			//_listener.onTouchableCreated(touchable);
			
		}
		else if (localName.equals("camera"))
		{
			Camera camera = new Camera(
				XmlUtil.parseString(atts, "name", "Camera:N/A"),
				_movableStack.peek(),
				_scene
			);

			parseMovableOptionalParam(camera, atts);
			_camera = camera;
			//_scene.bindCamera(camera);
			
			for (IBuildSceneHandler listener : _listeners)
			{
				listener.onCameraCreated(camera);
			}
			
			//_listener.onCameraCreated(camera);
		}
		else if (localName.equals("skybox"))
		{
			Skybox skybox = new Skybox(
					XmlUtil.parseString(atts, "name", "Skybox:N/A"),
					MaterialManager.INSTANCE.getByName(XmlUtil.parseString(atts, "material", "N/A")),
					XmlUtil.parseFloat(atts, "size", 30.0f)
			);
			
			_camera.setDrawable(skybox);
					
					
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
		}
		else if (localName.equals("camera"))
		{
			_camera = null;
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
	Camera _camera;
	Scene _scene;

	ArrayList<IBuildSceneHandler> _listeners;
	static final String TAG = "SceneBuilder";
}













