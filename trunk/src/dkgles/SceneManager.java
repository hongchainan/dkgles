package dkgles;

public class SceneManager
{
	
	private SceneManager()
	{
		_root = new Movable("ROOT");
	}
	
	/**
	 * Get root reference;
	 * @return root
	 */
	public Movable root()
	{
		return _root;
	}
	
	public static SceneManager instance()
	{
		if (_instance==null)
		{
			_instance = new SceneManager();
		}
		
		return _instance;
	}
	
	private Movable _root;
	
	private static SceneManager _instance;
}