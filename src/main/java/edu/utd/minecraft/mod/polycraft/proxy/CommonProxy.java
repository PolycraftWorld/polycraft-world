package edu.utd.minecraft.mod.polycraft.proxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockCompressed;
import edu.utd.minecraft.mod.polycraft.block.BlockFluid;
import edu.utd.minecraft.mod.polycraft.block.BlockOre;
import edu.utd.minecraft.mod.polycraft.block.BlockPlastic;
import edu.utd.minecraft.mod.polycraft.config.CompressedBlock;
import edu.utd.minecraft.mod.polycraft.config.Ingot;
import edu.utd.minecraft.mod.polycraft.config.Ore;
import edu.utd.minecraft.mod.polycraft.config.Plastic;
import edu.utd.minecraft.mod.polycraft.handler.BucketHandler;
import edu.utd.minecraft.mod.polycraft.item.ItemGripped;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilDesert;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilOcean;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeInitializer;
import edu.utd.minecraft.mod.polycraft.worldgen.OilPopulate;
import edu.utd.minecraft.mod.polycraft.worldgen.OreWorldGenerator;

public class CommonProxy {

	public void preInit() {
		createBiomes();
		createFluids();
		createOres();
		createIngots();
		createCompressedBlocks();
		createPlastics();
		createTools();
	}

	public void init() {
		createRecipes();
		GameRegistry.registerWorldGenerator(new OreWorldGenerator(), PolycraftMod.oreWorldGeneratorWeight);
	}

	public void postInit() {
		MinecraftForge.EVENT_BUS.register(OilPopulate.INSTANCE);
		MinecraftForge.TERRAIN_GEN_BUS.register(new BiomeInitializer());
	}

	private void createBiomes() {
		class BiomeIdException extends RuntimeException {
			public BiomeIdException(String biome, int id) {
				super(String.format("You have a Biome Id conflict at %d for %s", id, biome));
			}
		}

		if (PolycraftMod.oilDesertBiomeId > 0) {
			if (BiomeGenBase.getBiomeGenArray()[PolycraftMod.oilDesertBiomeId] != null) {
				throw new BiomeIdException("oilDesert", PolycraftMod.oilDesertBiomeId);
			}
			PolycraftMod.biomeOilDesert = BiomeGenOilDesert.makeBiome(PolycraftMod.oilDesertBiomeId);
		}

		if (PolycraftMod.oilOceanBiomeId > 0) {
			if (BiomeGenBase.getBiomeGenArray()[PolycraftMod.oilOceanBiomeId] != null) {
				throw new BiomeIdException("oilOcean", PolycraftMod.oilOceanBiomeId);
			}
			PolycraftMod.biomeOilOcean = BiomeGenOilOcean.makeBiome(PolycraftMod.oilOceanBiomeId);
		}
	}

	private void createFluids() {
		final Fluid fluidOil = new Fluid("oil").setDensity(PolycraftMod.oilFluidDensity).setViscosity(PolycraftMod.oilFluidViscosity);
		FluidRegistry.registerFluid(fluidOil);

		PolycraftMod.blockOil = PolycraftMod.registerBlock("oil", new BlockFluid(fluidOil, Material.water).setFlammable(true).setFlammability(PolycraftMod.oilBlockFlammability).setParticleColor(0.7F, 0.7F, 0.0F));
		fluidOil.setBlock(PolycraftMod.blockOil);

		PolycraftMod.registerItem("bucket_oil", new ItemBucket(PolycraftMod.blockOil).setContainerItem(Items.bucket).setTextureName(PolycraftMod.getTextureName("bucket_oil")));
		FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluidStack("oil", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(PolycraftMod.items.get("bucket_oil")), new ItemStack(Items.bucket));
		BucketHandler.INSTANCE.buckets.put(PolycraftMod.blockOil, PolycraftMod.items.get("bucket_oil"));
		MinecraftForge.EVENT_BUS.register(BucketHandler.INSTANCE);
	}

	private void createOres() {
		for (final Ore ore : Ore.ores.values())
			PolycraftMod.registerBlock(ore.gameName, new BlockOre(ore));
	}

	private void createIngots() {
		for (final Ingot ingot : Ingot.ingots.values())
			PolycraftMod.registerItem(ingot.gameName, new Item().setCreativeTab(CreativeTabs.tabMaterials).setTextureName(PolycraftMod.getTextureName(ingot.gameName)));
	}

	private void createCompressedBlocks() {
		for (final CompressedBlock compressedBlock : CompressedBlock.compressedBlocks.values())
			PolycraftMod.registerBlock(compressedBlock.gameName, new BlockCompressed(compressedBlock));
	}

