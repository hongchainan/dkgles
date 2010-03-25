package dkgles;

import javax.microedition.khronos.opengles.GL10;

public class Material
{
	public Material(String name)
	{
		_name = name;
		_red = _green = _blue = _alpha = 1.0f;
	}
	
	public void red(float val)
	{
		_red = val;
	}
	
	public float red()
	{
		return _red;
	}
	
	public void green(float val)
	{
		_green = val;	
	}
	
	public float green()
	{
		return _green;
	}
	
	public void blue(float val)
	{
		_blue = val;
	}
	
	public float blue()
	{
		return _blue;
	}
	
	/**
	 *
	 */
	public void alpha(float val)
	{
		_alpha = val;
	}
	
	public float alpha()
	{
		return _alpha;
	}
	
	public void bindTexture(Texture texture)
	{
		_texture = texture;
	}
	
	public void apply(GL10 gl)
	{
		gl.glColor4f(_red, _green, _blue, _alpha);
		if (_texture!=null)
		{
			_texture.bind(gl);
		}
	}
	
	public String name()
	{
		return _name;
	}
	
	public String toString()
	{
		return _name;
	}
	
	private float _red;
	private float _green;
	private float _blue;
	private float _alpha;
	
	private final static String TAG = "Material";
	private final String _name;
	private Texture _texture;
	
}