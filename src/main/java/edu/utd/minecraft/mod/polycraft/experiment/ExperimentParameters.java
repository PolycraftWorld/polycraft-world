package edu.utd.minecraft.mod.polycraft.experiment;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.client.gui.experiment.ExperimentDef.ExperimentType;
import edu.utd.minecraft.mod.polycraft.entity.ai.EntityAICaptureBases;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.Vec3;

public class ExperimentParameters {

	public HashMap<String, Integer[]> timingParameters;
	public HashMap<String, Integer[]> scoringParameters;
	public HashMap<String, Object> itemParameters;
	public HashMap<String, Integer[]> extraParameters;
	
	private static boolean hasDefault = false;
	public static ExperimentParameters DEFAULT_PARAMS;
	
	
	public ExperimentParameters() {
		this.timingParameters = new HashMap<>();
		this.scoringParameters = new HashMap<>();
		this.itemParameters = new HashMap<>();
		this.extraParameters = new HashMap<>();
		if(!hasDefault){
			hasDefault = true;
			DEFAULT_PARAMS = new ExperimentParameters();
			ExperimentParameters.setDefaultParameters();	//TODO: This static default stuff doesn't look like it was setup right...
			hasDefault = true;
		}
	}
	
	public ExperimentParameters(boolean useDefaults) {	//work-around because I don't want to change this whole class
		this();
		if(useDefaults) {
			this.timingParameters.putAll(DEFAULT_PARAMS.timingParameters);
			this.scoringParameters.putAll(DEFAULT_PARAMS.scoringParameters);
			this.itemParameters.putAll(DEFAULT_PARAMS.itemParameters);
			this.extraParameters.putAll(DEFAULT_PARAMS.extraParameters);
		}
	}
	
	public ExperimentParameters(Experiment exp) {
		this();
		this.addCurrentParams(exp);
	}
	
	public int getSize() {
		return timingParameters.size() + scoringParameters.size() + itemParameters.size();
	}
	
