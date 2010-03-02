package dkgles;

public class DrawableQueue
{
	static private DrawableQueue _instance = new DrawableQueue();
	//private Queue<Drawable> _queue;

	private DrawableQueue()
	{
		//_queue = new Queue<Drawable>();
	}
	
	
	public static DrawableQueue instance()
	{
		return _instance;
	}
	
	public void add(Drawable drawable)
	{
		//_queue.add(drawable);
	}
}