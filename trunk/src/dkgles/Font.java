package dkgles;

public class Font
{
	public Font(char ch, Texture texture)
	{
		_char = ch;
		_texture = texture;
		
	}
	
	public void setTexture(Texture texture)
	{
		_texture = texture;
	}
	
	public Texture texture()
	{
		return _texture;
	}
	
	private char	_char;
	private Texture _texture;
}