	private static void setDefaultParameters() {
		 ItemStack[] armors = {
				new ItemStack(PolycraftRegistry.getItem("Golden Helmet")),
				new ItemStack(PolycraftRegistry.getItem("Kevlar Helmet")),
				new ItemStack(PolycraftRegistry.getItem("Sparkling Headgear")),
				new ItemStack(PolycraftRegistry.getItem("Jeffersonian Wig")),
				new ItemStack(PolycraftRegistry.getItem("Copper Cap")),
				new ItemStack(PolycraftRegistry.getItem("Rubber Shower Cap")),
				new ItemStack(PolycraftRegistry.getItem("Plumed Close Helm")),
				new ItemStack(PolycraftRegistry.getItem("Pepto Bismal Pink Cap")),
				new ItemStack(PolycraftRegistry.getItem("Fine Polyester Top Hat")),
				new ItemStack(PolycraftRegistry.getItem("SuperB Barbute")),
				new ItemStack(PolycraftRegistry.getItem("Spectra Helmet")),
				new ItemStack(PolycraftRegistry.getItem("Wolfram Great Helm")),
				new ItemStack(PolycraftRegistry.getItem("Brazen Bassinet")),
				new ItemStack(PolycraftRegistry.getItem("Comfortable Cap")),
				new ItemStack(PolycraftRegistry.getItem("Ripstop Nylon Beanie")),
				new ItemStack(PolycraftRegistry.getItem("SBR Swim Cap"))
		};
		int currentArmor = 0;
		
		//experimental params
		int maxTicks = 12000; //Server drops ticks?
		//private float MAXSCORE = 1000; 
		 int halfTimeTicks = 2400; //(2 minutes)
		 int WAITSPAWNTICKS = 400;
		 int WAIT_TELEPORT_UTD_TICKS = 400;
		//TODO: can you use a real clock instead of "skippable" server ticks??
		
		 int claimBaseScoreBonus = 50;
		 int stealBaseScoreBonus = 200;
		 int updateScoreOnTickRate = 20;
		 int ownedBaseScoreBonusOnTicks = 5;
		 int ticksToClaimBase = 120; //also the same number of ticks to steal base, for now.
		
//		//give players a stick with knockback == 5.
//		ItemStack item = new ItemStack(GameData.getItemRegistry().getObject("stick"));
//		item.addEnchantment(Enchantment.knockback, 5); //give them a knockback of 5.
		
//		//give players knockback bombs
//		ItemStack kbb = new ItemStack(PolycraftRegistry.getItem("Knockback Bomb"), 4);
//		ItemStack fkb = new ItemStack(PolycraftRegistry.getItem("Freezing Knockback Bomb"), 4);
//		ItemStack carrot = new ItemStack(GameData.getItemRegistry().getObject("carrot"), 20);
		
		HashMap<String, Integer> playerInventory = new HashMap<>();
		playerInventory.put("Knockback Bomb", 4);
		playerInventory.put("Freezing Knockback Bomb", 4);
		playerInventory.put("carrot", 20);
		
		
		//add timing variables
		DEFAULT_PARAMS.timingParameters.put("Min: Game Time", new Integer[] {(maxTicks)/60/20, 3, 20}); //minutes
		DEFAULT_PARAMS.timingParameters.put("Sec: Half Time", new Integer[] {(halfTimeTicks)/20, 30, 180}); //seconds
		DEFAULT_PARAMS.timingParameters.put("Sec: Pre-Game", new Integer[] {(WAITSPAWNTICKS)/10, 10, 60}); //seconds
		DEFAULT_PARAMS.timingParameters.put("Sec: Post-Game", new Integer[] {(WAIT_TELEPORT_UTD_TICKS)/20, 10, 60}); //seconds
		
		DEFAULT_PARAMS.extraParameters.put("Animals", new Integer[] {1,0});
		DEFAULT_PARAMS.extraParameters.put("Chickens", new Integer[] {0, 0, 60});
		DEFAULT_PARAMS.extraParameters.put("Cows", new Integer[] {0, 0, 60});
		DEFAULT_PARAMS.extraParameters.put("Sheep", new Integer[] {0, 0, 60});
		DEFAULT_PARAMS.extraParameters.put("Androids", new Integer[] {0, 0, 60});
		DEFAULT_PARAMS.extraParameters.put("Animal Difficulty", new Integer[] {0,0,2});
		//DEFAULT_PARAMS.timingParameters.put("Pigs", new Integer[] {0, 0, 60});
		
		//add scoring variables
		DEFAULT_PARAMS.scoringParameters.put("Pts: Claim Base", new Integer[] {(claimBaseScoreBonus), 0, 499}); //points
		DEFAULT_PARAMS.scoringParameters.put("Pts: Steal Base", new Integer[] {(stealBaseScoreBonus), 0, 499}); //points
		DEFAULT_PARAMS.scoringParameters.put("Sec: Base Pts Gen", new Integer[] {(updateScoreOnTickRate)/20, 0, 10}); //seconds
		DEFAULT_PARAMS.scoringParameters.put("Pts: Owned Base", new Integer[] {(ownedBaseScoreBonusOnTicks), 0, 99}); //points
		DEFAULT_PARAMS.scoringParameters.put("Sec: Claim Base", new Integer[] {(ticksToClaimBase)/20, 0, 10}); //seconds
		
		//item variables
		DEFAULT_PARAMS.itemParameters.put("Give Knockback Stick", new Boolean(true));
		DEFAULT_PARAMS.itemParameters.put("Inventory", playerInventory); //its easier to send this vs. Item stacks
		DEFAULT_PARAMS.itemParameters.put("Armor", new HashMap<String, Integer>());
		
		//chest variables
		DEFAULT_PARAMS.extraParameters.put("Chest: Update Interval", new Integer[] {30, 5, 120}); //seconds
		DEFAULT_PARAMS.extraParameters.put("Chest: KBB wt", new Integer[] {7, 1, 100}); //chance weight
		DEFAULT_PARAMS.extraParameters.put("Chest: Ice wt", new Integer[] {5, 1, 100}); //chance weight
		DEFAULT_PARAMS.extraParameters.put("Chest: Wood wt", new Integer[] {2, 1, 100}); //chance weight
		DEFAULT_PARAMS.extraParameters.put("Chest: Rubber wt", new Integer[] {1, 1, 100}); //chance weight
		DEFAULT_PARAMS.extraParameters.put("Chest: Aluminum wt", new Integer[] {1, 1, 100}); //chance weight
		
		
	}
	
