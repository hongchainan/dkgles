package dkgles.render;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;
import dkgles.Drawable;

public class RenderQueue
{
	public RenderQueue(String name, int count)
	{
		_name = name;
		_groups = new Group[count];
		
		for (int i=0;i<count;i++)
		{
			_groups[i] = new Group(i);
		}
		
		_groupCount = count;
	}
	
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
	 * 
	 * @param drawable
	 */
	public synchronized void removeDrawable(Drawable drawable)
	{
		Log.v(TAG, "removeDrawable:" + drawable);
		
		int id = drawable.groupID();
		_groups[id].remove(drawable);
	}
	
	/**
	 * 
	 * @param gl
	 */
	public synchronized void render(GL10 gl)
	{
		for (Group g : _groups)
		{
			if (g!=null)
			{
				g.render(gl);
			}
		}
	}
	
	public String toString()
	{
		return _name;
	}
	
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
		
		private int						_id;
		private ArrayList<Drawable> 	_drawables; 
	}
	
	private int 	_groupCount;
	private Group[]	_groups;
	private final String _name;
	private final static String TAG = "RenderQueue";
}