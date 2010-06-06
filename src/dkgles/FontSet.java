package dkgles;

import java.util.HashMap;

import dkgles.manager.TextureManager;



public class FontSet implements TextureManager.IListener
{
	public FontSet(String name, int rsc_id)
	{
		_name = name;
		_textureTag = new StringBuffer("TEX_");
		_textureTag.append(name);
		_textureTag.append("_");
		
		_fontMap = new HashMap<Character, Font>();
		
		TextureManager.INSTANCE.registerListener(this);
		TextureManager.INSTANCE.parse(rsc_id);
		TextureManager.INSTANCE.unregisterListener(this);
	}
	
	public void onCreated(int id, Texture texture) 
	{
		if (texture.name().contains(_textureTag))
		{
			char ch = texture.name().charAt(_textureTag.length());
			_fontMap.put(Character.valueOf(ch), new Font(ch, texture));
		}
	}
	
	public Font get(char ch)
	{
		return _fontMap.get(Character.valueOf(ch));
	}
	
	private String _name;
	private StringBuffer _textureTag;
	private HashMap<Character, Font>	_fontMap;
	
}