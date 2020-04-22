package edu.utd.minecraft.mod.polycraft.aitools.observations;

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

public class ObservationPlayerPos implements IObservation{

	BlockPos posOffset;

	@Override
	public void init(ExperimentTutorial exp) {
		posOffset = new BlockPos(exp.pos);
	}

	@Override
	public JsonElement getObservation(ExperimentTutorial exp, String args) {
		Gson gson = new Gson();
		JsonObject jobject = new JsonObject();
		
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		ArrayList<Integer> playerPos = new ArrayList<Integer>();
		// Player offset
//		playerPos.add((int) (player.posX - posOffset.getX()));
//		playerPos.add((int) (player.posY - posOffset.getY()));
//		playerPos.add((int) (player.posZ - posOffset.getZ()));
		
		//removed offsets
		playerPos.add((int) (player.posX));
		playerPos.add((int) (player.posY));
		playerPos.add((int) (player.posZ));
		
		jobject.add("pos", gson.toJsonTree(playerPos));
		
		jobject.addProperty("facing", player.getHorizontalFacing().name());
		jobject.addProperty("yaw", player.rotationYaw);
		jobject.addProperty("pitch", player.rotationPitch);
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
		return "player";
	}
}
