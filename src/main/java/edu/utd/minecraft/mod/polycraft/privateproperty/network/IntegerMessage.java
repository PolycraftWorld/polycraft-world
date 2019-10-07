package edu.utd.minecraft.mod.polycraft.privateproperty.network;

import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class IntegerMessage implements IMessage{

	public int secretNumber = 0;
	
	public IntegerMessage()
    {
    }

    public IntegerMessage(int secretNumber)
    {
        this.secretNumber = secretNumber;
    }
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.secretNumber = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(secretNumber);
	}

}
