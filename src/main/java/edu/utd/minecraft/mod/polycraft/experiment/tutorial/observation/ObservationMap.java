package edu.utd.minecraft.mod.polycraft.experiment.tutorial.observation;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentTutorial;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;

public class ObservationMap implements IObservation{

	BlockPos pos1, pos2;

	@Override
	public void init(ExperimentTutorial exp) {
		pos1 = new BlockPos(exp.pos).add(0, 1, 0);
		pos2 = new BlockPos(exp.size);
	}

	@Override
	public JsonElement getObservation(ExperimentTutorial exp) {
		Gson gson = new Gson();
		ArrayList<Integer> map = new ArrayList<Integer>();
		for(int i = 0; i <= pos2.getX(); i++) {
			for(int k = 0; k <= pos2.getZ(); k++) {
				map.add(Block.getIdFromBlock(exp.getWorld().getBlockState(pos1.add(i, 0, k)).getBlock()));
			}
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
		return "map";
	}
}
