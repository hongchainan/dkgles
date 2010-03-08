package dkgles;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

public class TextureFactory
{

	static public Texture create(GL10 gl, InputStream is)
	{
		Bitmap bitmap;
		bitmap = BitmapFactory.decodeStream(is);
        
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		int[] id = new int[1];
    gl.glGenTextures(1, id, 0);
        
    // Set default parameters
    gl.glBindTexture(GL10.GL_TEXTURE_2D, id[0]);

    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_LINEAR);
    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_LINEAR);
    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
        		GL10.GL_REPEAT);
    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
        		GL10.GL_REPEAT);
    gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
        		GL10.GL_REPLACE);
        
    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
    bitmap.recycle();
        
		return new Texture(id[0]);
	}
	
}
