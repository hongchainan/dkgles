package dkgles;

import javax.microedition.khronos.opengles.GL10;

import lost.kapa.XmlUtil;

import org.xml.sax.Attributes;

import dkgles.manager.TextureManager;

/**
 *@author doki lin
 */
public class Material implements Cloneable
{
	public Material(String name)
	{
		name_ = name;
		setRGBA(1.0f, 1.0f, 1.0f, 1.0f);
	}
	
	public Material(String name, float r, float g, float b, float a)
	{
		name_ = name;
		setRGBA(r, g, b, a);
	}
	
	public Material(Attributes atts)
	{
		name_ 	= XmlUtil.parseString(atts, "name", "N/A");
		red_  	= XmlUtil.parseFloat(atts, "red", 	1.0f);
		green_ 	= XmlUtil.parseFloat(atts, "green", 1.0f);
		blue_ 	= XmlUtil.parseFloat(atts, "blue", 	1.0f);
		alpha_ 	= XmlUtil.parseFloat(atts, "alpha", 1.0f);
		
		String textureName = XmlUtil.parseString(atts, "texture_name", "");
		
		if (!textureName.equals(""))
		{
			texture_ = TextureManager.INSTANCE.getByName(textureName);
		}
	}
	
	public void setRGB(float r, float g, float b)
	{
		red_ 	= r;
		green_ 	= g;
		blue_ 	= b;
	}
	
	public void setRGBA(float r, float g, float b, float a)
	{
		red_ 	= r;
		green_ 	= g;
		blue_ 	= b;
		alpha_ 	= a;
	}
	
	public void red(float val)
	{
		red_ = val;
	}
	
	public float red()
	{
		return red_;
	}
	
	public void green(float val)
	{
		green_ = val;	
	}
	
	public float green()
	{
		return green_;
	}
	
	public void blue(float val)
	{
		blue_ = val;
	}
	
	public float blue()
	{
		return blue_;
	}
	
	/**
	 *
	 */
	public void alpha(float val)
	{
		alpha_ = val;
	}
	
	public float alpha()
	{
		return alpha_;
	}

	public Texture texture()
	{
		return texture_;
	}
	
	public void bindTexture(Texture texture)
	{
		texture_ = texture;
	}
    
    public void setBackFaceCulling(boolean val)
    {
        backFaceCulling_ = val;
    }
    
    public boolean backFaceCulling()
    {
        return backFaceCulling_;
    }
	
	/**
	 *Apply this material to current render state
	 *Should always called in GLThread
	 */
	public void beforeApply(GL10 gl)
	{
		gl.glColor4f(red_, green_, blue_, alpha_);
		
		if (texture_!=null)
		{
			gl.glEnable(GL10.GL_TEXTURE_2D);
			texture_.bind(gl);
			
			if (null!=uvanimation_)
			{
				uvanimation_.pre(gl);
			}
		}
		else
		{
			gl.glDisable(GL10.GL_TEXTURE_2D);
		}
	}
    
    public void aferApply(GL10 gl)
    {
        if (texture_!=null)
        {
        	if (null!=uvanimation_)
        	{
        		uvanimation_.post(gl);
        	}
        }
    }
	
	public String name()
	{
		return name_;
	}
	
	public String toString()
	{
		return name_;
	}

	public void release()
	{
		texture_ = null;
		
		if (uvanimation_!=null)
		{
			uvanimation_.release();
			uvanimation_ = null;
		}
	}
	
	public void setUVAnimation(UVAnimation animation)
	{
		uvanimation_ = animation;
	}
	
	public static Material GetDummyMaterial()
	{
		return dummy_;
	}
	
	public Material clone()
	{
		Material material = new Material(name_, red_, green_, blue_, alpha_);
		material.texture_ = this.texture_;
		
		return material;
	}
	
	private float   red_;
	private float   green_;
	private float   blue_;
	private float   alpha_;
	private Texture texture_;
	
	private UVAnimation uvanimation_;
    
    private boolean backFaceCulling_ = true;
    
	static DummyMaterial dummy_ = new DummyMaterial("MAT_DUMMY");
	
	public  final static String TAG = "Material";
	private String name_;
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
