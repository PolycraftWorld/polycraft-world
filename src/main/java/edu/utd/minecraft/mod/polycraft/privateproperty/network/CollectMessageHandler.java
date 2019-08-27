package edu.utd.minecraft.mod.polycraft.privateproperty.network;

import edu.utd.minecraft.mod.polycraft.inventory.treetap.TreeTapInventory;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CollectMessageHandler implements IMessageHandler<CollectMessage, IMessage>{

	@Override
    public CollectMessage onMessage(final CollectMessage message, MessageContext ctx)
    {
        final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        IThreadListener mainThread = (WorldServer)ctx.getServerHandler().playerEntity.worldObj;
        mainThread.addScheduledTask(new Runnable()
        {
            @Override
            public void run()
            {
                for(int x = 0; x < 1; x++) {
					player.inventory.addItemStackToInventory(((TreeTapInventory)player.worldObj.getTileEntity(message.containerPos)).removeStackFromSlot(x));
                }
            }
        });
        return null;
    }

}
