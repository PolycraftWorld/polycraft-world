package edu.utd.minecraft.mod.polycraft.privateproperty.network;

import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class CollectMessage implements IMessage{

	public BlockPos containerPos;

    public CollectMessage()
    {
    }

    public CollectMessage(List<Object> params)
    {
        if (params.size() == 1)
            this.containerPos = (BlockPos)params.get(0);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        if (buf.readBoolean())
            this.containerPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(this.containerPos != null);
        if (this.containerPos != null)
        {
            buf.writeInt(this.containerPos.getX());
            buf.writeInt(this.containerPos.getY());
            buf.writeInt(this.containerPos.getZ());
        }
    }

}
