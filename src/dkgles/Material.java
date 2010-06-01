package dkgles;

import javax.microedition.khronos.opengles.GL10;

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

	public Texture texture()
	{
		return _texture;
	}
	
	public void bindTexture(Texture texture)
	{
		_texture = texture;
		
		/*if (texture!=null)
		{
			_texture = texture;
		}
		else
		{
			Log.v("TAG", _name + "bind a dummy texture");
			_texture = Texture.GetDummyTexture();
		}*/
	}
    
    public void setBackFaceCulling(boolean val)
    {
        _backFaceCulling = val;
    }
    
    public boolean backFaceCulling()
    {
        return _backFaceCulling;
    }
	
	/**
	 *Apply this material to current render state
	 *Should always called in GLThread
	 */
	public void beforeApply(GL10 gl)
	{
		gl.glColor4f(_red, _green, _blue, _alpha);
		
		if (_texture!=null)
		{
			gl.glEnable(GL10.GL_TEXTURE_2D);
			_texture.bind(gl);
			
			if (null!=_uvanimation)
			{
				_uvanimation.pre(gl);
			}
		}
		else
		{
			gl.glDisable(GL10.GL_TEXTURE_2D);
		}
	}
    
    public void aferApply(GL10 gl)
    {
        if (_texture!=null)
        {
        	if (null!=_uvanimation)
        	{
        		_uvanimation.post(gl);
        	}
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

	public void release()
	{
		_texture = null;
		
		if (_uvanimation!=null)
		{
			_uvanimation.release();
			_uvanimation = null;
		}
	}
	
	public void setUVAnimation(UVAnimation animation)
	{
		_uvanimation = animation;
	}
	
	public static Material GetDummyMaterial()
	{
		return _dummy;
	}
	
	private float   _red;
	private float   _green;
	private float   _blue;
	private float   _alpha;
	private Texture _texture;
	
	private UVAnimation _uvanimation;
    
    private boolean _backFaceCulling = true;
    
	static DummyMaterial _dummy = new DummyMaterial("MAT_DUMMY");
	
	public  final static String TAG = "Material";
	private final String _name;
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
