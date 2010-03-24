package dkgles;

public class Scene
{
	public Scene(String name)
	{
		_name = name;
		_root = new Movable("Root in " + _name);
	}
	
	
	public Movable root()
	{
		return _root;
	}
	
	
	public String toString()
	{
		return "Scene: " + _name;
	}
	
	private Movable			_root;
	private final String 	_name;
}