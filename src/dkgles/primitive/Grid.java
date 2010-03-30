 package dkgles.primitive;
 
 import dkgles.Mesh;
 
 public class Grid extends Mesh
 {
	 
	 public Grid(String name, float width, float height, int width_segment, int height_segment)
	 {
		 super(name, 1);
		 
		 _width = width;
		 _height = height;
		 _width_segments = width_segment;
		 _height_segments = height_segment;
		 
		 _initVerticesBuffer();
		 _initTexcoordBuffer();
		 _initIndicesBuffer();
	 }
	 
	 private void _initVerticesBuffer()
	 {
		 float[] vertices = new float[(_width_segments+1)*(_height_segments+1)*3];
		 
		 float step_x = _width / _width_segments;
         float step_y = _height / _height_segments;
         float xshift = _width / 2;
         float yshift = _height / 2;
         float x, y;
         int base, index;

         for(int j=0;j<_height_segments+1;j++)
         {
             base = j*(_width_segments+1);
             for(int i=0;i<_width_segments+1;i++)
             {
            	 index = base + i;
                 x = step_x*i - xshift;
                 y = yshift - step_y*j;
                 vertices[index*3+0] = x;
                 vertices[index*3+1] = y;
                 vertices[index*3+2] = 0.0f;
             }
         }
         
         //setVertices(vertices);
	 }
	 
	 
	 private void _initIndicesBuffer()
	 {
		 short[] indices = new short[_width_segments*_height_segments*6];
		 
		 int base, index;
		 int offset = 0;
		 
		 for (int j=0;j<_height_segments;j++)
		 {
			 base = j*(_width_segments+1);
			 
			 for (int i=0;i<_width_segments;i++)
			 {
				 index = base + i;
				 
				 indices[offset + 0] = (short)(index);
				 indices[offset + 1] = (short)(index + _width_segments+1);
				 indices[offset + 2] = (short)(index + _width_segments+2);
				 
				 indices[offset + 3] = (short)(index);
				 indices[offset + 4] = (short)(index + _width_segments+2);
				 indices[offset + 5] = (short)(index + 1);
				 
				 offset += 6;
			 }
		 }
		 
		 //setIndices(indices); 
	 }
	 
	 private void _initTexcoordBuffer()
	 {
		 float[] texcoords = new float[(_width_segments+1)*(_height_segments+1)*2];
		 
		 int base, index;
		 float step_x, step_y;
		 
		 step_x = 1.0f/ _width_segments;
		 step_y = 1.0f/ _height_segments;
		 
		 for (int j=0;j<_height_segments+1;j++)
		 {
			 base = j*(_width_segments+1);
			 
			 for (int i=0;i<_width_segments+1;i++)
			 {
				 index = base + i;
				 texcoords[index*2 + 0] = step_x*i;
				 texcoords[index*2 + 1] = step_y*j;
			 }
		 }
		 
		 //setTexcoords(texcoords);
	 }
	 
	 
	 private float 	_width;
	 private float 	_height;
	 private int	_width_segments;
	 private int	_height_segments;
 }
 
 
