package dkgles.render;

import javax.microedition.khronos.opengles.GL10;

public class PerspectiveRenderQueue extends RenderQueue
{

	public PerspectiveRenderQueue(String name, int count, int layer) 
	{
		super(name, count, layer);
	}
	
	protected synchronized void renderImpl(GL10 gl)
	{
		if (!_visible)
			return;
		
		for (Group g : _groups)
		{
			if (g!=null)
			{
				g.render(gl);
			}
		}
	}
	
	
}