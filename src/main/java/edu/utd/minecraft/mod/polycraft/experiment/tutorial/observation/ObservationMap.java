package edu.utd.minecraft.mod.polycraft.experiment.tutorial.observation;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentTutorial;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureData;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureGuide;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;

public class ObservationMap implements IObservation{

	BlockPos pos1, pos2;

	@Override
	public void init(ExperimentTutorial exp) {
		pos1 = new BlockPos(exp.pos).add(0, 2, 0);
		pos2 = new BlockPos(exp.size);
	}

	@Override
	public JsonElement getObservation(ExperimentTutorial exp, String args) {
		
		for(TutorialFeature feature: exp.getFeatures()) {
			if(feature.getName().equalsIgnoreCase("map") && feature instanceof TutorialFeatureData) {
				pos1 = feature.getPos();
				pos2 = feature.getPos2();
			}
		}
		
		Gson gson = new Gson();
		JsonObject jobject = new JsonObject();
		if(args != null && args.length() > 0 && args.contains("BUID")) {
			for(int i = 0; i <= pos2.getX(); i++) {
				for(int k = 0; k <= pos2.getZ(); k++) {
					JsonObject blockObj = new JsonObject();
					blockObj.addProperty("name", exp.getWorld().getBlockState(pos1.add(i, 0, k)).getBlock().getRegistryName());
					
					for(IProperty prop: exp.getWorld().getBlockState(pos1.add(i, 0, k)).getProperties().keySet()) {
						blockObj.addProperty(prop.getName(), exp.getWorld().getBlockState(pos1.add(i, 0, k)).getProperties().get(prop).toString());;
					}
					jobject.add(pos1.add(i, 0, k).toString(), blockObj);
				}
			}
		}else {
			ArrayList<String> blocks = new ArrayList<String>();
			for(int i = 0; i <= pos2.getX(); i++) {
				for(int k = 0; k <= pos2.getZ(); k++) {
					blocks.add(exp.getWorld().getBlockState(pos1.add(i, 0, k)).getBlock().getRegistryName());
				}
			}
			jobject.add("blocks", gson.toJsonTree(blocks));
			ArrayList<Integer> size = new ArrayList<Integer>();
			size.add(1 + pos2.getX() - pos1.getX());
			size.add(1 + pos2.getY() - pos1.getY());
			size.add(1 + pos2.getZ() - pos1.getZ());
			jobject.add("size", gson.toJsonTree(size));
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
		return "map";
	}
}
