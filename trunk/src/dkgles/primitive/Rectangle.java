package dkgles.primitive;

import lost.kapa.XmlUtil;

import org.xml.sax.Attributes;

import dkgles.Material;
import dkgles.Mesh;
import dkgles.SubMesh;
import dkgles.manager.MaterialManager;


public class Rectangle extends Mesh
{
	public Rectangle(String name, float width, float height, Material material)
	{
		super(name, 1);
		initialize(name, width, height, material);
	}
	
	public Rectangle(Attributes atts)
	{
		super(1);
		
		String name = XmlUtil.parseString(atts, "name", ""); 
		
		float width  = XmlUtil.parseFloat(atts, "width", 1.0f);
		float height = XmlUtil.parseFloat(atts, "height", 1.0f);
		
		boolean materialSharing = XmlUtil.parseBoolean(atts, "material_sharing", true);
		
		Material material = null;
		
		if (materialSharing)
		{
			material = MaterialManager.INSTANCE.getByName(XmlUtil.parseString(atts, "material", "")); 
		}
		else
		{
			Material protoType = MaterialManager.INSTANCE.getByName(XmlUtil.parseString(atts, "material", ""));
			
			if (protoType!=null)
			{
				material = protoType.clone();
			}
		}
		
		initialize(name, width, height, material);
	}
	
	private void initialize(String name, float width, float height, Material material)
	{
		setName(name);
		
		width_ 	= width;
		height_ = height;
		subMesh_ = new SubMesh("SubMesh_" + name, SubMesh.DRAW_ARRAY, material);
		setSubMesh(0, subMesh_);
		
		initVerticesBuffer();
		initTexcoordsBuffer();	
	}
	
	public void release()
	{
		super.release();
		if (subMesh_!=null)
		{
			subMesh_.release();
			subMesh_ = null;
		}
	}
	
	public float width()
	{
		return width_;
	}
	
	public float height()
	{
		return height_;
	}
	
	public void finalize() throws Throwable
	{
		try
		{
			release();
		}
		finally
		{
			super.finalize();
		}
	}
	
	private void initVerticesBuffer()
	{
		float hx = width_ / 2;
		float hy = height_ / 2;
		
		float[] vertices = new float[]{
				-hx,  hy, 0,
				 hx,  hy, 0,
				-hx, -hy, 0,
				 hx, -hy, 0
		};
		
		subMesh_.setVertices(vertices);	                             
	}
	
	
	private void initTexcoordsBuffer()
	{
		float[] texcoords = new float[]{
				0.0f, 0.0f,
				1.0f, 0.0f,
				0.0f, 1.0f,
				1.0f, 1.0f
		};
		
		subMesh_.setTexcoords(texcoords);
	}
	
	protected SubMesh	subMesh_;
	protected float 	width_;
	protected float 	height_;
}
