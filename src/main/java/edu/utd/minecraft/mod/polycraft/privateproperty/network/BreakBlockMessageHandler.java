package edu.utd.minecraft.mod.polycraft.privateproperty.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.collect.Sets;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.aitools.APICommandResult;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipeManager;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeComponent;
import edu.utd.minecraft.mod.polycraft.inventory.treetap.TreeTapInventory;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class BreakBlockMessageHandler implements IMessageHandler<BreakBlockMessage, IMessage>{

	@Override
    public CollectMessage onMessage(final BreakBlockMessage message, final MessageContext ctx)
    {
        final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        IThreadListener mainThread = (WorldServer)ctx.getServerHandler().playerEntity.worldObj;
        mainThread.addScheduledTask(new Runnable()
        {
            @Override
            public void run()
            {
            	EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            	if(player.worldObj.isAirBlock(message.blockPos)) {
            		APICommandResult result = new APICommandResult(message.args.split(" "), APICommandResult.Result.FAIL, "Cannot break air block");
        			PolycraftMod.SChannel.sendTo(new CommandResultMessage(result), player);
        			return;
            	}
            	player.theItemInWorldManager.tryHarvestBlock(message.blockPos);
            	
            	if(!player.worldObj.isAirBlock(message.blockPos)) {
            		APICommandResult result = new APICommandResult(message.args.split(" "), APICommandResult.Result.FAIL, "Failed to break block");
        			PolycraftMod.SChannel.sendTo(new CommandResultMessage(result), player);
        			return;
            	}else {
            		APICommandResult result = new APICommandResult(message.args.split(" "), APICommandResult.Result.SUCCESS, "");
        			PolycraftMod.SChannel.sendTo(new CommandResultMessage(result), player);
        			return;
            	}
            }
        });
        return null;
    }

}
