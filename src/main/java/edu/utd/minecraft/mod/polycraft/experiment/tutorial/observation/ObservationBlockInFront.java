package edu.utd.minecraft.mod.polycraft.experiment.tutorial.observation;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentTutorial;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;

public class ObservationBlockInFront implements IObservation{

	@Override
	public void init(ExperimentTutorial exp) {
	}

	@Override
	public JsonElement getObservation(ExperimentTutorial exp) {
		Gson gson = new Gson();
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		int id = Block.getIdFromBlock(player.worldObj.getBlockState(new BlockPos(player.posX + player.getHorizontalFacing().getFrontOffsetX(),
				player.posY + player.getHorizontalFacing().getFrontOffsetY(),
				player.posZ + player.getHorizontalFacing().getFrontOffsetZ())).getBlock());
		JsonObject jobject = new JsonObject();
		jobject.addProperty("id", id);
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
