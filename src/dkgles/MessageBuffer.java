package dkgles;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLException;
import android.util.Log;
import dkgles.primitive.Rectangle;

public class MessageBuffer extends Drawable
{
	public MessageBuffer(int bufferLen, FontSet fontSet, float fontSize)
	{
		super(null, 3);
		
		_fontSize 	= fontSize;
		_fontSet 	= fontSet;
		_fonts 		= new Font[bufferLen];
	}
	
	public void release()
	{
		if (_fonts!=null)
		{
			for (int i=0;i<_fonts.length;++i)
			{
				_fonts[i] = null;
			}
			_fonts = null;
		}
		
		if (_fontSet!=null)
		{
			_fontSet = null;
		}	
	}
	
	public synchronized void render(GL10 gl)
	{
		if (!_visible)
			return;
		
		try
		{
			gl.glPushMatrix();
			gl.glLoadMatrixf(_worldTransformation._matrix, 0);
			
			for (int i=0;i<_fonts.length;++i)
			{
				gl.glPushMatrix();
				gl.glTranslatef(_fontSize*i, 0.0f, 0.0f);

				if (_fonts[i]!=null)
				{
					gl.glScalef(_fontSize, _fontSize, 1.0f);
					_material.bindTexture(_fonts[i].texture());
					_fontMesh.setMaterial(0, _material);
					_fontMesh.renderImpl(gl);
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
		for (int i=0;i<_fonts.length;i++)
		{
			if (i<message.length())
			{
				_fonts[i] = _fontSet.get(message.charAt(i));
			}
		}
	}
	
	public void setFontSize(float fontSize)
	{
		_fontSize = fontSize;
	}
	
	public void setFontSet(FontSet fontSet)
	{
		_fontSet = fontSet;
	}
	
	private float 		_fontSize;
	private Font[]		_fonts;
	private FontSet 	_fontSet;
	
	private static Rectangle	_fontMesh = new Rectangle("RTG_Font", 1.0f, 1.0f,  null);
	private static Material 	_material = new Material("MAT_Font");
}