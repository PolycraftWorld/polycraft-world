package edu.utd.minecraft.mod.polycraft.privateproperty.network;

import java.nio.charset.StandardCharsets;
import java.util.List;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialOptions;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ExperimentMessage implements IMessage{

	public TutorialOptions tutOptions;
	public enum Type{
		JOIN,
		LEAVE
	}
	
	public Type type;

    public ExperimentMessage()
    {
    }

    public ExperimentMessage(Type type, TutorialOptions tutOptions)
    {
    	this.type = type;
        this.tutOptions = tutOptions;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
    	tutOptions = new TutorialOptions();
    	tutOptions.fromBytes(buf);
    	type = Type.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    	tutOptions.toBytes(buf);
    	buf.writeInt(type.ordinal());
    }

}
