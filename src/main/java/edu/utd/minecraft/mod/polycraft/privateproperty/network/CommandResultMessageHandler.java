package edu.utd.minecraft.mod.polycraft.privateproperty.network;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.aitools.BotAPI;
import edu.utd.minecraft.mod.polycraft.inventory.treetap.TreeTapInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CommandResultMessageHandler implements IMessageHandler<CommandResultMessage, IMessage>{

	@Override
    public CollectMessage onMessage(final CommandResultMessage message, final MessageContext ctx)
    {
		final EntityPlayer player = PolycraftMod.proxy.getPlayer();
        IThreadListener mainThread = Minecraft.getMinecraft();
        mainThread.addScheduledTask(new Runnable()
        {
            @Override
            public void run()
            {
            	BotAPI.setServerResult(message.result);
            }
        });
        return null;
    }

}
