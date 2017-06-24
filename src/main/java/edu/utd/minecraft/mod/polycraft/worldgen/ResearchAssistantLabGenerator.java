package edu.utd.minecraft.mod.polycraft.worldgen;

import java.util.Random;

import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameData;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.entity.npc.EntityResearchAssistant;
import edu.utd.minecraft.mod.polycraft.inventory.fueledlamp.FueledLampBlock;
import edu.utd.minecraft.mod.polycraft.inventory.fueledlamp.GaslampInventory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenerator;

public class ResearchAssistantLabGenerator extends WorldGenerator implements IWorldGenerator {
	// JO = White PP Block
	// K2 = White PVC Block
	// YK = PVC Stairs

	public static final float CHUNK_PROB = 0.05F;
	public static final Item DIESEL = GameData.getItemRegistry().getObject(PolycraftMod.getAssetName("tR"));
	public static final int[][] HOLE_WALLS = { { 12, 13 }, { 11, 14 }, { 13, 14 }, { 12, 15 } };
	public static final Item KERO = GameData.getItemRegistry().getObject(PolycraftMod.getAssetName("tQ"));
	public static final BlockContainer LIGHT = (BlockContainer) GameData.getBlockRegistry()
			.getObject(PolycraftMod.getAssetName("1bW"));
	public static final int[] NUM_SPAWNS = { 1, 1, 2, 3, 3, 3, 4, 4, 5, 6, 7 };
	public static final Block PP = GameData.getBlockRegistry().getObject(PolycraftMod.getAssetName("JO"));
	public static final Block PVC = GameData.getBlockRegistry().getObject(PolycraftMod.getAssetName("K2"));
	public static final Block STAIRS = GameData.getBlockRegistry().getObject(PolycraftMod.getAssetName("YK"));

	public ResearchAssistantLabGenerator() {
		super();
	}

