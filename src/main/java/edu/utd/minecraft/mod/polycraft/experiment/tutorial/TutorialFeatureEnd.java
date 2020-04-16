package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;

import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyLabel;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyNumField;
import edu.utd.minecraft.mod.polycraft.client.gui.exp.creation.GuiExpCreator;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureInstruction.InstructionType;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.util.Format;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class TutorialFeatureEnd extends TutorialFeature{

	public enum EndCondition{
		END_TIME,	// max total runtime
		ITEM,	// item to acquire in inventory
		BLOCK_TO_LOCATION,	// place block at location
		LOCATION,	// Reach location
		END_STEP_COST	// max step cost
	};
	
	public EndCondition endCondition;
	private String itemToAcquire;
	private String blockToPlace;
	public BlockPos locationToReach;
	private float endTime, endStepCost;
	public boolean isFinished, placedLapis;
	
	public TutorialFeatureEnd() {}
	
	public TutorialFeatureEnd(String name, BlockPos pos){
		super(name, pos, Color.YELLOW);
		super.featureType = TutorialFeatureType.END;
		isFinished = false;
		placedLapis = false;
	}
	
	@Override
	public void preInit(ExperimentTutorial exp) {
		super.preInit(exp);
	}
	
	@Override
	public void onServerTickUpdate(ExperimentTutorial exp) {
		
		if(!placedLapis) {
			if(this.endCondition == EndCondition.BLOCK_TO_LOCATION) {
				if(exp.world.getBlockState(locationToReach.add(0,-1,0)).getBlock() != Blocks.lapis_block) {
					exp.world.setBlockState(locationToReach.add(0,-1,0), Blocks.lapis_block.getDefaultState());
				}
			}else { // set placedLapis to true so we don't keep testing this
				placedLapis = true;
			}
		}
		
		for(String playerName : exp.getPlayersInExperiment()) {
			EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(playerName);
			isFinished = onCommandReceived(player);
		}
		
		if(isFinished) {
			this.complete(exp);
		}
	}
	
	public boolean onCommandReceived(EntityPlayer player) {
		
		switch(endCondition) {
		case ITEM:
			if(player.inventory.hasItem(Item.itemRegistry.getObject(new ResourceLocation(itemToAcquire))))
				return true;
			break;
		case BLOCK_TO_LOCATION:
			if(player.worldObj.getBlockState(locationToReach).getBlock() == Block.getBlockFromName(blockToPlace))
				return true;
			break;
		case LOCATION:
			break;
		case END_STEP_COST:
			break;
		case END_TIME:
			break;
		default:
			break;
		}
		
		return false;
	}
	
	@Override
	public void render(Entity entity) {
		return;
		//TutorialRender.instance.renderTutorialDrawStringWithScale("\u00A76YOU WON!",100,35,2F);
	}
	
//	public EndCondition endCondition;
//	private String itemToAcquire;
//	private String blockToPlace;
//	private BlockPos locationToReach;
//	private float endTime, endStepCost;
//	public boolean isFinished;
	
	@Override
	public NBTTagCompound save()
	{
		super.save();
		nbt.setString("endCondition", endCondition.name());
		nbt.setString("itemToAcquire", itemToAcquire);
		nbt.setString("blockToPlace", blockToPlace);
		int pos[] = {locationToReach.getX(), locationToReach.getY(), locationToReach.getZ()};
		nbt.setIntArray("locationToReach",pos);
		nbt.setFloat("endTime", endTime);
		nbt.setFloat("endStepCost", endStepCost);
		nbt.setBoolean("isFinished", isFinished);
		return nbt;
	}
	
	@Override
	public void load(NBTTagCompound nbtFeat)
	{
		super.load(nbtFeat);
		endCondition = EndCondition.valueOf(nbtFeat.getString("endCondition"));
		itemToAcquire = nbtFeat.getString("itemToAcquire");
		blockToPlace = nbtFeat.getString("blockToPlace");
		int featPos[]=nbtFeat.getIntArray("pos");
		locationToReach = new BlockPos(featPos[0], featPos[1], featPos[2]);
		endTime = nbtFeat.getFloat("endTime");
		endStepCost = nbtFeat.getFloat("endStepCost");
		isFinished = nbtFeat.getBoolean("isFinished");
	}
	

	@Override
	public JsonObject saveJson()
	{
		super.saveJson();
		jobj.addProperty("endCondition", endCondition.name());
		jobj.addProperty("itemToAcquire", itemToAcquire);
		jobj.addProperty("blockToPlace", blockToPlace);
		jobj.add("locationToReach", blockPosToJsonArray(locationToReach));
		jobj.addProperty("endTime", endTime);
		jobj.addProperty("endStepCost", endStepCost);
		jobj.addProperty("isFinished", isFinished);
		
		return jobj;
	}
	
	@Override
	public void loadJson(JsonObject featJson)
	{
		super.loadJson(featJson);
		endCondition = EndCondition.valueOf(featJson.get("endCondition").getAsString());
		itemToAcquire = featJson.get("itemToAcquire").getAsString();
		blockToPlace = featJson.get("blockToPlace").getAsString();
		locationToReach = blockPosFromJsonArray(featJson.get("locationToReach").getAsJsonArray());
		endTime = featJson.get("endTime").getAsFloat();
		endStepCost = featJson.get("endStepCost").getAsFloat();
		isFinished = featJson.get("isFinished").getAsBoolean();
		
	}
}
