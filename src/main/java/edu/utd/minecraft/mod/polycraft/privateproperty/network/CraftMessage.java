package edu.utd.minecraft.mod.polycraft.privateproperty.network;

import java.nio.charset.StandardCharsets;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class CraftMessage implements IMessage{

	public BlockPos containerPos;
	public String args;

    public CraftMessage()
    {
    }

    public CraftMessage(List<Object> params)
    {
        if (params.size() == 1)
            this.args = (String)params.get(0);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
    	args = StandardCharsets.UTF_8.decode(buf.readBytes(buf.readInt()).nioBuffer()).toString();;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    	buf.writeInt(args.length());
        buf.writeBytes(args.getBytes());
    }

}
