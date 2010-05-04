package dkgles;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import dkgles.manager.MaterialManager;

import lost.kapa.ContextHolder;
import android.content.res.AssetManager;
import android.util.Log;

public class WavefrontObj extends Mesh
{
	public WavefrontObj(String name, int subMeshCount)
	{
		super(name, subMeshCount);
	}
	
	public void load(String assetPath) throws IOException
	{	
		AssetManager asm = ContextHolder.INSTANCE.get().getAssets();
		InputStream is = asm.open(assetPath);
		InputStreamReader isr = new InputStreamReader(is);
		LineNumberReader reader = new LineNumberReader(isr);
			
		String string = null;
		ArrayList<Vector3> vertList 	= new ArrayList<Vector3>();
		ArrayList<Vector2> texcoordList = new ArrayList<Vector2>();
		
		_vertList 		= new ArrayList<Float>();
		_texcoordList 	= new ArrayList<Float>();
		_indexList 		= new ArrayList<Short>();
		
		try 
		{
			while ((string = reader.readLine())!=null)
			{
				Log.v("WavefrontObj", string);
				
				StringTokenizer st = new StringTokenizer(string, " ");
				
				if (!st.hasMoreElements())
					continue;
				
				String tag = st.nextToken();
				
				if (tag.equals("v"))
				{
					Vector3 vec3 = new Vector3();
					vec3.x = Float.parseFloat(st.nextToken());
					vec3.y = Float.parseFloat(st.nextToken());
					vec3.z = Float.parseFloat(st.nextToken());
					
					vertList.add(vec3);
				}
				else if (tag.equals("vt"))
				{
					Vector2 vec2 = new Vector2();
					vec2.x = Float.parseFloat(st.nextToken());
					vec2.y = Float.parseFloat(st.nextToken());
					texcoordList.add(vec2);
				}
				else if (tag.equals("f"))
				{
					while (st.hasMoreElements())
					{
						parseVertexOfFace(
							st.nextToken(),
							vertList,
							texcoordList);
						//String s = st.nextToken();
						//indexList.add(Short.parseShort(st.nextToken()));
					}
				}
			}// Endof while
			
			SubMesh sm = new SubMesh(
					"foo",
					SubMesh.DRAW_ELEMENT,
					MaterialManager.INSTANCE.getByName("MAT_Green"));
			
			Float[] fary = (Float[])_vertList.toArray(new Float[1]);
			float[] vertexBuffer = new float[fary.length];
			
			for (int i=0;i<fary.length;i++)
			{
				vertexBuffer[i] = fary[i].floatValue();
			}
			
			Float[] tary = (Float[])_texcoordList.toArray(new Float[1]);
			float[] texcoordBuffer = new float[tary.length];
			
			for (int i=0;i<tary.length;i++)
			{
				texcoordBuffer[i] = tary[i].floatVaule();
			}
			
			Short[] sary = (Short[])_indexList.toArray(new Short[1]);
			short[] indexBuffer = new short[sary.length];
			
			for (int i=0;i<sary.length;i++)
			{
				indexBuffer[i] = sary[i].shortValue();
			}
			
			sm.setVertices(vertexBuffer);
			sm.setTexcoords(texcoordBuffer);
			sm.setIndices(indexBuffer);
			
			setSubMesh(0, sm);
			
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	void parseVertexOfFace(String tok, ArrayList<Vector3> vertList, ArrayList<Vector2> texcoordList)
	{
		StringTokenizer st = new StringTokenizer(string, " /");
		
		int vindex = Integer.parseInt(st.nextToken());
		int tindex = Integer.parseInt(st.nextToken());
		
		vindex--;
		tindex--;
		
		Vector3 vert = vertList.get(vindex);
		
		_vertList.add(vert.x);
		_vertList.add(vert.y);
		_vertList.add(vert.z);
		
		Vector2 texc = texcoordList.get(tindex);
		_texcoordList.add(texc.x);
		_texcoordList.add(texc.y);
		
		_indexList.add(vindex);
	}
	
	ArrayList<Float> 	_vertList;
	ArrayList<Float>	_texcoordList;
	ArrayList<Short>	_indexList;
}
