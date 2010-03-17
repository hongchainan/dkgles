package dkgles;

import javax.microedition.khronos.opengles.GL10;

public class Material
{
	public Material(String name)
	{
		_name = name;
		_red = _green = _blue = _alpha = 1.0f;
	}
	
	public void bindTexture(Texture texture)
	{
		_texture = texture;
	}
	
	/**
	 *
	 */
	public void alpha(float alpha)
	{
		_alpha = alpha;
	}
	
	public void apply(GL10 gl)
	{
		gl.glColor4f(_r, _g, _b, _a);
		
		if (_texture!=null)
		{
			_texture.bind(gl);
		}
	}
	
	private float _red;
	private float _green;
	private float _blue;
	private float _alpha;
	
	private final String _name;
	private Texture _texture;
	
}