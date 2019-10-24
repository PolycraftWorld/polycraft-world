package edu.utd.minecraft.mod.polycraft.privateproperty.network.aitool;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import edu.utd.minecraft.mod.polycraft.aitools.resource.AIToolResource;
import edu.utd.minecraft.mod.polycraft.aitools.resource.AIToolResourceOre;
import edu.utd.minecraft.mod.polycraft.aitools.resource.AIToolResourceTree;
import edu.utd.minecraft.mod.polycraft.item.ItemAITool.BlockType;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class GenerateMessage implements IMessage{

	public int width;
	public int length;
	public int height;
	public boolean walls;
	public int block;
	public List<Integer> treeTypes= Lists.<Integer>newArrayList();
	public int treeTypeSize;
	public int depth;
	public int caveBlock;
	public List<Integer> treeGenTypes= Lists.<Integer>newArrayList();
	public int treeGenSize;
	public List<List<List<Boolean>>> booleanTableList= new ArrayList<List<List<Boolean>>>();
	public int booleanTableListSize;
	public int chunkWidth,chunkLength;
	public List<Integer> oreTypes= Lists.<Integer>newArrayList();
	public List<Integer> oreGenTypes= Lists.<Integer>newArrayList();
	public int oreTypeSize;
	public int oreGenSize;
	public List<List<List<Boolean>>> oreBooleanTableList= new ArrayList<List<List<Boolean>>>();
	public int oreBooleanTableListSize;
	
	
    public GenerateMessage()
    {
    }

    public GenerateMessage(List<Object> params)
    {

        if (params.size() == 9)
        {
        	this.walls = (boolean)params.get(0);
        	this.width = (int)params.get(1)*16;
        	this.length = (int)params.get(2)*16;
        	this.chunkWidth=(int)params.get(1);
        	this.chunkLength =(int)params.get(2);
        	this.height = (int)params.get(3);
        	switch(((BlockType)params.get(4)).ordinal())
        	{
        		case 0:
        			this.block = Blocks.stone.getIdFromBlock(Blocks.stone);
        			break;
        		case 1:
        			this.block =Blocks.grass.getIdFromBlock(Blocks.grass);
        			break;
        		case 2:
        			this.block =Blocks.sand.getIdFromBlock(Blocks.sand);
        			break;
        		case 3:
        			this.block =Blocks.water.getIdFromBlock(Blocks.water);
        			break;
        		case 4:
        			this.block =Blocks.snow.getIdFromBlock(Blocks.snow);
        			break;
        		default:
        			this.block =Blocks.stone.getIdFromBlock(Blocks.stone);
        			break;
        		
        	}
        	int c=0;
        	for(AIToolResource rec:(List<AIToolResource>)params.get(5))
        	{
        		if(rec instanceof AIToolResourceTree)
        		{
        			this.treeTypes.add(((AIToolResourceTree) rec).treeTypeID);
        			this.treeGenTypes.add(((AIToolResourceTree) rec).genType);
        			this.booleanTableList.add(((AIToolResourceTree) rec).booleanTable);
        		}
        		c++;
        	}
        	this.treeTypeSize=this.treeTypes.size();
        	this.treeGenSize=this.treeGenTypes.size();
        	this.booleanTableListSize=this.booleanTableList.size();
        	//
        	this.depth=(int) params.get(6);
            switch(((BlockType)params.get(7)).ordinal())
        	{
        		case 0:
        			this.caveBlock = Blocks.stone.getIdFromBlock(Blocks.stone);
        			break;
        		case 1:
        			this.caveBlock =Blocks.grass.getIdFromBlock(Blocks.grass);
        			break;
        		case 2:
        			this.caveBlock =Blocks.sand.getIdFromBlock(Blocks.sand);
        			break;
        		case 3:
        			this.caveBlock =Blocks.water.getIdFromBlock(Blocks.water);
        			break;
        		case 4:
        			this.caveBlock =Blocks.snow.getIdFromBlock(Blocks.snow);
        			break;
        		default:
        			this.caveBlock =Blocks.stone.getIdFromBlock(Blocks.stone);
        			break;
        		
        	}
        	c=0;
            for(AIToolResource caveRec:(List<AIToolResource>)params.get(8))
        	{
        		if(caveRec instanceof AIToolResourceOre)
        		{
        			this.oreTypes.add(((AIToolResourceOre) caveRec).oreTypeID);
        			this.oreGenTypes.add(((AIToolResourceOre) caveRec).genType);
        			this.oreBooleanTableList.add(((AIToolResourceOre) caveRec).booleanTable);
        		}
        		c++;
        	}
        	this.oreTypeSize=this.oreTypes.size();
        	this.oreGenSize=this.oreGenTypes.size();
        	this.oreBooleanTableListSize=this.oreBooleanTableList.size();
        }
        
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
    	this.walls = buf.readBoolean();
        this.width = buf.readInt();
        this.length = buf.readInt();
        this.height = buf.readInt();
        this.block = buf.readInt();
        this.treeTypeSize = buf.readInt();
        for(int c=0;c<treeTypeSize;c++)
        {
        	this.treeTypes.add(buf.readInt());
        }
        this.depth=buf.readInt();
        this.caveBlock=buf.readInt();
        this.treeGenSize=buf.readInt();
        for(int c=0;c<treeGenSize;c++)
        {
        	this.treeGenTypes.add(buf.readInt());
        }
        this.booleanTableListSize=buf.readInt();
        this.chunkWidth=buf.readInt();
        this.chunkLength=buf.readInt();
        for(int c=0;c<booleanTableListSize;c++)
        {
        	List<List<Boolean>> boolTable = new ArrayList<List<Boolean>>();
        	for(int x=0;x<this.chunkWidth;x++)
        	{
        		List<Boolean> boolList = new ArrayList<Boolean>();
        		for(int y=0;y<this.chunkLength;y++)
            	{
            		boolList.add(buf.readBoolean());
            	}
        		boolTable.add(boolList);
        	}
        	this.booleanTableList.add(boolTable);
        }
        
        this.oreTypeSize = buf.readInt();
        for(int c=0;c<this.oreTypeSize;c++)
        {
        	this.oreTypes.add(buf.readInt());
        }
        this.oreGenSize=buf.readInt();
        for(int c=0;c<oreGenSize;c++)
        {
        	this.oreGenTypes.add(buf.readInt());
        }
        this.oreBooleanTableListSize=buf.readInt();
        for(int c=0;c<oreBooleanTableListSize;c++)
        {
        	List<List<Boolean>> boolTable = new ArrayList<List<Boolean>>();
        	for(int x=0;x<this.chunkWidth;x++)
        	{
        		List<Boolean> boolList = new ArrayList<Boolean>();
        		for(int y=0;y<this.chunkLength;y++)
            	{
            		boolList.add(buf.readBoolean());
            	}
        		boolTable.add(boolList);
        	}
        	this.oreBooleanTableList.add(boolTable);
        	
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    	buf.writeBoolean(this.walls);
    	buf.writeInt(this.width);
    	buf.writeInt(this.length);
    	buf.writeInt(this.height);
    	buf.writeInt(this.block);
    	buf.writeInt(this.treeTypeSize);
    	for(Integer types:this.treeTypes)
    	{
    		buf.writeInt(types);
    	}
    	buf.writeInt(this.depth);
    	buf.writeInt(this.caveBlock);
    	buf.writeInt(this.treeGenSize);
    	for(Integer types:this.treeGenTypes)
    	{
    		buf.writeInt(types);
    	}
    	buf.writeInt(this.booleanTableListSize);
    	buf.writeInt(this.chunkWidth);
    	buf.writeInt(this.chunkLength);
    	for(List<List<Boolean>> boolTable:this.booleanTableList)
    	{
    		for(int x=0;x<this.chunkWidth;x++)
        	{
        		for(int y=0;y<this.chunkLength;y++)
            	{
        			buf.writeBoolean(boolTable.get(x).get(y));
            	}
        	}
    	}
    	buf.writeInt(this.oreTypeSize);
    	for(Integer types:this.oreTypes)
    	{
    		buf.writeInt(types);
    	}
    	buf.writeInt(this.oreGenSize);
    	for(Integer types:this.oreGenTypes)
    	{
    		buf.writeInt(types);
    	}
    	buf.writeInt(this.oreBooleanTableListSize);
    	for(List<List<Boolean>> boolTable:this.oreBooleanTableList)
    	{
    		for(int x=0;x<this.chunkWidth;x++)
        	{
        		for(int y=0;y<this.chunkLength;y++)
            	{
        			buf.writeBoolean(boolTable.get(x).get(y));
            	}
        	}
    	}
    }

}
