package edu.utd.minecraft.mod.polycraft.experiment.tutorial.observation;

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
		
		String id = player.worldObj.getBlockState(blockPos).getBlock().getRegistryName();
		JsonObject jobject = new JsonObject();
		jobject.addProperty("name", id);
		
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
		return "BlockInFront";
	}
}
