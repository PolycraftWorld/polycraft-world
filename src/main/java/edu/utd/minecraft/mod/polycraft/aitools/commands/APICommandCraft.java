package edu.utd.minecraft.mod.polycraft.aitools.commands;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.aitools.APICommandResult;
import edu.utd.minecraft.mod.polycraft.aitools.APICommandResult.Result;
import edu.utd.minecraft.mod.polycraft.aitools.APIHelper.CommandResult;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipeManager;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeComponent;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureRecipeOverride;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialManager;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.privateproperty.network.CommandResultMessage;
import edu.utd.minecraft.mod.polycraft.util.SetMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;

public class APICommandCraft extends APICommandBase{

	private String argsOverride;
	
	public APICommandCraft() {}
	
	public APICommandCraft(float cost, String args) {
		super(cost);
		this.argsOverride = args;
		this.side = Side.SERVER;
	}

	@Override
	public APICommandResult serverExecute(String[] args, EntityPlayerMP player) {
		ContainerWorkbench dummyContainer = new ContainerWorkbench(player.inventory, player.worldObj, player.getPosition());
		InventoryCrafting craftMatrix = new InventoryCrafting(dummyContainer, 3, 3);
		Set<RecipeComponent> items = Sets.newHashSet();
		int multiplier = 1;
    	
		boolean missingItem = false;
		boolean nearCraftingTable = false;
		String missingItems = "";
		String[] clientArgs = args;	//store original input for command result
		
		if(!argsOverride.isEmpty())
			args = argsOverride.split(" ");
		
    	if(args.length == 6) {	//example format "CRAFT 1 minecraft:planks minecraft:planks minecraft:planks minecraft:planks"
    		if(!NumberUtils.isNumber(args[1])) {	//check for number in first argument
    			APICommandResult result = new APICommandResult(args, APICommandResult.Result.FAIL, "Invalid syntax", this.stepCost);
    			return result;
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
    			APICommandResult result = new APICommandResult(args, APICommandResult.Result.FAIL, "Invalid syntax", this.stepCost);
    			return result;
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
    	boolean recipeOverride = false;
    	PolycraftRecipe resultRecipe = null;
    	
    	// check for custom recipelist for this experiment
    	int expID = TutorialManager.INSTANCE.isPlayerinExperiment(player.getName());
    	if(expID != -1) {
    		for(TutorialFeature feature : TutorialManager.INSTANCE.getExperiment(expID).getFeatures()) {
    			if(feature instanceof TutorialFeatureRecipeOverride) {
    				recipeOverride = true;
    				
    				// *** Search for Recipe ***
    				// Check shaped recipe in initial positions
    				// copied code from PolycraftRecipeManager 
    				List<PolycraftRecipe> validRecipes = Lists.newArrayList();
    				final Set<PolycraftRecipe> shapedSet = ((TutorialFeatureRecipeOverride)feature).getShapedRecipes().getAnySubset(items);
    				for (final PolycraftRecipe recipe : shapedSet) {
    					if (recipe.isShapedOnly() && recipe.areInputsValid(items)) {
    						validRecipes.add(recipe);
    					}
    				}
    				if (validRecipes.size() != 0) {
        				Collections.sort(validRecipes, new Comparator<PolycraftRecipe>() {
        					@Override
        					public int compare(PolycraftRecipe o1, PolycraftRecipe o2) {
        						return PolycraftMod.compareInt(o2.getMaxInputStackSize(), o1.getMaxInputStackSize());
        					}
        				});
        				resultRecipe = validRecipes.get(0);
    				}
    				
    				if(resultRecipe != null) {	// couldn't find shaped recipe, check shapeless recipes
    					// copied code from PolycraftRecipeManager 
    					Set<String> itemSet = Sets.newLinkedHashSet();
    					for (final RecipeComponent input : items) {
    						itemSet.add(input.itemStack.getItem().toString());
    					}
    					
    					final Set<PolycraftRecipe> shapelessSet = ((TutorialFeatureRecipeOverride)feature).getShapelessRecipes().getAnySubset(itemSet);
    					for (final PolycraftRecipe recipe : shapelessSet) {
    						if (recipe.areInputsValid(items)) {
    							validRecipes.add(recipe);
    						}
    					}
    					if (validRecipes.size() != 0) {
	    					Collections.sort(validRecipes, new Comparator<PolycraftRecipe>() {
	    						@Override
	    						public int compare(PolycraftRecipe o1, PolycraftRecipe o2) {
	    							return PolycraftMod.compareInt(o2.getMaxInputStackSize(), o1.getMaxInputStackSize());
	    						}
	    					});
	    					resultRecipe = validRecipes.get(0);
    					}
    				}
    			}
    		}
    	}
    	
    	if(!recipeOverride)	// if there was no recipe override, search through default recipes
    		resultRecipe = PolycraftMod.recipeManagerRuntime.findRecipe(PolycraftContainerType.POLYCRAFTING_TABLE, items);
    	
    	if(resultRecipe != null) {
    		recipeWorked = true;
    	}
    	
    	if(missingItem || !recipeWorked || !nearCraftingTable) {	// if we had missing items or the recipe didn't work, add them back to the player's inventory
			//player.addChatComponentMessage(new ChatComponentText("Missing Item:" + missingItems));
			APICommandResult result = new APICommandResult(args, APICommandResult.Result.FAIL, missingItem?"missing items: " + missingItems: !recipeWorked? "Invalid recipe":"Need to be near crafting table", this.stepCost * items.size());
			for(RecipeComponent item: items) {
				player.inventory.addItemStackToInventory(item.itemStack);
			}
			return result;
		}
    	
    	Collection<RecipeComponent> resultItems = resultRecipe.getOutputs(null);
    	int count = 0;
    	String resultingItems = "";
    	for(RecipeComponent item: resultItems) {
    		if(item != null) {
    			player.inventory.addItemStackToInventory(item.itemStack.copy());
    			resultingItems += item.itemStack.getItem().getRegistryName() + (++count < resultItems.size()?",":"");
    		}
    	}
    	
    	if(resultingItems.isEmpty()) 
    		return new APICommandResult(args, APICommandResult.Result.FAIL, "Unhandled error", this.stepCost);
    	else 
    		return new APICommandResult(args, APICommandResult.Result.SUCCESS, "Crafted items: " + resultingItems, this.stepCost * items.size());
	}

	@Override
	public APICommandResult clientExecute(String[] args) {
		// Do nothing on Client Side
		return null;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		argsOverride = StandardCharsets.UTF_8.decode(buf.readBytes(buf.readInt()).nioBuffer()).toString();
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeInt(argsOverride.length());
        buf.writeBytes(argsOverride.getBytes());
	}

}