	public void spawnResearchAssistant(World world, int x, int y, int z) {
		// EntityResearchAssistant testra = new EntityResearchAssistant(world,
		// true);
		// testra.setLocationAndAngles(x + 2, y + 8, z + 2, 0, 0);
		// world.spawnEntityInWorld(testra);
		EntityResearchAssistant ra = new EntityResearchAssistant(world, true);
		ra.setLocationAndAngles(x, y, z, 0, 0);
		world.spawnEntityInWorld(ra);
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
			IChunkProvider chunkProvider) {
		if (world.provider.dimensionId != 0 || random.nextFloat() > CHUNK_PROB)
			return;
		int x = chunkX * 16;
		int y = 6 + random.nextInt(80);
		int z = chunkZ * 16;

		// Checks

		// Floor
		for (int i = 0; i < 16; i++)
			for (int k = 0; k < 16; k++)
				world.setBlock(x + i, y, z + k, PVC, 15, 2);

		// 8x8x8 (10x10x10) Segment
		for (int j = 0; j < 10; j++) {
			for (int i = 0; i < 10; i++) {
				world.setBlock(x + i, y + j, z + 6, PP, 15, 2);
				world.setBlock(x + i, y + j, z + 15, PP, 15, 2);
			}
			for (int k = 15; k > 5; k--) {
				world.setBlock(x, y + j, z + k, PP, 15, 2);
				world.setBlock(x + 9, y + j, z + k, PP, 15, 2);
			}
		}
		// Clear 8x8x8 room and make roof.
		for (int i = 1; i < 9; i++) {
			for (int k = 7; k < 15; k++) {
				for (int j = 1; j < 9; j++)
					world.setBlockToAir(x + i, y + j, z + k);
				world.setBlock(x + i, y + 9, z + k, PP, 15, 2);
			}
		}

		// Other walls.
		for (int j = 1; j < 5; j++) {
			for (int i = 1; i < 15; i++)
				world.setBlock(x + i, y + j, z, PVC, 15, 2);
			for (int i = 10; i < 16; i++)
				world.setBlock(x + i, y + j, z + 15, PVC, 15, 2);
			for (int k = 0; k < 6; k++) {
				world.setBlock(x, y + j, z + k, PVC, 15, 2);
				world.setBlock(x + 15, y + j, z + k, PVC, 15, 2);
			}
			for (int k = 6; k < 16; k++)
				world.setBlock(x + 15, y + j, z + k, PVC, 15, 2);
			// Clear the remaining area.
			for (int i = 1; i < 15; i++)
				for (int k = 1; k < 6; k++)
					world.setBlockToAir(x + i, y + j, z + k);
			for (int i = 10; i < 15; i++)
				for (int k = 6; k < 15; k++)
					world.setBlockToAir(x + i, y + j, z + k);
		}

		// First 2 rooms.
		for (int k = 14; k > 7; k--) {
			world.setBlock(x + 10, y + 1, z + k, PVC, 15, 2);
			world.setBlock(x + 14, y + 1, z + k, PVC, 15, 2);
			world.setBlock(x + 10, y + 3, z + k, STAIRS, 1, 2);
			world.setBlock(x + 10, y + 4, z + k, STAIRS, 1, 2);
			world.setBlock(x + 14, y + 3, z + k, STAIRS, 0, 2);
			world.setBlock(x + 14, y + 4, z + k, STAIRS, 0, 2);
		}

		// 2 Doors.
		for (int k = 7; k < 12; k += 4) {
			world.setBlock(x + 11, y + 1, z + k, PVC, 15, 2);
			world.setBlock(x + 13, y + 1, z + k, PVC, 15, 2);
			for (int i = 10; i < 15; i++)
				for (int j = 1; j < 5; j++)
					world.setBlock(x + i, y + j, z + k, PVC, 15, 2);
			ItemDoor.placeDoorBlock(world, x + 12, y + 1, z + k, 1, Blocks.iron_door);
			world.setBlock(x + 13, y + 2, z + k + 1, Blocks.lever, 3, 2);
			// Extra cauldrons here...
			world.setBlock(x + 10, y + 1, z + k + 2, Blocks.cauldron, random.nextInt(4), 2);
			world.setBlock(x + 14, y + 1, z + k + 2, Blocks.cauldron, random.nextInt(4), 2);
			spawnResearchAssistant(world, x + 12, y + 1, z + k + 2);
		}

		// 3rd Room and 4th Room
		for (int k = 1; k < 6; k++) {
			for (int j = 1; j < 5; j++)
				world.setBlock(x + 9, y + j, z + k, PVC, 15, 2);
			world.setBlock(x + 1, y + 1, z + k, PVC, 15, 2);
			world.setBlock(x + 1, y + 3, z + k, STAIRS, 1, 2);
			world.setBlock(x + 1, y + 4, z + k, STAIRS, 1, 2);
		}
		for (int i = 10; i < 15; i++) {
			world.setBlock(x + i, y + 1, z + 1, PVC, 15, 2);
			world.setBlock(x + i, y + 3, z + 1, STAIRS, 3, 2);
			world.setBlock(x + i, y + 4, z + 1, STAIRS, 3, 2);
		}
		world.setBlock(x + 12, y + 1, z + 1, Blocks.cauldron, random.nextInt(4), 2);
		ItemDoor.placeDoorBlock(world, x + 9, y + 1, z + 3, 0, Blocks.iron_door);
		world.setBlock(x + 10, y + 2, z + 2, Blocks.lever, 1, 2);
		world.setBlock(x + 1, y + 1, z + 3, Blocks.cauldron, random.nextInt(4), 2);
		ItemDoor.placeDoorBlock(world, x + 5, y + 1, z + 6, 3, Blocks.iron_door);
		world.setBlock(x + 4, y + 2, z + 5, Blocks.lever, 4, 2);

		// PolycraftInventoryBlocks only override placement by entity method.
		EntityResearchAssistant helper = new EntityResearchAssistant(world, true);
		helper.setPositionAndRotation(x + 1, y + 1, z + 8, 0, 0);
		world.setBlock(x + 8, y + 1, z + 7, LIGHT, 0, 2);
		FueledLampBlock light = (FueledLampBlock) world.getBlock(x + 8, y + 1, z + 7);
		light.onBlockPlacedBy(world, x + 8, y + 1, z + 7, helper, new ItemStack(LIGHT));
		GaslampInventory lightInv = (GaslampInventory) light.getInventory(world, x + 8, y + 1, z + 7);
		lightInv.setInventorySlotContents(0,
				new ItemStack(random.nextFloat() > 0.5 ? KERO : DIESEL, 1 + random.nextInt(3)));
		lightInv.updateEntity();

		/*
		 * 0: 1 - Crafting table 1: 1 - Tree with taps 2: 2 - Machining mill 3:
		 * 3 - Injection molder 4: 3 - Extruder 5: 3 - Distillation Column 6: 4
		 * - Industrial Oven 7: 4 - Steam Cracker 8: 5 - Merox 9: 6 - Chemical
		 * Processor or condensers 10: 7 - Oil derrick, pumps, flow regulators,
		 */

		// Roofing
		for (int i = 0; i < 16; i++)
			for (int k = 0; k < 6; k++)
				world.setBlock(x + i, y + 5, z + k, PVC, 15, 2);
		for (int i = 10; i < 16; i++)
			for (int k = 6; k < 16; k++)
				world.setBlock(x + i, y + 5, z + k, PVC, 15, 2);

		// Entrance hole
		int top = world.getTopSolidOrLiquidBlock(x + 12, z + 14);
		for (; world.getBlock(x + 12, top, z + 14) != PVC; top--)
			world.setBlockToAir(x + 12, top, z + 14);
		world.setBlockToAir(x + 12, top, z + 14);
		for (int[] wall : HOLE_WALLS) {
			top = world.getTopSolidOrLiquidBlock(x + wall[0], z + wall[1]);
			for (; world.getBlock(x + wall[0], top, z + wall[1]) != PVC; top--)
				world.setBlock(x + wall[0], top, z + wall[1], PVC, 15, 2);
		}

		// Loot Room
		int loot = random.nextInt(11);
		for (int i = 0; i < NUM_SPAWNS[loot] - 1; i++)
			spawnResearchAssistant(world, x + 2 + i, y + 1, z + 8);
		switch (loot) {
		case 0:
			break;
		default:
			break;
		}
		world.spawnEntityInWorld(helper);

	}

	@Override
	public boolean generate(World p_76484_1_, Random p_76484_2_, int p_76484_3_, int p_76484_4_, int p_76484_5_) {
		return false;
	}

}
