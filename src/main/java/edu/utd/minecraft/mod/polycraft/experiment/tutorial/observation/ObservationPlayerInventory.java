package edu.utd.minecraft.mod.polycraft.experiment.tutorial.observation;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentTutorial;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;

public class ObservationPlayerInventory implements IObservation{

	BlockPos posOffset;

	@Override
	public void init(ExperimentTutorial exp) {
		posOffset = new BlockPos(exp.pos);
	}

	@Override
	public JsonElement getObservation(ExperimentTutorial exp) {
		Gson gson = new Gson();
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		HashMap<Integer, Integer> items = new HashMap<Integer, Integer>();
		for(int i = 0; i < player.inventory.getSizeInventory(); i ++) {
			if(player.inventory.getStackInSlot(i) == null)
				continue;
			int id = Item.getIdFromItem(player.inventory.getStackInSlot(i).getItem());
			if(items.containsKey(id)) {
				items.put(id, items.get(id) + player.inventory.getStackInSlot(i).stackSize);
			}else {
				items.put(id, player.inventory.getStackInSlot(i).stackSize);
			}
		}
		return gson.toJsonTree(items);
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
		return "inventory";
	}
}
