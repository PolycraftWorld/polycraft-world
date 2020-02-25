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

public class CraftMessageHandler implements IMessageHandler<CraftMessage, IMessage>{

	@Override
    public CollectMessage onMessage(final CraftMessage message, final MessageContext ctx)
    {
        final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        IThreadListener mainThread = (WorldServer)ctx.getServerHandler().playerEntity.worldObj;
        mainThread.addScheduledTask(new Runnable()
        {
            @Override
            public void run()
            {
            	EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            	ContainerWorkbench dummyContainer = new ContainerWorkbench(player.inventory, player.worldObj, player.getPosition());
        		InventoryCrafting craftMatrix = new InventoryCrafting(dummyContainer, 3, 3);
        		Set<RecipeComponent> items = Sets.newHashSet();
        		int multiplier = 1;
            	
            	String args[] =  message.args.split("\\s+");
        		boolean missingItem = false;
    			boolean nearCraftingTable = false;
        		String missingItems = "";
        		
            	if(args.length == 6) {	//example format "CRAFT 1 minecraft:planks minecraft:planks minecraft:planks minecraft:planks"
            		if(!NumberUtils.isNumber(args[1])) {	//check for number in first argument
            			APICommandResult result = new APICommandResult(args, APICommandResult.Result.FAIL, "Invalid syntax");
            			PolycraftMod.SChannel.sendTo(new CommandResultMessage(result), player);
            			return;
            		}else {
            			nearCraftingTable = true; //don't need crafting table for 2x2 crafting
            			multiplier = Integer.parseInt(args[1]);
            			craftLoop: for(int x = 2; x < 6; x++) {
            				if(args[x].equals("0"))
            					continue craftLoop;
            				for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
            					if(player.inventory.getStackInSlot(i) != null 
            							&& player.inventory.getStackInSlot(i).getItem().getRegistryName().equals(args[x])
            							&& player.inventory.getStackInSlot(i).stackSize >= multiplier) {
            						items.add(new RecipeComponent(x==2? 0: x==3? 1: x==4? 3: 4, player.inventory.decrStackSize(i, 1)));
            						continue craftLoop;
            					}
            				}
            				missingItem = true;
            				missingItems += args[x] + "::";
            			}
            		}
            	}else if(args.length == 11) {	//example format "CRAFT 1 minecraft:planks 0 0 minecraft:planks 0 0 0 0 0"
            		if(!NumberUtils.isNumber(args[1])) {	//check for number in first argument
            			APICommandResult result = new APICommandResult(args, APICommandResult.Result.FAIL, "Invalid syntax");
            			PolycraftMod.SChannel.sendTo(new CommandResultMessage(result), player);
            			return;
            		}else {
            			//Check if we are near a crafting table (within 3 blocks)
			searchloop: for(int x=-3;x<=3;x++) {
            				for(int y=-2;y<=2;y++) {
            					for(int z=-3;z<=3;z++) {
            						if(player.worldObj.getBlockState(player.getPosition().add(x,y,z)).getBlock() == Blocks.crafting_table) {
            							nearCraftingTable = true;
            							break searchloop;
            						}
            					}
            				}
            			}
            			
            			multiplier = Integer.parseInt(args[1]);
            			craftLoop: for(int x = 2; x < 11; x++) {
            				if(args[x].equals("0"))
            					continue craftLoop;
            				for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
            					if(player.inventory.getStackInSlot(i) != null 
            							&& player.inventory.getStackInSlot(i).getItem().getRegistryName().equals(args[x])
            							&& player.inventory.getStackInSlot(i).stackSize >= multiplier) {
            						items.add(new RecipeComponent(x-2, player.inventory.decrStackSize(i, multiplier)));
            						continue craftLoop;
            					}
            				}
            				missingItem = true;
            				missingItems += args[x] + "::";
            			}
            		}
            	}
            	
            	boolean recipeWorked = false;
            	
            	PolycraftRecipe resultRecipe = PolycraftMod.recipeManagerRuntime.findRecipe(PolycraftContainerType.POLYCRAFTING_TABLE, items);
            	
            	if(resultRecipe != null) {
            		recipeWorked = true;
            	}
            	
            	if(missingItem || !recipeWorked || !nearCraftingTable) {	// if we had missing items or the recipe didn't work, add them back to the player's inventory
        			//player.addChatComponentMessage(new ChatComponentText("Missing Item:" + missingItems));
        			APICommandResult result = new APICommandResult(args, APICommandResult.Result.FAIL, missingItem?"missing items: " + missingItems: !recipeWorked? "Invalid recipe":"Need to be near crafting table");
        			PolycraftMod.SChannel.sendTo(new CommandResultMessage(result), player);
        			for(RecipeComponent item: items) {
        				player.inventory.addItemStackToInventory(item.itemStack);
        			}
        			return;
        		}
            	
            	Collection<RecipeComponent> resultItems = resultRecipe.getOutputs(null);
            	int count = 0;
            	String resultingItems = "";
            	for(RecipeComponent item: resultItems) {
            		if(item != null) {
            			player.inventory.addItemStackToInventory(item.itemStack.copy());
            			resultingItems += item.itemStack.getItem().getRegistryName() + (++count < resultItems.size()?",":"");
            		}
            		APICommandResult result = new APICommandResult(args, APICommandResult.Result.SUCCESS, "Crafted items: " + resultingItems);
        			PolycraftMod.SChannel.sendTo(new CommandResultMessage(result), player);
        			return;
            	}
            	
            	
//            	if(args.length > 9) {
//        			craftLoop: for(int x = 1; x < 9; x++) {
//        				if(Integer.parseInt(args[x]) == 0)
//        					continue craftLoop;
//        				for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
//        					if(player.inventory.getStackInSlot(i) != null && Item.getIdFromItem(player.inventory.getStackInSlot(i).getItem()) == Integer.parseInt(args[x])) {
//        						craftMatrix.setInventorySlotContents(x-1, player.inventory.decrStackSize(i, 1));
//        						continue craftLoop;
//        					}
//        				}
//        				missingItem = true;
//        				missingItems += Integer.parseInt(args[x]) + "::";
//        			}
//        		}
//        		
//        		if(missingItem) {
//        			//player.addChatComponentMessage(new ChatComponentText("Missing Item:" + missingItems));
//        			APICommandResult result = new APICommandResult(args, APICommandResult.Result.FAIL, "missing items: " + missingItems);
//        			PolycraftMod.SChannel.sendTo(new CommandResultMessage(result), player);
//        			for(int slot = 0; slot < craftMatrix.getSizeInventory(); slot++) {
//        				player.inventory.addItemStackToInventory(craftMatrix.getStackInSlot(slot));
//        			}
//        			return;
//        		}
        		
//        		ItemStack resultItem = CraftingManager.getInstance().findMatchingRecipe(craftMatrix, player.worldObj);
//        		if(resultItem != null) {
//        			player.inventory.addItemStackToInventory(resultItem);
//        			craftMatrix.clear();
//        			player.addChatComponentMessage(new ChatComponentText("Crafted: " + resultItem.getDisplayName()));
//        		}else {
//        			String result = "";
//        			for(int x = 0; x < craftMatrix.getSizeInventory(); x ++) {
//        				if(craftMatrix.getStackInSlot(x) != null)
//        					result += craftMatrix.getStackInSlot(x).getDisplayName() + "::";
//        				else
//        					result += "null::";
//        			}
//        			player.addChatComponentMessage(new ChatComponentText("No Item found for recipe:" + result));
//        		}
            }
        });
        return null;
    }

}
