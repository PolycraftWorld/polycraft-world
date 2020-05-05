package edu.utd.minecraft.mod.polycraft.aitools.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.aitools.APICommandResult;
import edu.utd.minecraft.mod.polycraft.aitools.APICommandResult.Result;
import edu.utd.minecraft.mod.polycraft.aitools.APIHelper.CommandResult;
import edu.utd.minecraft.mod.polycraft.block.BlockMacGuffin;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialManager;
import edu.utd.minecraft.mod.polycraft.privateproperty.network.InventoryMessage;
import edu.utd.minecraft.mod.polycraft.privateproperty.network.TeleportMessage;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class APICommandReportNovelty extends APICommandBase{

	public APICommandReportNovelty(float cost) {
		super(cost);
	}

	@Override
	public APICommandResult serverExecute(String[] args, EntityPlayerMP player) {
		// Don't do anything for server side
		return null;
	}

	@Override
	public APICommandResult clientExecute(String[] args) {
		// Process on client side
		// Report_Novelty -c [confidence (0-100)] -m [message] -l [numeric level] -g [game novelty appeared]
		
//		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
//    	for(int i = 0; i < args.length; i++) {
//    		switch(args[i]) {
//    		case "-c":
//    			break;
//    		case "-m":
//    			break;
//    		case "-l":
//    			break;
//    		case "-g":
//    			break;
//			default:
//				break;
//    		}
//    	}
		
		APICommandResult result = new APICommandResult(args, APICommandResult.Result.SUCCESS, "Novelty Reported", this.stepCost);
		return result;
	}
}
