package edu.utd.minecraft.mod.polycraft.privateproperty.network;

import java.nio.charset.StandardCharsets;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class BreakBlockMessage implements IMessage{

	public BlockPos blockPos;
	public String args;

    public BreakBlockMessage()
    {
    }

    public BreakBlockMessage(List<Object> params)
    {
        if (params.size() == 1)
            this.blockPos = (BlockPos)params.get(0);
    }
    
    public BreakBlockMessage(BlockPos blockPos, String args) {
    	this.blockPos = blockPos;
    	this.args = args;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
    	args = StandardCharsets.UTF_8.decode(buf.readBytes(buf.readInt()).nioBuffer()).toString();
    	blockPos = new BlockPos(buf.readInt(),buf.readInt(),buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    	buf.writeInt(args.length());
        buf.writeBytes(args.getBytes());
        buf.writeInt(blockPos.getX());
        buf.writeInt(blockPos.getY());
        buf.writeInt(blockPos.getZ());
    }

}
