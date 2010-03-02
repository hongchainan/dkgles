package dkgles.render;

import javax.microedition.khronos.opengles.GL10;

public class DepthLayeredRenderer
{
	public DepthLayeredRenderer(int numOfLayers)
	{
		_numOfLayers = numOfLayers;
		_depthLayers = new DepthLayer[_numOfLayers];
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
	
	
	
	private DepthLayer[] _depthLayers;
	
	private int _numOfLayers;
}