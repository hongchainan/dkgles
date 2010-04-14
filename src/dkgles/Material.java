package dkgles;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

/**
 *@author doki lin
 */
public class Material
{
	public Material(String name)
	{
		_name = name;
		rgba(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public Material(String name, float r, float g, float b, float a)
	{
		_name = name;
		rgba(r, g, b, a);
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
			Log.v("TAG", _name + "bind a dummy texture");
			_texture = Texture.GetDummyTexture();
		}
	}
	
	/**
	 *Apply this material to current render state
	 *Should always called in GLThread
	 */
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

	public void release()
	{
		_texture = null;
	}
	
	public static Material GetDummyMaterial()
	{
		return _dummy;
	}
	
	float _red;
	float _green;
	float _blue;
	float _alpha;
	Texture _texture;
	static DummyMaterial _dummy = new DummyMaterial("MAT_DUMMY");
	
	final static String TAG = "Material";
	final String _name;
}

class DummyMaterial extends Material
{
	public DummyMaterial(String name)
	{
		super(name);
	}

	public void apply(GL10 gl)
	{
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}
}
