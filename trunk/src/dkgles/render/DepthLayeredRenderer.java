package dkgles.render;

import javax.microedition.khronos.opengles.GL10;

public class DepthLayeredRenderer
{
	
	public DepthLayeredRenderer instance()
	{
		if (_instance==null)
		{
			_instance = new DepthLayeredRenderer();
		}
		
		return _instance;
	}
	
	private DepthLayeredRenderer(int numOfLayers)
	{
		_numOfLayers = numOfLayers;
		_depthLayers = new DepthLayer[_numOfLayers];
		
		for (int i=0;i<_numOfLayers;i++)
		{
			_depthLayers[i] = new DepthLayer(i);
		}
	}
	
	public void addDrawableToLayer(Drawable drawable, int layer)
	{
		_depthLayers[layer].addDrawble(drawable);
	}
	
	
	DepthLayer getDepthLayer(int layer)
	{
		return _depthLayers[layer];
	}
	
	
	void render(GL10 gl)
	{
		for (DepthLayer layer : _depthLayers)
		{
			layer.render(gl);
		}
	}
	
	private static DepthLayeredRenderer _instance;
	private DepthLayer[] _depthLayers;
	private int _numOfLayers;
}