	private void createPlastics() {
		for (final Plastic plastic : Plastic.plastics.values()) {
			PolycraftMod.registerBlock(plastic.gameName, new BlockPlastic(plastic));
			PolycraftMod.registerItem(plastic.itemNamePellet, new Item().setCreativeTab(CreativeTabs.tabMaterials).setTextureName(PolycraftMod.getTextureName(plastic.itemNamePellet)));
			PolycraftMod.registerItem(plastic.itemNameGrip, new Item().setCreativeTab(CreativeTabs.tabTools).setTextureName(PolycraftMod.getTextureName("plastic_grip")));
		}
	}

	private void createTools() {
		for (final Entry<String, ToolMaterial> materialEntry : ItemGripped.allowedMaterials.entrySet()) {
			final String materialName = materialEntry.getKey();
			final ToolMaterial material = materialEntry.getValue();
			for (final Plastic plastic : Plastic.plastics.values())
				for (final String type : ItemGripped.allowedTypes.keySet())
					PolycraftMod.registerItem(ItemGripped.getName(plastic, materialName, type), ItemGripped.create(type, materialName, material, plastic.itemDurabilityBonus));
		}
	}

	private void createRecipes() {

		for (final Ore ore : Ore.ores.values())
			if (ore.smeltingIngot != null)
				GameRegistry.addSmelting(PolycraftMod.blocks.get(ore.gameName), new ItemStack(PolycraftMod.items.get(ore.smeltingIngot.gameName), ore.smeltingIngotsPerBlock), ore.smeltingExperience);

		for (final CompressedBlock compressedBlock : CompressedBlock.compressedBlocks.values()) {
			ItemStack blockCompressed = new ItemStack(PolycraftMod.blocks.get(compressedBlock.gameName));
			Item compressedItem = PolycraftMod.items.get(compressedBlock.type.gameName);
			Object[] compressedItems = new ItemStack[compressedBlock.itemsPerBlock];
			for (int i = 0; i < compressedItems.length; i++)
				compressedItems[i] = new ItemStack(compressedItem);
			GameRegistry.addShapelessRecipe(blockCompressed, compressedItems);
			GameRegistry.addShapelessRecipe(new ItemStack(compressedItem, compressedBlock.itemsPerBlock), blockCompressed);
		}

		for (Plastic plastic : Plastic.plastics.values()) {
			Block plasticBlock = PolycraftMod.blocks.get(plastic.gameName);
			Item plasticPellet = PolycraftMod.items.get(plastic.itemNamePellet);
			Item plasticGrip = PolycraftMod.items.get(plastic.itemNameGrip);

			GameRegistry.addShapelessRecipe(new ItemStack(plasticPellet, plastic.craftingPelletsPerBlock), new ItemStack(plasticBlock));
			GameRegistry.addShapelessRecipe(new ItemStack(plasticGrip), new ItemStack(plasticPellet));

			for (String materialName : ItemGripped.allowedMaterials.keySet())
				for (String type : ItemGripped.allowedTypes.keySet())
					GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.items.get(ItemGripped.getName(plastic, materialName, type))), new ItemStack((Item) Item.itemRegistry.getObject(ItemGripped.getNameBase(materialName, type))),
							new ItemStack(plasticGrip));
		}

		if (PolycraftMod.cheatRecipesEnabled) {
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.torch, 64), new ItemStack(Blocks.cobblestone));
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.furnace), new ItemStack(Blocks.cobblestone), new ItemStack(Blocks.cobblestone));
			GameRegistry.addShapelessRecipe(new ItemStack(Items.coal, 64), new ItemStack(Blocks.cobblestone), new ItemStack(Blocks.cobblestone), new ItemStack(Blocks.cobblestone));

			ItemStack dirtStack = new ItemStack(Blocks.dirt);
			Collection<ItemStack> dirtStacks = new ArrayList<ItemStack>();

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.crafting_table), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(Items.diamond_pickaxe), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			ItemStack oilBucketStack = new ItemStack(PolycraftMod.items.get("bucket_oil"));
			GameRegistry.addShapelessRecipe(oilBucketStack, dirtStacks.toArray());

			Collection<ItemStack> oilBuckets = new ArrayList<ItemStack>();
			for (Plastic plastic : Plastic.plastics.values()) {
				oilBuckets.add(oilBucketStack);
				Block plasticBlock = PolycraftMod.blocks.get(plastic.gameName);
				GameRegistry.addShapelessRecipe(new ItemStack(plasticBlock), oilBuckets.toArray());
			}
		}
	}
}
