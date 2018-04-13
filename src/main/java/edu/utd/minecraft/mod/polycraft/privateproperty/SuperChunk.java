package edu.utd.minecraft.mod.polycraft.privateproperty;

public class SuperChunk {
	private int x;
	private int z;

	public SuperChunk(int x, int z) {
		this.x=x;
		this.z=z;
	}
	
	public int[][] getChunks()
	{
		int[][] c= new int[2][4];
		c[0][0]=x*2;
		c[1][0]=z*2;
		
		c[0][1]=x*2-1;
		c[1][1]=z*2;
		
		c[0][2]=x*2-1;
	    c[1][2]=z*2-1;
	    
		c[0][3]=x*2;
	    c[1][3]=z*2-1;
	    return c;
				
	}

}
