package dkgles.render;

import java.util.ArrayList;
import javax.microedition.khronos.opengles.GL10;

import dkgles.Drawable;

public class DepthLayer
{
	public DepthLayer(int layer)
	{
		_layer = layer;
		_drawables = new ArrayList<Drawable>();
	}
	
	public void addDrawble(Drawable drawable)
	{
		_drawables.add(drawable);
	}
	
	
	public void render(GL10 gl)
	{
		for (Drawable drawable : _drawables)
		{
			drawable.render(gl);
		}
	}
	
	private int 		_layer;
	private ArrayList<Drawable> 	_drawables;
}