package dkgles;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLException;
import android.util.Log;
import dkgles.primitive.Rectangle;

public class MessageBuffer extends Drawable
{
	public MessageBuffer(int bufferLen, FontSet fontSet, float fontSize, int depth)
	{
		super(null, depth);
		
		fontSize_ 	= fontSize;
		fontSet_ 	= fontSet;
		fonts_ 		= new Font[bufferLen];
	}
	
	public void release()
	{
		if (fonts_!=null)
		{
			for (int i=0;i<fonts_.length;++i)
			{
				fonts_[i] = null;
			}
			fonts_ = null;
		}
		
		if (fontSet_!=null)
		{
			fontSet_ = null;
		}	
	}
	
	public synchronized void render(GL10 gl)
	{
		if (!visibility_)
			return;
		
		try
		{
			gl.glPushMatrix();
			gl.glLoadMatrixf(worldTransformation_.matrix, 0);
			
			for (int i=0;i<fonts_.length;++i)
			{
				gl.glPushMatrix();
				gl.glTranslatef(fontSize_*i, 0.0f, 0.0f);

				if (fonts_[i]!=null)
				{
					gl.glScalef(fontSize_, fontSize_, 1.0f);
					material_.bindTexture(fonts_[i].texture());
					fontMesh_.setMaterial(0, material_);
					fontMesh_.renderImpl(gl);
				}
				gl.glPopMatrix();
			}
			gl.glPopMatrix();
		}
		catch(GLException e)
		{
			Log.e(TAG, "catch a GLException:" + e.getMessage());
			throw e;
		}
	}// End of render
	
	public void setMessage(String message)
	{
		for (int i=0;i<fonts_.length;i++)
		{
			fonts_[i] = null;
		}
		
		for (int i=0;i<fonts_.length;i++)
		{
			if (i<message.length())
			{
				fonts_[i] = fontSet_.get(message.charAt(i));
			}
			else
			{
				break;
			}
		}
	}
	
	public void setFontSize(float fontSize)
	{
		fontSize_ = fontSize;
	}
	
	public void setFontSet(FontSet fontSet)
	{
		fontSet_ = fontSet;
	}
	
	private float 		fontSize_;
	private Font[]		fonts_;
	private FontSet 	fontSet_;
	
	private static Rectangle	fontMesh_ = new Rectangle("RTG_Font", 1.0f, 1.0f,  null);
	private static Material 	material_ = new Material("MAT_Font");
}