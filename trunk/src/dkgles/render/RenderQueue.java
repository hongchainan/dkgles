package dkgles.render;

import java.util.ArrayList;
import java.util.Collections;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;
import dkgles.Camera;
import dkgles.Drawable;

/**
 * RenderQueue is handled by Scene Object. End user should not operate them directly.
 */
public abstract class RenderQueue implements Comparable<RenderQueue>
{
	public final static int UI_LAYER = 100;
	public final static int POST_EFFECT_LAYER = 1000;
	
	/**
	 *@param name name for RenderQueue
	 *@param count number of group count
	 *@param layer layer depth
	 */
	public RenderQueue(final String name, int count, int layer)
	{
		_name = name;
		_layer = layer;
		_groupCount = count;
		_visible = true;
		
		_groups = new Group[count];
		
		for (int i=0;i<count;i++)
		{
			_groups[i] = new Group(i);
		}
		
		synchronized(RenderQueue.class)
		{
			_renderQueueList.add(this);
			Collections.sort(_renderQueueList);
		}
	}

	/**
	 * Release this render queue
	 */
	public void release()
	{

		for (int i=0;i<_groupCount;i++)
		{
			_groups[i].release();
			_groups[i] = null;
		}

		_groups = null;		

		synchronized(RenderQueue.class)
		{
			_renderQueueList.remove(this);
			Collections.sort(_renderQueueList);
		}
	}
	
	/**
	 * Used in sort function
	 */
	public int compareTo(RenderQueue another)
	{
		return _layer - another._layer;
	}
	
	/**
	 *@deprecated
	 */
	public void bindCamera(Camera camera)
	{
		_camera = camera;
	}
	
	/**
	 *Set visibility
	 */
	public synchronized void visibility(boolean b)
	{
		_visible = b;
	}
	
	public synchronized static void renderAll(GL10 gl)
	{
		for (RenderQueue r : _renderQueueList)
		{
			r.render(gl);
		}
	}
	
	/**
	 *Add drawable to this render queue
	 */
	public synchronized void addDrawble(Drawable drawable)
	{
		int id = drawable.groupID();
		
		Log.v(TAG, "addDrawable:" + drawable);
		
		if (id<_groupCount)
		{
			_groups[id].add(drawable);
		}
		else
		{
			
		}
	}
	
	/**
	 * Remove drawables
	 * @param drawable
	 */
	public synchronized void removeDrawable(Drawable drawable)
	{
		Log.v(TAG, "removeDrawable:" + drawable);
		
		int id = drawable.groupID();
		_groups[id].remove(drawable);
	}
	
	/**
	 * Called in GLThread
	 * @param gl
	 */
	public void render(GL10 gl)
	{
		if (!_visible)
			return;
		
		if (_camera!=null)
		{
			gl.glLoadMatrixf(_camera.viewMatrix(), 0);
		}
		
		renderImpl(gl);
	}
	
	protected abstract void renderImpl(GL10 gl);
	
	public String toString()
	{
		return _name;
	}

	static List<RenderQueue> _renderQueueList = new List<RenderQueue>();

	
	/**
	 * Group can hava arbitrary number of drawables
	 */
	class Group
	{
		public Group(int id)
		{
			_id = id;
			_drawables = new ArrayList<Drawable>();
		}
		
		public int id()
		{
			return _id;
		}
		
		public void add(Drawable drawable)
		{
			_drawables.add(drawable);
		}
		
		public void remove(Drawable drawable)
		{
			_drawables.remove(drawable);
		}
		
		public void render(GL10 gl)
		{
			for (Drawable d : _drawables)
			{
				if (d!=null)
				{
					gl.glPushMatrix();
					d.render(gl);
					gl.glPopMatrix();
				}
			}
		}
		
		int	_id;
		ArrayList<Drawable> 	_drawables; 
	}
	
	static ArrayList<RenderQueue> _renderQueueList = new ArrayList<RenderQueue>();
	protected boolean _visible;
	Camera	_camera;
	int 	_groupCount;
	int _layer;
	protected Group[]	_groups;
	final String _name;
	final static String TAG = "RenderQueue";
}
