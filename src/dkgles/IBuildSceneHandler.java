package dkgles;

import dkgles.primitive.Rectangle;
import dkgles.ui.Touchable;

public interface IBuildSceneHandler
{
	public void onCameraCreated(Camera camera);
	
	public void onImmovableCreated(Immovable immovable);

	public void onMovableCreated(Movable movable);

	public void onRectangleCreated(Rectangle rectangle);
	
	public void onTouchableCreated(Touchable touchable);
	
	public void onSceneCreated(Scene scene);
	
	public void onSkyboxCreated(Skybox skybox);
}