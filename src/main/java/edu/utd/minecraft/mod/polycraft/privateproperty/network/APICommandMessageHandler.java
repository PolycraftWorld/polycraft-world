package edu.utd.minecraft.mod.polycraft.privateproperty.network;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class APICommandMessageHandler implements IMessageHandler<APICommandMessage, IMessage>{

	@Override
    public APICommandMessage onMessage(final APICommandMessage message, final MessageContext ctx)
    {
        final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        IThreadListener mainThread = (WorldServer)ctx.getServerHandler().playerEntity.worldObj;
        mainThread.addScheduledTask(new Runnable()
        {
            @Override
            public void run()
            {	
            	//Execute Server side and send back command result
            	PolycraftMod.logger.info("Running Command on SERVER side: " + message.command.getClass().getName());
            	PolycraftMod.SChannel.sendTo(new CommandResultMessage(message.command.serverExecute(message.args.split(" "), player)), player);
            }
        });
        return null;
    }

}
