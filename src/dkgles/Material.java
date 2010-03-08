package dkgles;

import javax.microedition.khronos.opengles.GL10;

public class Material
{
	public Material(String name)
	{
		_name = name;
	}
	
	public void bindTexture(Texture texture)
	{
		_texture = texture;
	}
	
	public void apply(GL10 gl)
	{
		if (_texture!=null)
		{
			_texture.bind(gl);
		}
	}
	
	
	private final String _name;
	private Texture _texture;
	
}