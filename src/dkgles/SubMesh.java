package dkgles;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

public class SubMesh
{
	public final static int DRAW_ARRAY 		= 0;
	public final static int DRAW_ELEMENT 	= 1;
	
	public final static int TRIANGLE_STRIP 	= GL10.GL_TRIANGLE_STRIP;
	public final static int TRIANGLES 		= GL10.GL_TRIANGLES;
	
	/**
	 * 
	 * @param name a human readable string for debugging
	 * @param drawMode could be SubMesh.DRAW_ARRAY or SubMesh.DRAW_ELEMENT  
	 * @param material
	 * @deprecated
	 */
	public SubMesh(final String name, final int drawMode, final Material material)
	{
		_name 		= name;
		_drawMode 	= drawMode;
		
		if (_drawMode == DRAW_ARRAY)
		{
			_renderImpl = new DrawArrayImpl();
		}
		else
		{
			_renderImpl = new DrawElementImpl();	
		}
		
		_renderImpl.primType(TRIANGLE_STRIP);
		setMaterial(material);
	}
	
	public SubMesh(final String name, final int drawMode, int primType, final Material material)
	{
		_name 		= name;
		_drawMode 	= drawMode;
		
		if (_drawMode == DRAW_ARRAY)
		{
			_renderImpl = new DrawArrayImpl();
		}
		else
		{
			_renderImpl = new DrawElementImpl();	
		}
		
		_renderImpl.primType(primType);
		
		setMaterial(material);
	}
	
	public void release()
	{
		//TODO
	}
	
	public void setMaterial(Material material)
	{
		if (material!=null)
		{
			_material = material;
		}
		else
		{
			_material = Material.GetDummyMaterial();
		}
	}
	
	public Material getMaterial()
	{
		return _material;
	}
	
	public void renderImpl(GL10 gl)
	{
		_material.beforeApply(gl);
		_renderImpl.render(gl);
		_material.aferApply(gl);
	}
	
	public void setVertices(float[] vertices)
	{
		_renderImpl.setVertices(vertices);
	}
	
	public void setIndices(short[] indices)
	{
		_renderImpl.setIndices(indices);
  	}
	
	public void setFaceList(int[] flist)
	{
		_renderImpl.setFaceList(flist);
	}
	
	public void setTexcoords(float[] texcoords)
	{
		_renderImpl.setTexcoords(texcoords);
  	}
	
	public String toString()
	{
		return _name;
	}
	
	private RenderImpl		_renderImpl;
	protected Material 		_material;
	private final String 	_name;
	private final int		_drawMode;

}

abstract class RenderImpl
{
	public abstract void render(GL10 gl);
	
	public void setTexcoords(float[] texcoords)
	{
		// short is 2 bytes, therefore we multiply the number if
		// vertices with 2.
		ByteBuffer tbb = ByteBuffer.allocateDirect(texcoords.length * 4);
		tbb.order(ByteOrder.nativeOrder());
		
		FloatBuffer texBuf;
		texBuf = tbb.asFloatBuffer();
		texBuf.put(texcoords);
		texBuf.position(0);
		
		setTextureBuffer(texBuf);
  	}
	
	public void setFaceList(int[] faceList)
	{
		_faceList = faceList;
	}
  	
  	public void setVertices(float[] vertices)
	{
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		
		FloatBuffer vertBuf;
		vertBuf = vbb.asFloatBuffer();
		vertBuf.put(vertices);
		vertBuf.position(0);
		
		setVerticeBuffer(vertBuf);
	}
	
	public void setIndices(short[] indices)
	{
		// short is 2 bytes, therefore we multiply the number if
		// vertices with 2.
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		
		ShortBuffer idxBuf;
		
		idxBuf = ibb.asShortBuffer();
		idxBuf.put(indices);
		idxBuf.position(0);
		
		setIndicesBuffer(idxBuf);
  	}
	
	protected void enableClientState(GL10 gl)
	{
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		gl.glVertexPointer(3, 	GL10.GL_FLOAT, 0, getVerticeBuffer());
		
		FloatBuffer fb = getTextureBuffer();
		
		if (fb!=null)
		{
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, fb);
		}
	}
	
	protected void disableClientState(GL10 gl)
	{
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}
	
	public void primType(int type)
	{
		_primType = type;
	}
	
	protected int _primType;
	
	protected int[]	_faceList;
	
  	protected abstract void setTextureBuffer(FloatBuffer buffer);
  	protected abstract void setVerticeBuffer(FloatBuffer buffer);
  	protected abstract void setIndicesBuffer(ShortBuffer buffer);
  	
  	protected abstract FloatBuffer getTextureBuffer();
  	protected abstract FloatBuffer getVerticeBuffer();
  	protected abstract ShortBuffer getIndicesBuffer();
}	

class DrawArrayImpl extends RenderImpl
{
	public void render(GL10 gl)
	{
		enableClientState(gl);
		
		if (_faceList==null)
		{
			//gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, _vcount);
			gl.glDrawArrays(_primType, 0, _vcount);
		}
		else
		{
			for (int i=0;i<_faceList.length;i++)
			{
				//gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, _faceList[i], 4);
				gl.glDrawArrays(_primType, _faceList[i], 4);
			}
			
		}
		disableClientState(gl);
	}
	
	public void setTextureBuffer(FloatBuffer buffer)
	{
		_texcoordsBuffer = buffer;
	}
	
  	public void setVerticeBuffer(FloatBuffer buffer)
  	{
  		_verticesBuffer = buffer;
  		_vcount = buffer.capacity() / 3;
  	}
  	
  	public void setIndicesBuffer(ShortBuffer buffer)
  	{
  		Log.v(TAG, "warning:");
  	}
  	
  	public FloatBuffer getTextureBuffer()
  	{
  		return _texcoordsBuffer;
  	}
  	
  	public FloatBuffer getVerticeBuffer()
  	{
  		return _verticesBuffer;
  	}
  	
  	public ShortBuffer getIndicesBuffer()
  	{
  		return null;
  	}
	
	private FloatBuffer 	_verticesBuffer;
	private FloatBuffer 	_texcoordsBuffer;
	private int	_vcount;
	private final static String TAG = "DrawArrayImpl";
}

class DrawElementImpl extends RenderImpl
{
	public void render(GL10 gl)
	{
		enableClientState(gl);
		gl.glDrawElements(GL10.GL_TRIANGLES, _icount, 
			GL10.GL_UNSIGNED_SHORT, _indicesBuffer);
		disableClientState(gl);
	}
	
	public void setTextureBuffer(FloatBuffer buffer)
	{
		_texcoordsBuffer = buffer;
	}
	
  	public void setVerticeBuffer(FloatBuffer buffer)
  	{
  		_verticesBuffer = buffer;
  	}
  	
  	public void setIndicesBuffer(ShortBuffer buffer)
  	{
  		_indicesBuffer = buffer;
  		_icount = buffer.capacity();
  	}
  	
  	public FloatBuffer getTextureBuffer()
  	{
  		return _texcoordsBuffer;
  	}
  	
  	public FloatBuffer getVerticeBuffer()
  	{
  		return _verticesBuffer;
  	}
  	
  	public ShortBuffer getIndicesBuffer()
  	{
  		return _indicesBuffer;
  	}
  	
  	FloatBuffer 	_verticesBuffer;
	FloatBuffer 	_texcoordsBuffer;
	ShortBuffer 	_indicesBuffer;
	int	_icount;
}
