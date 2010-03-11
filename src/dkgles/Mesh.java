package dkgles;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Mesh extends Drawable
{
	public Mesh(String name)
	{
		super(name);
	}
	
	public void setMaterial(Material material)
	{
		//_material = material;
	}
	
	public void renderImpl(GL10 gl)
	{
		//if (_material!=null)
		//{
		//	_material.apply(gl);
		//}
		
		//gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		//gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		//gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _verticesBuffer);
		//gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, _texcoordsBuffer);
		
		//gl.glDrawElements(GL10.GL_TRIANGLES, _numOfIndices, 
		//	GL10.GL_UNSIGNED_SHORT, _indicesBuffer);
			
		//gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		//gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}
	
	/**
	 * issue gl render command by glDrawArrays
	 */
	public void renderByDrawArray(GL10 gl, int vcount)
	{
		openBufferPtr(gl);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vcount); 
		closeBufferPtr(gl);
	}
	
	/**
	 * issues gl render command by gl 
	 */
	public void renderByDrawElement(GL10 gl, int icount)
	{
		openBufferPtr(gl);
		gl.glDrawElements(GL10.GL_TRIANGLES, icount, 
			GL10.GL_UNSIGNED_SHORT, _indicesBuffer);
		closeBufferPtr(gl);
	}
	
	private void openBufferPtr(GL10 gl)
	{
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _verticesBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, _texcoordsBuffer);
	}
	
	
	private void closeBufferPtr(GL10 gl)
	{
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}
	
	
	protected void setVertices(float[] vertices)
	{
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		_verticesBuffer = vbb.asFloatBuffer();
		_verticesBuffer.put(vertices);
		_verticesBuffer.position(0);
	}
	
	
	protected void setIndices(short[] indices)
	{
		// short is 2 bytes, therefore we multiply the number if
		// vertices with 2.
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		_indicesBuffer = ibb.asShortBuffer();
		_indicesBuffer.put(indices);
		_indicesBuffer.position(0);
  }
	
	
	protected void setTexcoords(float[] texcoords)
	{
		// short is 2 bytes, therefore we multiply the number if
		// vertices with 2.
		ByteBuffer tbb = ByteBuffer.allocateDirect(texcoords.length * 4);
		tbb.order(ByteOrder.nativeOrder());
		_texcoordsBuffer = tbb.asFloatBuffer();
		_texcoordsBuffer.put(texcoords);
		_texcoordsBuffer.position(0);
  }
	
	private FloatBuffer _verticesBuffer;
	private FloatBuffer _texcoordsBuffer;
	private	ShortBuffer _indicesBuffer;
	
	
	

}