package edu.utd.minecraft.mod.polycraft.privateproperty.network.aitool;

import java.util.List;

import com.google.common.collect.Lists;

import edu.utd.minecraft.mod.polycraft.aitools.AIToolResource;
import edu.utd.minecraft.mod.polycraft.aitools.AIToolResourceTree;
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

    public GenerateMessage()
    {
    }

    public GenerateMessage(List<Object> params)
    {

        if (params.size() == 8)
        {
        	this.walls = (boolean)params.get(0);
        	this.width = (int)params.get(1);
        	this.length = (int)params.get(2);
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
        		}
        		c++;
        	}
        	this.treeTypeSize=treeTypes.size();
        	//
        }
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
    }

}
