package edu.utd.minecraft.mod.polycraft.privateproperty.network;

import java.nio.charset.StandardCharsets;
import java.util.List;

import edu.utd.minecraft.mod.polycraft.aitools.commands.APICommandBase;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class APICommandMessage implements IMessage{

	public APICommandBase command;
	public String args;

    public APICommandMessage() {}

    public APICommandMessage(APICommandBase command, String args)
    {
        this.command = command;
        this.args = args;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        try {
        	args = StandardCharsets.UTF_8.decode(buf.readBytes(buf.readInt()).nioBuffer()).toString();
        	String commandClass = StandardCharsets.UTF_8.decode(buf.readBytes(buf.readInt()).nioBuffer()).toString();
			command = (APICommandBase)Class.forName(commandClass).newInstance();
	    	command.fromBytes(buf);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			// What should we do here? There was an issue with 
			e.printStackTrace();
		}
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    	buf.writeInt(args.length());
        buf.writeBytes(args.getBytes());
        String commandClass = command.getClass().getCanonicalName();
        buf.writeInt(commandClass.length());
        buf.writeBytes(commandClass.getBytes());
        command.toBytes(buf);
    }

}
