package edu.utd.minecraft.mod.polycraft.privateproperty.network;

import java.nio.charset.StandardCharsets;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class TeleportMessage implements IMessage{

	public BlockPos targetPos;
	public float yaw, pitch;
	public String args;

    public TeleportMessage()
    {
    }

    public TeleportMessage(List<Object> params)
    {
        if (params.size() == 2) {
            this.args = (String)params.get(0);
            this.targetPos = (BlockPos)params.get(1);
        }
    }
    
    public TeleportMessage(BlockPos targetPos, String args, float yaw, float pitch)
    {
        this.args = args;
        this.targetPos = targetPos;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
    	args = StandardCharsets.UTF_8.decode(buf.readBytes(buf.readInt()).nioBuffer()).toString();
    	targetPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    	yaw = buf.readFloat();
    	pitch = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    	buf.writeInt(args.length());
        buf.writeBytes(args.getBytes());
        buf.writeInt(targetPos.getX());
        buf.writeInt(targetPos.getY());
        buf.writeInt(targetPos.getZ());
        buf.writeFloat(yaw);
        buf.writeFloat(pitch);
    }

}
