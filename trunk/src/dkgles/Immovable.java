package dkgles;

public class Immovable
{
	public Immovable(String name, Transformation transformation)
	{
		_name = name;
		_transformation = transformation;
		_childList = new ArrayList<Immovable>(); 
		
	}
	
	
	public Immovable createChild(String name, Transformation transformation)
	{
		Immovable child = new Immovable(name, transformation);
		addChild(child);
		return child;
	}
	
	
	public void addChild(Immovable child)
	{
		child.setParent(this);
		_childList.add(child);
	}
	
	public Transformation worldTransformation()
	{
		return _worldTransformationCache;
	}
	
	
	private void setParent(Immovable parent)
	{
		_parent = parent;
		_worldTransformationCache.mul(parent.worldTransformation(), _transformation);
	}
	
	public void setDrawable(Drawable drawable)
	{
		_drawable = drawable;
		_drawable.setWorldTransformation(_worldTransformationCache);
	}
	
	private Immovable					_parent;
	private Drawable 					_drawable;
	private String 						_name;
	private Transformation 		_transformation;
	private List<Immovable> 	_childList;
	protected Transformation 	_worldTransformationCache;
}