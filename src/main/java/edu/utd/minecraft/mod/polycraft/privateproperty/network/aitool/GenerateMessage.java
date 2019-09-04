package edu.utd.minecraft.mod.polycraft.privateproperty.network.aitool;

import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class GenerateMessage implements IMessage{

	public int width;
	public int length;
	public boolean walls;

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
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
    	this.walls = buf.readBoolean();
        this.width = buf.readInt();
        this.length = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    	buf.writeBoolean(this.walls);
    	buf.writeInt(this.width);
    	buf.writeInt(this.length);
    }

}
