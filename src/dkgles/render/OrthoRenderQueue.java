package dkgles.render;

import javax.microedition.khronos.opengles.GL10;

public class OrthoRenderQueue extends RenderQueue
{

	public OrthoRenderQueue(String name, int count, int layer)
	{
		super(name, count, layer);
	}
	
	protected void renderImpl(GL10 gl)
	{
		if (!_visible)
			return;
		
		OrthoRenderer.INSTANCE.beginRender(gl);
		for (Group g : _groups)
		{
			if (g!=null)
			{
				g.render(gl);
			}
		}
		OrthoRenderer.INSTANCE.endRender(gl);
	}

	
	
}