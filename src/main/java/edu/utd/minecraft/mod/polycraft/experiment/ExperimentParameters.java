package edu.utd.minecraft.mod.polycraft.experiment;

import java.util.HashMap;

import cpw.mods.fml.common.registry.GameData;
import net.minecraft.item.ItemStack;

public class ExperimentParameters {

	public HashMap<String, Integer[]> timingParameters;
	public HashMap<String, Number[]> scoringParameters;
	public HashMap<String, Object> itemParameters;
	public HashMap<String, Object> chestParameters;
	
	public ExperimentParameters() {
		this.timingParameters = new HashMap<>();
		this.scoringParameters = new HashMap<>();
		this.itemParameters = new HashMap<>();
		this.chestParameters = new HashMap<>();
	}
	
	public ExperimentParameters(Experiment exp) {
		this();
		this.addCurrentParams(exp);
	}
	
	public int getSize() {
		return timingParameters.size() + scoringParameters.size() + itemParameters.size();
	}
	
	public void addCurrentParams(Experiment exp) {
		
		if(exp instanceof ExperimentCTB) {
			//add timing variables
			timingParameters.put("Game Time", new Integer[] {(((ExperimentCTB) exp).getMaxTicks())/60/20, 3, 20}); //minutes
			timingParameters.put("Half Time Duration", new Integer[] {(((ExperimentCTB) exp).getHalfTimeTicks())/20, 30, 180}); //seconds
			timingParameters.put("Pre-Game Wait Time", new Integer[] {(((ExperimentCTB) exp).getWAITSPAWNTICKS())/20, 10, 60}); //seconds
			timingParameters.put("After-Game Wait Time", new Integer[] {(((ExperimentCTB) exp).getWAIT_TELEPORT_UTD_TICKS())/20, 10, 60}); //seconds
			
			//add scoring variables
			scoringParameters.put("Claim New Base", new Number[] {((ExperimentCTB) exp).getClaimBaseScoreBonus(), (double)0, (double)1000}); //points
			scoringParameters.put("Steal Base", new Number[] {((ExperimentCTB) exp).getStealBaseScoreBonus(), 0, 1000}); //points
			scoringParameters.put("Base Points Generation Frequency", new Number[] {(((ExperimentCTB) exp).getUpdateScoreOnTickRate())/20, 0, 5}); //seconds
			scoringParameters.put("Owned Base Score Generation", new Number[] {((ExperimentCTB) exp).getOwnedBaseScoreBonusOnTicks(), 0, 1000}); //points
			scoringParameters.put("Time to Claim Base", new Number[] {(((ExperimentCTB) exp).getTicksToClaimBase())/20, 0, 10}); //seconds
			
			//item variables
			itemParameters.put("Give Knockback Stick", new Boolean(true));
			itemParameters.put("Inventory", new HashMap<String, Integer>()); //its easier to send this vs. Item stacks
			itemParameters.put("Armor", new HashMap<String, Integer>());
			
			//chest variables
			itemParameters.put("Chest", new HashMap<String, Integer>());
			
		}
	}
	
	@SuppressWarnings("unused")
	public ItemStack[] getItems(String key) {
		
		ItemStack[] finalItems;
		
		if(key.equals("Armor"))
			finalItems = new ItemStack[4];
		else
			finalItems = new ItemStack[36];
		
		HashMap<String, Integer> items = (HashMap<String, Integer>) itemParameters.get(key);
		if(items == null) {
			return null;
		}
		
		int count = 0;
		for(String str : items.keySet()) {
			//we need to figure out whether a user wants a block or item...
			ItemStack item;
			
			//did the user pass in the item ID?
			try {
				int id = Integer.parseInt(str);
				item = new ItemStack(GameData.getItemRegistry().getObjectById(id), items.get(str));
				
				if(item != null) {
					finalItems[count] = item;
					continue;
				}
				
				item = new ItemStack(GameData.getBlockRegistry().getObjectById(id), items.get(str));
				
								
			} catch(NumberFormatException e) {
				item = new ItemStack(GameData.getItemRegistry().getObject(str), items.get(str));
				if(!item.hasDisplayName()) {
					item = new ItemStack(GameData.getBlockRegistry().getObject(str), items.get(str));
				}
			}
			
			count++;
			
		}
		
		
		return finalItems;
	}

}
