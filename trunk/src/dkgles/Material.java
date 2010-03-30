package dkgles;

import javax.microedition.khronos.opengles.GL10;

public class Material
{
	public Material(String name)
	{
		_name = name;
		rgba(1.0f, 1.0f, 1.0f, 1.0f);
	}
	
	public void rgb(float r, float g, float b)
	{
		_red = r;
		_green = g;
		_blue = b;
	}
	
	public void rgba(float r, float g, float b, float a)
	{
		_red 	= r;
		_green 	= g;
		_blue 	= b;
		_alpha 	= a;
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
		if (texture!=null)
		{
			_texture = texture;
		}
		else
		{
			_texture = Texture.GetDummyTexture();
		}
	}
	
	public void apply(GL10 gl)
	{
		gl.glColor4f(_red, _green, _blue, _alpha);
		_texture.bind(gl);
	}
	
	public String name()
	{
		return _name;
	}
	
	public String toString()
	{
		return _name;
	}
	
	public static GetDummyMaterial()
	{
		return _dummy;
	}
	
	private float _red;
	private float _green;
	private float _blue;
	private float _alpha;
	private Texture _texture;
	private static DummyMaterial _dummy = new DummyMaterial("MAT_DUMMY");
	
	private final static String TAG = "Material";
	private final String _name;
}

class DummyMaterial extends Material
{
	public void apply(GL10 gl)
	{
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}
}