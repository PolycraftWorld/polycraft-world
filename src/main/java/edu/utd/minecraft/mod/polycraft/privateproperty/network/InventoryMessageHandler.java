package edu.utd.minecraft.mod.polycraft.privateproperty.network;

import edu.utd.minecraft.mod.polycraft.inventory.treetap.TreeTapInventory;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
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

public class InventoryMessageHandler implements IMessageHandler<InventoryMessage, IMessage>{

	@Override
    public CollectMessage onMessage(final InventoryMessage message, MessageContext ctx)
    {
        final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        IThreadListener mainThread = (WorldServer)ctx.getServerHandler().playerEntity.worldObj;
        mainThread.addScheduledTask(new Runnable()
        {
            @Override
            public void run()
            {
    			//player.addChatComponentMessage(new ChatComponentText("test"));
            	String args[] =  message.items.split("\\s+");
            	String itemID = args[1];
            	int slotToTransfer = -1;
        		loop: for(int x = 0; x < player.inventory.mainInventory.length; x++) {
        			ItemStack item = player.inventory.mainInventory[x];
        			if(item != null && Item.getByNameOrId(itemID) == item.getItem()) {
        				slotToTransfer = x;
        				break loop;
        			}
        		}
        		int slot = player.inventory.currentItem;
        		if(slotToTransfer == -1) {
        			player.addChatComponentMessage(new ChatComponentText("Item not fount"));
        			return;
        		}else if(slotToTransfer == slot) {
        			player.addChatComponentMessage(new ChatComponentText("Item already selected"));
        			return;
        		}
    			ItemStack stack = player.inventory.getStackInSlot(slot);
    			ItemStack stack2 = player.inventory.getStackInSlot(slotToTransfer);
    			player.inventory.setInventorySlotContents(slot, stack2);
    			player.inventory.setInventorySlotContents(slotToTransfer, stack);
    			player.inventoryContainer.detectAndSendChanges();
        		
            }
        });
        return null;
    }

}
