package edu.utd.minecraft.mod.polycraft.aitools.observations;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentTutorial;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;

public class ObservationBlockInFront implements IObservation{

	@Override
	public void init(ExperimentTutorial exp) {
	}

	@Override
	public JsonElement getObservation(ExperimentTutorial exp, String args) {
		Gson gson = new Gson();
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		BlockPos blockPos = new BlockPos(player.posX + player.getHorizontalFacing().getFrontOffsetX(),
				player.posY + player.getHorizontalFacing().getFrontOffsetY(),
				player.posZ + player.getHorizontalFacing().getFrontOffsetZ());
		
		String blockName = player.worldObj.getBlockState(blockPos).getBlock().getRegistryName();
		JsonObject jobject = new JsonObject();
		jobject.addProperty("name", blockName);	//add block name to json
		
		// add meta data to json
		for(IProperty prop: exp.getWorld().getBlockState(blockPos).getProperties().keySet()) {
			jobject.addProperty(prop.getName(), exp.getWorld().getBlockState(blockPos).getProperties().get(prop).toString());;
		}
		
		return jobject;
	}

	@Override
	public NBTTagCompound save() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void load(NBTTagCompound nbtObs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return "blockInFront";
	}
}
