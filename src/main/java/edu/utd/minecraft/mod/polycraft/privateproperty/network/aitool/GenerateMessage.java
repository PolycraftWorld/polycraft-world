package edu.utd.minecraft.mod.polycraft.privateproperty.network.aitool;

import java.util.List;

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

    public GenerateMessage()
    {
    }

    public GenerateMessage(List<Object> params)
    {
        if (params.size() == 1)
            this.width = (int)params.get(0);
        if (params.size() == 2)
        {
        	this.width = (int)params.get(0);
        	this.length = (int)params.get(1);
        }
        if (params.size() == 3)
        {
        	this.walls = (boolean)params.get(0);
        	this.width = (int)params.get(1);
        	this.length = (int)params.get(2);
        }
        if (params.size() == 4)
        {
        	this.walls = (boolean)params.get(0);
        	this.width = (int)params.get(1);
        	this.length = (int)params.get(2);
        	this.height = (int)params.get(3);
        }
        if (params.size() == 5)
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
        	//
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
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    	buf.writeBoolean(this.walls);
    	buf.writeInt(this.width);
    	buf.writeInt(this.length);
    	buf.writeInt(this.height);
    	buf.writeInt(this.block);
    }

}
