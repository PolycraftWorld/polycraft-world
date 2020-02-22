package edu.utd.minecraft.mod.polycraft.experiment.tutorial.observation;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import edu.utd.minecraft.mod.polycraft.block.BlockMacGuffin;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentTutorial;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureData;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureGuide;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;

public class ObservationMacGuffinPos implements IObservation{

	BlockPos posOffset, posDest;

	@Override
	public void init(ExperimentTutorial exp) {
		posOffset = new BlockPos(exp.pos);
		posDest = new BlockPos(exp.pos);
	}

	@Override
	public JsonElement getObservation(ExperimentTutorial exp, String args) {
		
search: for(int x = (int)exp.pos.xCoord; x < (int)exp.pos2.xCoord; x++) {
			for(int y = (int)exp.pos.yCoord; y < (int)exp.pos2.yCoord; y++) {
				for(int z = (int)exp.pos.zCoord; z < (int)exp.pos2.zCoord; z++) {
					if(exp.getWorld().getBlockState(new BlockPos(x, y, z)).getBlock() instanceof BlockMacGuffin) {
						posDest = new BlockPos(x,y,z);
						break search;
					}
				}
			}
		}
		
		Gson gson = new Gson();
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		ArrayList<Integer> map = new ArrayList<Integer>();
		if(posDest == null) {
			map.add(0);
			map.add(0);
			map.add(0);
		}else {
			map.add((int) (posDest.getX() - posOffset.getX()));
			map.add((int) (posDest.getY() - posOffset.getY()));
			map.add((int) (posDest.getZ() - posOffset.getZ()));
		}
		return gson.toJsonTree(map);
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
		return "MacGuffinPos";
	}
}
