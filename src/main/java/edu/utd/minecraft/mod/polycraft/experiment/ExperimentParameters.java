package edu.utd.minecraft.mod.polycraft.experiment;

import java.util.HashMap;

import cpw.mods.fml.common.registry.GameData;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

public class ExperimentParameters {

	public HashMap<String, Integer[]> timingParameters;
	public HashMap<String, Number[]> scoringParameters;
	public HashMap<String, Object> itemParameters;
	public HashMap<String, Object> chestParameters;
	
	private static boolean hasDefault = false;
	public static ExperimentParameters DEFAULT_PARAMS;
	
	
	public ExperimentParameters() {
		this.timingParameters = new HashMap<>();
		this.scoringParameters = new HashMap<>();
		this.itemParameters = new HashMap<>();
		this.chestParameters = new HashMap<>();
		if(!hasDefault){
			hasDefault = true;
			DEFAULT_PARAMS = new ExperimentParameters();
			ExperimentParameters.setDefaultParameters();
			hasDefault = true;
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
		 int halfTimeTicks = maxTicks/2; //(5 minutes)
		 int WAITSPAWNTICKS = 400;
		 int WAIT_TELEPORT_UTD_TICKS = 400;
		//TODO: can you use a real clock instead of "skippable" server ticks??
		
		 float claimBaseScoreBonus = 50;
		 float stealBaseScoreBonus = 200;
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
		DEFAULT_PARAMS.timingParameters.put("Sec: Pre-Game", new Integer[] {(WAITSPAWNTICKS)/20, 10, 60}); //seconds
		DEFAULT_PARAMS.timingParameters.put("Sec: Post-Game", new Integer[] {(WAIT_TELEPORT_UTD_TICKS)/20, 10, 60}); //seconds
		DEFAULT_PARAMS.timingParameters.put("Chickens", new Integer[] {0, 0, 60});
		DEFAULT_PARAMS.timingParameters.put("Cows", new Integer[] {0, 0, 60});
		DEFAULT_PARAMS.timingParameters.put("Sheep", new Integer[] {0, 0, 60});
		//DEFAULT_PARAMS.timingParameters.put("Pigs", new Integer[] {0, 0, 60});
		
		//add scoring variables
		DEFAULT_PARAMS.scoringParameters.put("Pts: Claim Base", new Number[] {(claimBaseScoreBonus), (double)0, (double)499}); //points
		DEFAULT_PARAMS.scoringParameters.put("Pts: Steal Base", new Number[] {(stealBaseScoreBonus), 0, 499}); //points
		DEFAULT_PARAMS.scoringParameters.put("Sec: Base Pts Gen", new Number[] {(updateScoreOnTickRate)/20, 0, 10}); //seconds
		DEFAULT_PARAMS.scoringParameters.put("Pts: Owned Base", new Number[] {(ownedBaseScoreBonusOnTicks), 0, 99}); //points
		DEFAULT_PARAMS.scoringParameters.put("Sec: Claim Base", new Number[] {(ticksToClaimBase)/20, 0, 10}); //seconds
		
		//item variables
		DEFAULT_PARAMS.itemParameters.put("Give Knockback Stick", new Boolean(true));
		DEFAULT_PARAMS.itemParameters.put("Inventory", playerInventory); //its easier to send this vs. Item stacks
		DEFAULT_PARAMS.itemParameters.put("Armor", new HashMap<String, Integer>());
		
		//chest variables
		DEFAULT_PARAMS.itemParameters.put("Chest", new HashMap<String, Integer>());
		
		
	}
	
	public void addCurrentParams(Experiment exp) {
		
		if(exp instanceof ExperimentCTB) {
			//add timing variables
			timingParameters.put("Min: Game Time", new Integer[] {(((ExperimentCTB) exp).getMaxTicks())/60/20, 3, 20}); //minutes
			timingParameters.put("Sec: Half Time", new Integer[] {(((ExperimentCTB) exp).getHalfTimeTicks())/20, 30, 180}); //seconds
			timingParameters.put("Sec: Pre-Game", new Integer[] {(((ExperimentCTB) exp).getWAITSPAWNTICKS())/20, 10, 60}); //seconds
			timingParameters.put("Sec: Post-Game", new Integer[] {(((ExperimentCTB) exp).getWAIT_TELEPORT_UTD_TICKS())/20, 10, 60}); //seconds
			
//			//add animal parameters
//			timingParameters.put("Chickens", new Integer[] {0, 0, 60});
//			timingParameters.put("Cows", new Integer[] {0, 0, 60});
//			timingParameters.put("Sheep", new Integer[] {0, 0, 60});
//			timingParameters.put("Pigs", new Integer[] {0, 0, 60});
			
			//add scoring variables
			scoringParameters.put("Pts: Claim Base", new Number[] {((ExperimentCTB) exp).getClaimBaseScoreBonus(), (double)0, (double)499}); //points
			scoringParameters.put("Pts: Steal Base", new Number[] {((ExperimentCTB) exp).getStealBaseScoreBonus(), 0, 499}); //points
			scoringParameters.put("Sec: Base Pts Gen", new Number[] {(((ExperimentCTB) exp).getUpdateScoreOnTickRate())/20, 0, 10}); //seconds
			scoringParameters.put("Pts: Owned Base", new Number[] {((ExperimentCTB) exp).getOwnedBaseScoreBonusOnTicks(), 0, 99}); //points
			scoringParameters.put("Sec: Claim Base", new Number[] {(((ExperimentCTB) exp).getTicksToClaimBase())/20, 0, 10}); //seconds
			
			//item variables
			itemParameters.put("Give Knockback Stick", new Boolean(true));
			itemParameters.put("Inventory", new HashMap<String, Integer>()); //its easier to send this vs. Item stacks
			itemParameters.put("Armor", new HashMap<String, Integer>());
			
			//chest variables
			itemParameters.put("Chest", new HashMap<String, Integer>());
			
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
			scoringParameters.put("Pts: Claim Base", new Number[] {((ExperimentFlatCTB) exp).getClaimBaseScoreBonus(), (double)0, (double)499}); //points
			scoringParameters.put("Pts: Steal Base", new Number[] {((ExperimentFlatCTB) exp).getStealBaseScoreBonus(), 0, 499}); //points
			scoringParameters.put("Sec: Base Pts Gen", new Number[] {(((ExperimentFlatCTB) exp).getUpdateScoreOnTickRate())/20, 0, 10}); //seconds
			scoringParameters.put("Pts: Owned Base", new Number[] {((ExperimentFlatCTB) exp).getOwnedBaseScoreBonusOnTicks(), 0, 99}); //points
			scoringParameters.put("Sec: Claim Base", new Number[] {(((ExperimentFlatCTB) exp).getTicksToClaimBase())/20, 0, 10}); //seconds
			
			//item variables
			itemParameters.put("Give Knockback Stick", new Boolean(true));
			itemParameters.put("Inventory", new HashMap<String, Integer>()); //its easier to send this vs. Item stacks
			itemParameters.put("Armor", new HashMap<String, Integer>());
			
			//chest variables
			itemParameters.put("Chest", new HashMap<String, Integer>());
		}else if (exp instanceof Experiment1PlayerCTB) {
			
			Experiment1PlayerCTB experiment = (Experiment1PlayerCTB) exp;
			
			//add timing variables
			timingParameters.put("Min: Game Time", new Integer[] {(experiment.getMaxTicks())/60/20, 3, 20}); //minutes
			timingParameters.put("Sec: Half Time", new Integer[] {(experiment.getHalfTimeTicks())/20, 30, 180}); //seconds
			timingParameters.put("Sec: Pre-Game", new Integer[] {(experiment.getWAITSPAWNTICKS())/20, 10, 60}); //seconds
			timingParameters.put("Sec: Post-Game", new Integer[] {(experiment.getWAIT_TELEPORT_UTD_TICKS())/20, 10, 60}); //seconds
			
			//add animal parameters
			timingParameters.put("Chickens", new Integer[] {experiment.numChickens, 0, 60});
			timingParameters.put("Cows", new Integer[] {experiment.numCows, 0, 60});
			timingParameters.put("Sheep", new Integer[] {experiment.numSheep, 0, 60});
			//timingParameters.put("Pigs", new Integer[] {0, 0, 60});
			
			//add scoring variables
			scoringParameters.put("Pts: Claim Base", new Number[] {experiment.getClaimBaseScoreBonus(), (double)0, (double)499}); //points
			scoringParameters.put("Pts: Steal Base", new Number[] {experiment.getStealBaseScoreBonus(), 0, 499}); //points
			scoringParameters.put("Sec: Base Pts Gen", new Number[] {(experiment.getUpdateScoreOnTickRate())/20, 0, 10}); //seconds
			scoringParameters.put("Pts: Owned Base", new Number[] {experiment.getOwnedBaseScoreBonusOnTicks(), 0, 99}); //points
			scoringParameters.put("Sec: Claim Base", new Number[] {(experiment.getTicksToClaimBase())/20, 0, 10}); //seconds
			
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
