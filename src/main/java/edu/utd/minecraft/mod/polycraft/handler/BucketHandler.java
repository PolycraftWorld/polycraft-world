package edu.utd.minecraft.mod.polycraft.handler;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BucketHandler {

	public static BucketHandler INSTANCE = new BucketHandler();
	public Map<Block, Item> buckets = new LinkedHashMap<Block, Item>();

	private BucketHandler() {
	}

	@SubscribeEvent
	public void onBucketFill(FillBucketEvent event) {
		ItemStack result = fillCustomBucket(event.world, event.target);

		if (result == null) {
			return;
		}

		event.result = result;
		event.setResult(Result.ALLOW);
	}

	private ItemStack fillCustomBucket(World world, MovingObjectPosition pos) {
		Block block = world.getBlockState(pos.getBlockPos()).getBlock();

		Item bucket = buckets.get(block);

		if (bucket != null && block.getMetaFromState(world.getBlockState(pos.getBlockPos())) == 0) {
			world.setBlockToAir(pos.getBlockPos());
			return new ItemStack(bucket);
		} else {
			return null;
		}
	}
}
