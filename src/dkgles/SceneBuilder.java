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
		scene_ = scene;
		listeners_ = new ArrayList<IBuildSceneListener>();
	}
	
	public void build(int resId)
	{
		XmlUtil.parse(ContextHolder.INSTANCE.get(), this, resId);
	}

	public void registerListener(IBuildSceneListener listener)
	{
		listeners_.add(listener);
	}
	
	public void unregisterListener(IBuildSceneListener listener)
	{
		listeners_.remove(listener);
	}
	
	public void unregisterAllListener()
	{
		listeners_.clear();
	}
	
	@Override
	public void startDocument() throws SAXException 
	{
		movableStack_ = new Stack<Movable>();
		
		//if (scene_!=null)
		//{
			//movableStack_.push(scene_.root());
		//}
	}
	
	@Override
	public void endDocument() throws SAXException 
	{
		movableStack_.clear();
		movableStack_ = null;
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException 
	{
		if (localName.equals("scene"))
		{
			if (scene_==null)
			{
				scene_ = new Scene(XmlUtil.parseString(atts, "name", "Scene:N/A"), null);
			}
			else
			{
				scene_.setName(XmlUtil.parseString(atts, "name", "Scene:N/A"));
			}

			movableStack_.clear();
			movableStack_.push(scene_.root());	
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

			scene_.attachRenderQueue(renderQueue);			
		}
		else if (localName.equals("movable"))
		{
			Movable movable = new Movable(
				XmlUtil.parseString(atts, "name", "Movable:N/A"),
				movableStack_.peek(),
				scene_);
			
			parseMovableOptionalParam(movable, atts);

			movableStack_.push(movable);
			curNodeType_ = MOVABLE;

			
		}
		else if (localName.equals("drawable"))
		{
			String meshName = XmlUtil.parseString(atts, "mesh", "Mesh:N/A");
			
			Drawable drawable = new Drawable(
					MeshManager.INSTANCE.getByName(meshName),
					XmlUtil.parseInt(atts, "group_id", 0));
			
			if (curNodeType_==IMMOVABLE)
			{
				immovable_.setDrawable(drawable);
			}
			else
			{
				movableStack_.peek().setDrawable(drawable);
			}
		}
		else if (localName.equals("touchable"))
		{
			Rectangle rect = (Rectangle)MeshManager.INSTANCE.getByName(
					XmlUtil.parseString(atts, "mesh", "Mesh:N/A"));
			
			touchable_ = new Touchable(
					XmlUtil.parseString(atts, "name", "Touchable:N/A"),
					rect);
			
			UIManager.INSTANCE.register(touchable_);
			movableStack_.peek().setDrawable(touchable_);
		}
		else if (localName.equals("camera"))
		{
			camera_ = new Camera(
				XmlUtil.parseString(atts, "name", "Camera:N/A"),
				movableStack_.peek(),
				scene_
			);

			parseMovableOptionalParam(camera_, atts);
		}
		else if (localName.equals("skybox"))
		{
			Skybox skybox = new Skybox(
					XmlUtil.parseString(atts, "name", "Skybox:N/A"),
					MaterialManager.INSTANCE.getByName(XmlUtil.parseString(atts, "material", "N/A")),
					XmlUtil.parseFloat(atts, "size", 30.0f)
			);
			
			Drawable drawable = new Drawable("");
			drawable.setMesh(skybox);
			
			//_camera.setDrawable(skybox);
			camera_.setDrawable(drawable);
		}
		else if (localName.equals("immovable"))
		{
			String name = XmlUtil.parseString(atts, "name", "Immovable:N/A");
			float x = XmlUtil.parseFloat(atts, "x", 0);
			float y = XmlUtil.parseFloat(atts, "y", 0);
			float z = XmlUtil.parseFloat(atts, "z", 0);
			
			float pitch = XmlUtil.parseFloat(atts, "pitch",	0);
			float yaw 	= XmlUtil.parseFloat(atts, "yaw",	0);
			float roll 	= XmlUtil.parseFloat(atts, "roll",	0);
			
			Transformation t = new Transformation();
			t.rotateInLocalSpace(pitch, 1, 0, 0);
			t.rotateInLocalSpace(roll, 	0, 0, 1);
			t.rotateInLocalSpace(yaw, 	0, 1, 0);
			t.matrix[12] = x;
			t.matrix[13] = y;
			t.matrix[14] = z;
			
			immovable_ = new Immovable(name, t, scene_);
			curNodeType_ = IMMOVABLE;
		}
	}
	
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException 
	{
		//Log.v(TAG, "endElement");
		
		if (localName.equals("scene"))
		{
			int id = SceneManager.INSTANCE.register(scene_);
			movableStack_.clear();
			for (IBuildSceneListener listener : listeners_)
			{
				listener.onSceneCreated(id, scene_);
			}
			
			scene_ = null;
		}
		else if (localName.equals("render_queue"))
		{
			//_scene.attachRenderQueue(_renderQueue);
			//_renderQueue = null;
		}
		else if (localName.equals("movable"))
		{
			// notify movable created
			for (IBuildSceneListener listener : listeners_)
			{
				listener.onMovableCreated(movableStack_.peek());
			}
			
			movableStack_.pop();
		}
		else if (localName.equals("camera"))
		{
			for (IBuildSceneListener listener : listeners_)
			{
				listener.onCameraCreated(camera_);
			}
			
			camera_ = null;
		}
		else if (localName.equals("immovable"))
		{
			for (IBuildSceneListener listener : listeners_)
			{
				listener.onImmovableCreated(immovable_);
			}
			
			immovable_ = null;
			curNodeType_ = MOVABLE;
		}
		else if (localName.equals("touchable"))
		{
			for (IBuildSceneListener listener : listeners_)
			{
				listener.onTouchableCreated(touchable_);
			}
			
			touchable_ = null;
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
		
		float yaw = XmlUtil.parseFloat(atts, "yaw", 0.0f);
		movable.yaw(yaw);
		
		float roll = XmlUtil.parseFloat(atts, "roll", 0.0f);
		movable.roll(roll);
	}

	
	
	Stack<Movable> movableStack_;
	
	private Touchable touchable_;
	
	Immovable immovable_;
	Camera camera_;
	Scene scene_;
	int curNodeType_;
	
	final static int MOVABLE 	= 0;
	final static int IMMOVABLE 	= 1;

	ArrayList<IBuildSceneListener> listeners_;
	static final String TAG = "SceneBuilder";
}













