package dkgles;

import dkgles.primitive.Rectangle;
import dkgles.ui.Touchable;

public interface IBuildSceneHandler
{
	public void onCameraCreated(Camera camera);

	public void onMovableCreated(Movable movable);

	public void onRectangleCreated(Rectangle rectangle);
	
	public void onTouchableCreated(Touchable touchable);
	
	public void onSceneCreated(Scene scene);
}