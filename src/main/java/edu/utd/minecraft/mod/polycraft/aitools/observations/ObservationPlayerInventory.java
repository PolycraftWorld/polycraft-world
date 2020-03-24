package edu.utd.minecraft.mod.polycraft.aitools.observations;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentTutorial;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.item.EntityItem;
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
	public JsonElement getObservation(ExperimentTutorial exp, String args) {
		Gson gson = new Gson();
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		JsonObject jobject = new JsonObject();
		for(int i = 0; i < player.inventory.getSizeInventory(); i ++) {
			//using slot based item definitions instead of item based
			if(player.inventory.getStackInSlot(i) == null)
				continue;
//			int id = Item.getIdFromItem(player.inventory.getStackInSlot(i).getItem());
//			if(items.containsKey(id)) {
//				items.put(id, items.get(id) + player.inventory.getStackInSlot(i).stackSize);
//			}else {
//				items.put(id, player.inventory.getStackInSlot(i).stackSize);
//			}
			JsonObject itemObj = new JsonObject();
			itemObj.addProperty("item", player.inventory.getStackInSlot(i).getItem().getRegistryName());
			itemObj.addProperty("count", player.inventory.getStackInSlot(i).stackSize);
			itemObj.addProperty("damage", player.inventory.getStackInSlot(i).getItemDamage());
			itemObj.addProperty("maxdamage", player.inventory.getStackInSlot(i).getMaxDamage());
			
			if(Block.getBlockFromItem(player.inventory.getStackInSlot(i).getItem()) != null) {
				for(IProperty prop: Block.getBlockFromItem(player.inventory.getStackInSlot(i).getItem()).getDefaultState().getProperties().keySet()) {
					itemObj.addProperty(prop.getName(), Block.getBlockFromItem(player.inventory.getStackInSlot(i).getItem()).getDefaultState().getProperties().get(prop).toString());;
				}
			}
			
			jobject.add(String.valueOf(i), itemObj);
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
		return "inventory";
	}
}
