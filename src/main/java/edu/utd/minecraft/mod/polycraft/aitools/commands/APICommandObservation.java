package edu.utd.minecraft.mod.polycraft.aitools.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.aitools.APICommandResult;
import edu.utd.minecraft.mod.polycraft.aitools.APICommandResult.Result;
import edu.utd.minecraft.mod.polycraft.aitools.APIHelper.CommandResult;
import edu.utd.minecraft.mod.polycraft.block.BlockMacGuffin;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class APICommandObservation extends APICommandBase{
	
	private ObsType type;
	
	public enum ObsType {
		ALL,
		INVENTORY,
		SCREEN,
		LOCATIONS,
		RECIPES
	}
	
	public APICommandObservation(float cost, ObsType type) {
		super(cost);
		this.type = type;
	}

	@Override
	public APICommandResult serverExecute(String[] args, EntityPlayerMP player) {
		// Don't do anything for server side
		return null;
	}

	@Override
	public APICommandResult clientExecute(String[] args) {
		// Get Observation on client side
		if(TutorialManager.INSTANCE.clientCurrentExperiment != -1) {
			APICommandResult result = new APICommandResult(args, Result.SUCCESS, "", stepCost);
			switch(type) {
			case ALL:
				result.setJObject(TutorialManager.INSTANCE.getExperiment(TutorialManager.INSTANCE.clientCurrentExperiment).getObservations(args.length > 1 ? args[1] : null));
				break;
			case INVENTORY:
				result.setJObject(TutorialManager.INSTANCE.getExperiment(TutorialManager.INSTANCE.clientCurrentExperiment).getObservation("inventory", args.length > 1 ? args[1] : null));
				break;
			case LOCATIONS:
				result.setJObject(TutorialManager.INSTANCE.getExperiment(TutorialManager.INSTANCE.clientCurrentExperiment).getLocationObservations(args.length > 1 ? args[1] : null));
				break;
			case RECIPES:
				JsonObject jobj = new JsonObject();
				jobj.add("recipes", PolycraftMod.recipeManagerRuntime.getRecipesJsonByContainerType(PolycraftContainerType.POLYCRAFTING_TABLE));
				result.setJObject(jobj);
				break;
			case SCREEN:
				result.setJObject(TutorialManager.INSTANCE.getExperiment(TutorialManager.INSTANCE.clientCurrentExperiment).getVisualObservations(args.length > 1 ? args[1] : null));
				break;
			default:
				return new APICommandResult(args, Result.FAIL, "Unrecognized Observation", stepCost);
			}
			return result;
		}else {
			return new APICommandResult(args, Result.FAIL, "Not currently in an experiment", stepCost);
		}
	}
}
