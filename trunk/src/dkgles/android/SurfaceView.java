package dkgles.android;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import dkgles.ui.UIManager;

public class SurfaceView extends GLSurfaceView 
{
	public SurfaceView(Context context)
	{
		super(context);
		//setDebugFlags(DEBUG_CHECK_GL_ERROR);
	}


	
	public boolean onTouchEvent (MotionEvent event)
	{
		float x = event.getX();
		float y = event.getY();
		
		int action = event.getAction();
		
		if (action == MotionEvent.ACTION_DOWN)
		{
			Log.v(CLASS_TAG, "action down!!" + x + ", " + y);
		}
		else if (action == MotionEvent.ACTION_UP)
		{
			Log.v(CLASS_TAG, "action up");
		}
		
		UIManager.instance().touch(x, y);
		
		return true;
	}
	
	private static final String CLASS_TAG = "DKGLSurfaceView";
}
