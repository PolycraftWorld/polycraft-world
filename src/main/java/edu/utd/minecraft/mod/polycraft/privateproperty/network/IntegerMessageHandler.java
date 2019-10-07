package edu.utd.minecraft.mod.polycraft.privateproperty.network;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class IntegerMessageHandler implements IMessageHandler<IntegerMessage, IMessage>{

	@Override
	public IMessage onMessage(IntegerMessage message, MessageContext ctx) {
		final EntityPlayer player = PolycraftMod.proxy.getPlayer();
        IThreadListener mainThread = Minecraft.getMinecraft();
        mainThread.addScheduledTask(new Runnable()
        {
            @Override
            public void run()
            {
            	player.addChatMessage(new ChatComponentText("Integer value: " + message.secretNumber));
        		System.out.println("TEST");
            }
        });
        return null;
	}
}
