package dkgles;

import android.os.SystemClock;
import android.view.animation.AlphaAnimation;
import android.view.animation.LinearInterpolator;
import dkgles.manager.MaterialManager;
import dkgles.primitive.Rectangle;
import dkgles.render.OrthoRenderQueue;
import dkgles.render.RenderQueue;

public class PostProcessEffect extends Scene
{
	public void startFadeIn(long duration)
	{
		_animation = new AlphaAnimation(1.0f, 0.0f);
		_animation.setDuration(duration);
		_animation.setInterpolator(new LinearInterpolator());
		_animation.start();
		visibility(true);
	}
	
	public synchronized void updateSceneGraph()
	{
		super.updateSceneGraph();
		
		if (_animation!=null)
		{
			android.view.animation.Transformation t = new android.view.animation.Transformation();
			_animation.getTransformation(SystemClock.uptimeMillis(), t);
			_material.alpha(t.getAlpha());
			
			if (_animation.hasEnded())
			{
				visibility(false);
				_animation = null;
			}
		}
	}
	
	PostProcessEffect()
	{
		super("PPEScene", new OrthoRenderQueue("PPERenderQueue", 1, RenderQueue.POST_EFFECT_LAYER));
		MaterialManager matmgr = MaterialManager.INSTANCE;
		int mid = matmgr.create("MAT_PP_EFFECT", null);
		_material = matmgr.get(mid);
		_material.rgb(.0f,.0f,.0f);
		_sprite = new Rectangle("PPEffectSprite", 1.33f, 1.0f, matmgr.get(mid));	
		//root().setDrawable(_sprite);
		visibility(false);
	}
	
	public static PostProcessEffect instance()
	{
		if (_instance==null)
		{
			_instance = new PostProcessEffect();
		}
		return _instance;
	}
	
	AlphaAnimation 	_animation;
	Rectangle 		_sprite;
	Material  		_material;
	static PostProcessEffect _instance; 
}