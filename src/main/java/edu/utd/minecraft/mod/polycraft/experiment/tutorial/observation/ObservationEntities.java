package edu.utd.minecraft.mod.polycraft.experiment.tutorial.observation;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentTutorial;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureData;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureGuide;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;

public class ObservationEntities implements IObservation{

	BlockPos pos1, pos2;

	@Override
	public void init(ExperimentTutorial exp) {
		pos1 = new BlockPos(exp.pos).add(0, 2, 0);
		pos2 = new BlockPos(exp.pos2);
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

		List<Entity> list = Minecraft.getMinecraft().thePlayer.worldObj.getEntitiesWithinAABBExcludingEntity(Minecraft.getMinecraft().thePlayer, new AxisAlignedBB(pos1.add(0, -1, 0), pos2.add(0, 1, 0)));
		System.out.println("List size:" + list.size());
		for(Entity entity: list) {
			JsonObject entityObj = new JsonObject();
			entityObj.addProperty("type", entity.getClass().getSimpleName());
			entityObj.addProperty("name", entity.getName());
			entityObj.addProperty("id", entity.getEntityId());
			entityObj.addProperty("Pos", entity.getPosition().toString());
			if(entity instanceof EntityItem) {
				entityObj.addProperty("item", ((EntityItem)entity).getEntityItem().getItem().getRegistryName());
				entityObj.addProperty("count", ((EntityItem)entity).getEntityItem().stackSize);
				entityObj.addProperty("damage", ((EntityItem)entity).getEntityItem().getItemDamage());
				entityObj.addProperty("maxdamage", ((EntityItem)entity).getEntityItem().getMaxDamage());
			}
			jobject.add(String.valueOf(entity.getEntityId()), entityObj);
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
		return "entities";
	}
}