	public void addCurrentParams(Experiment exp) {
		
		if(exp instanceof ExperimentCTB) {
			//add timing variables
			timingParameters.put("Min: Game Time", new Integer[] {(((ExperimentCTB) exp).getMaxTicks())/60/20, 3, 20}); //minutes
			timingParameters.put("Sec: Half Time", new Integer[] {(((ExperimentCTB) exp).getHalfTimeTicks())/20, 30, 180}); //seconds
			timingParameters.put("Sec: Pre-Game", new Integer[] {(((ExperimentCTB) exp).getWAITSPAWNTICKS())/10, 10, 60}); //seconds
			timingParameters.put("Sec: Post-Game", new Integer[] {(((ExperimentCTB) exp).getWAIT_TELEPORT_UTD_TICKS())/20, 10, 60}); //seconds
			
//			//add animal parameters
//			timingParameters.put("Chickens", new Integer[] {0, 0, 60});
//			timingParameters.put("Cows", new Integer[] {0, 0, 60});
//			timingParameters.put("Sheep", new Integer[] {0, 0, 60});
//			timingParameters.put("Pigs", new Integer[] {0, 0, 60});
			
			//add scoring variables
			scoringParameters.put("Pts: Claim Base", new Integer[] {(int) ((ExperimentCTB) exp).getClaimBaseScoreBonus(), 0, 499}); //points
			scoringParameters.put("Pts: Steal Base", new Integer[] {(int) ((ExperimentCTB) exp).getStealBaseScoreBonus(), 0, 499}); //points
			scoringParameters.put("Sec: Base Pts Gen", new Integer[] {(((ExperimentCTB) exp).getUpdateScoreOnTickRate())/20, 0, 10}); //seconds
			scoringParameters.put("Pts: Owned Base", new Integer[] {((ExperimentCTB) exp).getOwnedBaseScoreBonusOnTicks(), 0, 99}); //points
			scoringParameters.put("Sec: Claim Base", new Integer[] {(((ExperimentCTB) exp).getTicksToClaimBase())/20, 0, 10}); //seconds
			
			//item variables
			itemParameters.put("Give Knockback Stick", new Boolean(true));
			itemParameters.put("Inventory", new HashMap<String, Integer>()); //its easier to send this vs. Item stacks
			itemParameters.put("Armor", new HashMap<String, Integer>());
			
			//chest variables
			extraParameters.put("Chest: Update Interval", new Integer[] {(((ExperimentCTB) exp).getTicksToUpdateChests())/20, 5, 120}); //seconds
			extraParameters.put("Chest: KBB wt", new Integer[] {((ExperimentCTB) exp).getItemKBBChance(), 1, 100}); //chance weight
			extraParameters.put("Chest: Ice wt", new Integer[] {((ExperimentCTB) exp).getItemIceChance(), 1, 100}); //chance weight
			extraParameters.put("Chest: Wood wt", new Integer[] {((ExperimentCTB) exp).getItemWoodChance(), 1, 100}); //chance weight
			extraParameters.put("Chest: Rubber wt", new Integer[] {((ExperimentCTB) exp).getItemNRChance(), 1, 100}); //chance weight
			extraParameters.put("Chest: Aluminum wt", new Integer[] {((ExperimentCTB) exp).getItemAlumChance(), 1, 100}); //chance weight
			
		}else if (exp instanceof ExperimentFlatCTB) {
			//add timing variables
			timingParameters.put("Min: Game Time", new Integer[] {(((ExperimentFlatCTB) exp).getMaxTicks())/60/20, 3, 20}); //minutes
			timingParameters.put("Sec: Half Time", new Integer[] {(((ExperimentFlatCTB) exp).getHalfTimeTicks())/20, 30, 180}); //seconds
			timingParameters.put("Sec: Pre-Game", new Integer[] {(((ExperimentFlatCTB) exp).getWAITSPAWNTICKS())/20, 10, 60}); //seconds
			timingParameters.put("Sec: Post-Game", new Integer[] {(((ExperimentFlatCTB) exp).getWAIT_TELEPORT_UTD_TICKS())/20, 10, 60}); //seconds
			
//			//add animal parameters
//			timingParameters.put("Chickens", new Integer[] {0, 0, 60});
//			timingParameters.put("Cows", new Integer[] {0, 0, 60});
//			timingParameters.put("Sheep", new Integer[] {0, 0, 60});
//			timingParameters.put("Pigs", new Integer[] {0, 0, 60});
//			
			//add scoring variables
			scoringParameters.put("Pts: Claim Base", new Integer[] {(int) ((ExperimentFlatCTB) exp).getClaimBaseScoreBonus(), 0, 499}); //points
			scoringParameters.put("Pts: Steal Base", new Integer[] {(int) ((ExperimentFlatCTB) exp).getStealBaseScoreBonus(), 0, 499}); //points
			scoringParameters.put("Sec: Base Pts Gen", new Integer[] {(((ExperimentFlatCTB) exp).getUpdateScoreOnTickRate())/20, 0, 10}); //seconds
			scoringParameters.put("Pts: Owned Base", new Integer[] {((ExperimentFlatCTB) exp).getOwnedBaseScoreBonusOnTicks(), 0, 99}); //points
			scoringParameters.put("Sec: Claim Base", new Integer[] {(((ExperimentFlatCTB) exp).getTicksToClaimBase())/20, 0, 10}); //seconds
			
			//item variables
			itemParameters.put("Give Knockback Stick", new Boolean(true));
			itemParameters.put("Inventory", new HashMap<String, Integer>()); //its easier to send this vs. Item stacks
			itemParameters.put("Armor", new HashMap<String, Integer>());
			
			//chest variables
			extraParameters.put("Chest: Update Interval", new Integer[] {(((ExperimentFlatCTB) exp).getTicksToUpdateChests())/20, 5, 120}); //seconds
			extraParameters.put("Chest: KBB wt", new Integer[] {((ExperimentFlatCTB) exp).getItemKBBChance(), 1, 100}); //chance weight
			extraParameters.put("Chest: Ice wt", new Integer[] {((ExperimentFlatCTB) exp).getItemIceChance(), 1, 100}); //chance weight
			extraParameters.put("Chest: Wood wt", new Integer[] {((ExperimentFlatCTB) exp).getItemWoodChance(), 1, 100}); //chance weight
			extraParameters.put("Chest: Rubber wt", new Integer[] {((ExperimentFlatCTB) exp).getItemNRChance(), 1, 100}); //chance weight
			extraParameters.put("Chest: Aluminum wt", new Integer[] {((ExperimentFlatCTB) exp).getItemAlumChance(), 1, 100}); //chance weight
		}else if (exp instanceof Experiment1PlayerCTB) {
			
			Experiment1PlayerCTB experiment = (Experiment1PlayerCTB) exp;
			
			
			//add timing variables
			timingParameters.put("Min: Game Time", new Integer[] {(experiment.getMaxTicks())/60/20, 3, 20}); //minutes
			timingParameters.put("Sec: Half Time", new Integer[] {(experiment.getHalfTimeTicks())/20, 30, 180}); //seconds
			timingParameters.put("Sec: Pre-Game", new Integer[] {(experiment.getWAITSPAWNTICKS())/20, 10, 60}); //seconds
			timingParameters.put("Sec: Post-Game", new Integer[] {(experiment.getWAIT_TELEPORT_UTD_TICKS())/20, 10, 60}); //seconds
			
			//add animal parameters
			extraParameters.put("Animals", new Integer[] {1, 0});
			extraParameters.put("Chickens", new Integer[] {experiment.numChickens, 0, 60});
			extraParameters.put("Cows", new Integer[] {experiment.numCows, 0, 60});
			extraParameters.put("Sheep", new Integer[] {experiment.numSheep, 0, 60});
			extraParameters.put("Androids", new Integer[] {experiment.numAndroids, 0, 60});	
			extraParameters.put("Animal Difficulty", new Integer[] {experiment.level, 0, 2});	
			//timingParameters.put("Pigs", new Integer[] {0, 0, 60});
			
			//add scoring variables
			scoringParameters.put("Pts: Claim Base", new Integer[] {(int) experiment.getClaimBaseScoreBonus(), 0, 499}); //points
			scoringParameters.put("Pts: Steal Base", new Integer[] {(int) experiment.getStealBaseScoreBonus(), 0, 499}); //points
			scoringParameters.put("Sec: Base Pts Gen", new Integer[] {(experiment.getUpdateScoreOnTickRate())/20, 0, 10}); //seconds
			scoringParameters.put("Pts: Owned Base", new Integer[] {experiment.getOwnedBaseScoreBonusOnTicks(), 0, 99}); //points
			scoringParameters.put("Sec: Claim Base", new Integer[] {(experiment.getTicksToClaimBase())/20, 0, 10}); //seconds
			
			//item variables
			itemParameters.put("Give Knockback Stick", new Boolean(true));
			itemParameters.put("Inventory", new HashMap<String, Integer>()); //its easier to send this vs. Item stacks
			itemParameters.put("Armor", new HashMap<String, Integer>());
			
			//chest variables
			extraParameters.put("Chest: Update Interval", new Integer[] {(((Experiment1PlayerCTB) exp).getTicksToUpdateChests())/20, 5, 120}); //seconds
			extraParameters.put("Chest: KBB wt", new Integer[] {((Experiment1PlayerCTB) exp).getItemKBBChance(), 1, 100}); //chance weight
			extraParameters.put("Chest: Ice wt", new Integer[] {((Experiment1PlayerCTB) exp).getItemIceChance(), 1, 100}); //chance weight
			extraParameters.put("Chest: Wood wt", new Integer[] {((Experiment1PlayerCTB) exp).getItemWoodChance(), 1, 100}); //chance weight
			extraParameters.put("Chest: Rubber wt", new Integer[] {((Experiment1PlayerCTB) exp).getItemNRChance(), 1, 100}); //chance weight
			extraParameters.put("Chest: Aluminum wt", new Integer[] {((Experiment1PlayerCTB) exp).getItemAlumChance(), 1, 100}); //chance weight
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
				item = new ItemStack(GameData.getItemRegistry().getObject(new ResourceLocation(str)), items.get(str));
				if(!item.hasDisplayName()) {
					item = new ItemStack(GameData.getBlockRegistry().getObject(new ResourceLocation(str)), items.get(str));
				}
			}
			
			count++;
			
		}
		
		
		return finalItems;
	}
	
	public NBTTagCompound save()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagCompound nbtListTiming = new NBTTagCompound();
		NBTTagCompound nbtListScoring = new NBTTagCompound();
		NBTTagCompound nbtListExtra = new NBTTagCompound();
		
		for(String timing: timingParameters.keySet()) {
			int[] temp = new int[timingParameters.get(timing).length];	//there's no other way to convert Integer[] to int[] :/
			for(int x = 0; x < timingParameters.get(timing).length; x++) {
				temp[x] = timingParameters.get(timing)[x];
			}
			nbtListTiming.setIntArray(timing, temp);
		}
		nbt.setTag("timing", nbtListTiming);
		
		for(String scoring: scoringParameters.keySet()) {
			int[] temp = new int[scoringParameters.get(scoring).length];	//there's no other way to convert Integer[] to int[] :/
			for(int x = 0; x < scoringParameters.get(scoring).length; x++) {
				temp[x] = scoringParameters.get(scoring)[x];
			}
			nbtListScoring.setIntArray(scoring, temp);
		}
		nbt.setTag("scoring", nbtListScoring);
		
		for(String extra: extraParameters.keySet()) {
			int[] temp = new int[extraParameters.get(extra).length];	//there's no other way to convert Integer[] to int[] :/
			for(int x = 0; x < extraParameters.get(extra).length; x++) {
				temp[x] = extraParameters.get(extra)[x];
			}
			nbtListExtra.setIntArray(extra, temp);
		}
		nbt.setTag("extra", nbtListExtra);
		
		return nbt;
	}
	
	public void load(NBTTagCompound nbtFeat)
	{
		NBTTagCompound nbtListTiming = nbtFeat.getCompoundTag("timing");
		NBTTagCompound nbtListScoring = nbtFeat.getCompoundTag("scoring");
		NBTTagCompound nbtListExtra = nbtFeat.getCompoundTag("extra");
		
		for(String timing: timingParameters.keySet()) {
			int[] temp = nbtListTiming.getIntArray(timing);	//there's no other way to convert Integer[] to int[] :/
			for(int x = 0; x < timingParameters.get(timing).length; x++) {
				timingParameters.get(timing)[x] = temp[x];
			}
		}
		
		for(String scoring: scoringParameters.keySet()) {
			int[] temp = nbtListScoring.getIntArray(scoring);	//there's no other way to convert Integer[] to int[] :/
			for(int x = 0; x < scoringParameters.get(scoring).length; x++) {
				scoringParameters.get(scoring)[x] = temp[x];
			}
		}
		
		for(String extra: extraParameters.keySet()) {
			int[] temp = nbtListExtra.getIntArray(extra);	//there's no other way to convert Integer[] to int[] :/
			for(int x = 0; x < extraParameters.get(extra).length; x++) {
				extraParameters.get(extra)[x] = temp[x];
			}
		}
	}

}
