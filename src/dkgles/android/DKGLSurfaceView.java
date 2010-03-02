package dkgles.android;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import dkgles.ui.UIManager;

public class DKGLSurfaceView extends GLSurfaceView 
{
	public DKGLSurfaceView(Context context)
	{
		super(context);
	}
	
	public boolean onTouchEvent (MotionEvent event)
	{
		float x = event.getX();
		float y = event.getY();
		
		Log.v(CLASS_TAG, "touched!!" + x + ", " + y);
		
		UIManager.instance().touch(x, y);
		
		return true;
	}
	
	private static final String CLASS_TAG = "DKGLSurfaceView";
}
