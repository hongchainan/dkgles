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
		ArrayList<Float> vertList = new ArrayList<Float>();
		ArrayList<Short> indexList = new ArrayList<Short>();
		
		try 
		{
			while ((string = reader.readLine())!=null)
			{
				Log.v("WavefrontObj", string);
				
				StringTokenizer st = new StringTokenizer(string);
				
				if (!st.hasMoreElements())
					continue;
				
				String tag = st.nextToken();
				
				if (tag.equals("v"))
				{
					float x = Float.parseFloat(st.nextToken());
					float y = Float.parseFloat(st.nextToken());
					float z = Float.parseFloat(st.nextToken());
					
					vertList.add(x);
					vertList.add(y);
					vertList.add(z);
				}
				else if (tag.equals("f"))
				{
					while (st.hasMoreElements())
					{
						indexList.add(Short.parseShort(st.nextToken()));
					}
				}
			}// Endof while
			
			SubMesh sm = new SubMesh(
					"foo",
					SubMesh.DRAW_ELEMENT,
					MaterialManager.INSTANCE.getByName("MAT_Green"));
			
			Float[] fary = (Float[])vertList.toArray(new Float[1]);
			float[] vertexBuffer = new float[fary.length];
			
			for (int i=0;i<fary.length;i++)
			{
				vertexBuffer[i] = fary[i].floatValue();
			}
			
			Short[] sary = (Short[])indexList.toArray(new Short[1]);
			short[] indexBuffer = new short[sary.length];
			
			for (int i=0;i<sary.length;i++)
			{
				indexBuffer[i] = sary[i].shortValue();
			}
			
			sm.setVertices(vertexBuffer);
			sm.setIndices(indexBuffer);
			
			setSubMesh(0, sm);
			
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}