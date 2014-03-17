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
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockCompound;
import edu.utd.minecraft.mod.polycraft.block.BlockCompressed;
import edu.utd.minecraft.mod.polycraft.block.BlockFluid;
import edu.utd.minecraft.mod.polycraft.block.BlockOre;
import edu.utd.minecraft.mod.polycraft.block.BlockPlastic;
import edu.utd.minecraft.mod.polycraft.config.Alloy;
import edu.utd.minecraft.mod.polycraft.config.Catalyst;
import edu.utd.minecraft.mod.polycraft.config.Compound;
import edu.utd.minecraft.mod.polycraft.config.CompressedBlock;
import edu.utd.minecraft.mod.polycraft.config.Entity;
import edu.utd.minecraft.mod.polycraft.config.Ingot;
import edu.utd.minecraft.mod.polycraft.config.Ore;
import edu.utd.minecraft.mod.polycraft.config.Plastic;
import edu.utd.minecraft.mod.polycraft.handler.BucketHandler;
import edu.utd.minecraft.mod.polycraft.handler.GuiHandler;
import edu.utd.minecraft.mod.polycraft.inventory.fracker.BlockFracker;
import edu.utd.minecraft.mod.polycraft.inventory.fracker.FrackerRecipes;
import edu.utd.minecraft.mod.polycraft.inventory.fracker.RenderFracker;
import edu.utd.minecraft.mod.polycraft.inventory.fracker.TileEntityFracker;
import edu.utd.minecraft.mod.polycraft.item.ItemGripped;
import edu.utd.minecraft.mod.polycraft.item.ItemWorn;
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
		createCatalysts();
		createPlastics();
		createCompounds();
		createTools();
		createInventories();
		createClothes(); // TODO fix the Clothes Class
	}

	public void init() {
		createRecipes();
		createCheatRecipes();
		GameRegistry.registerWorldGenerator(new OreWorldGenerator(), PolycraftMod.oreWorldGeneratorWeight);
		RenderingRegistry.registerBlockHandler(PolycraftMod.renderFrackerID, RenderFracker.INSTANCE);
		NetworkRegistry.INSTANCE.registerGuiHandler(PolycraftMod.instance, new GuiHandler());
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

		PolycraftMod.registerItem("fluid_container", new Item().setCreativeTab(CreativeTabs.tabMaterials).setTextureName(PolycraftMod.getTextureName("fluid_container")));
		PolycraftMod.registerItem("fluid_container_nozzle", new Item().setCreativeTab(CreativeTabs.tabMaterials).setTextureName(PolycraftMod.getTextureName("fluid_container_nozzle")));
	}

	private void createOres() {
		for (final Ore ore : Ore.registry.values())
			PolycraftMod.registerBlock(ore.gameName, new BlockOre(ore));
	}

	private void createIngots() {
		for (final Ingot ingot : Ingot.registry.values())
			PolycraftMod.registerItem(ingot.gameName, new Item().setCreativeTab(CreativeTabs.tabMaterials).setTextureName(PolycraftMod.getTextureName(ingot.gameName)));
	}

	private void createCompressedBlocks() {
		for (final CompressedBlock compressedBlock : CompressedBlock.registry.values())
			PolycraftMod.registerBlock(compressedBlock.gameName, new BlockCompressed(compressedBlock));
	}

	private void createCatalysts() {
		for (final Catalyst catalyst : Catalyst.registry.values())
			PolycraftMod.registerItem(catalyst.gameName, new Item().setCreativeTab(CreativeTabs.tabBrewing).setTextureName(PolycraftMod.getTextureName(catalyst.gameName)));
	}

	private void createPlastics() {
		for (final Plastic plastic : Plastic.registry.values()) {
			PolycraftMod.registerBlock(plastic.gameName, new BlockPlastic(plastic));
			PolycraftMod.registerItem(plastic.itemNamePellet, new Item().setCreativeTab(CreativeTabs.tabMaterials).setTextureName(PolycraftMod.getTextureName(plastic.itemNamePellet.replaceAll("_[0-9]", ""))));
			PolycraftMod.registerItem(plastic.itemNameFiber, new Item().setCreativeTab(CreativeTabs.tabMaterials).setTextureName(PolycraftMod.getTextureName(plastic.itemNameFiber.replaceAll("_[0-9]", ""))));

			// this makes only one grip object for each type of plastic color
			if (plastic.isDefaultColor()) {
				PolycraftMod.registerItem(plastic.itemNameGrip, new Item().setCreativeTab(CreativeTabs.tabTools).setTextureName(PolycraftMod.getTextureName("plastic_grip")));
			}
		}
	}

	private void createCompounds() {
		for (final Compound compound : Compound.registry.values())
			if (compound.fluid)
				PolycraftMod.registerItem(compound.gameName + "_fluid_container", new Item().setCreativeTab(CreativeTabs.tabMaterials).setTextureName(PolycraftMod.getTextureName("fluid_container")));
			else
				PolycraftMod.registerBlock(compound.gameName, new BlockCompound(compound));
	}

	private void createTools() {
		for (final Entry<String, ToolMaterial> materialEntry : ItemGripped.allowedMaterials.entrySet()) {
			final String materialName = materialEntry.getKey();
			final ToolMaterial material = materialEntry.getValue();
			for (final Plastic plastic : Plastic.registry.values())
				for (final String type : ItemGripped.allowedTypes.keySet()) {
					if (plastic.isDefaultColor())
						PolycraftMod.registerItem(ItemGripped.getName(plastic, materialName, type), ItemGripped.create(type, materialName, material, plastic.itemDurabilityBonus));
				}
		}
	}

	private void createClothes() {
		for (final Entry<String, ArmorMaterial> materialEntry : ItemWorn.allowedMaterials.entrySet()) {
			final String materialName = materialEntry.getKey();
			final ArmorMaterial material = materialEntry.getValue();
			for (final Plastic plastic : Plastic.registry.values())
				for (final String type : ItemWorn.allowedTypes.keySet()) {

					for (int bodyLocation = 0; bodyLocation < 3; bodyLocation++) {
						PolycraftMod.registerItem(ItemWorn.getName(plastic, materialName, type, bodyLocation), ItemWorn.create(type, materialName, material, plastic.itemDurabilityBonus, bodyLocation));
					}

				}
		}
	}

	private void createInventories() {
		PolycraftMod.blockFracker = PolycraftMod.registerBlock("fracker", new BlockFracker(false));
		PolycraftMod.blockFrackerActive = PolycraftMod.registerBlock("fracker_active", new BlockFracker(true));
		GameRegistry.registerTileEntity(TileEntityFracker.class, "tile_entity_fracker");
	}

	private void createRecipes() {

		for (final Ore ore : Ore.registry.values())
			if (ore.smeltingEntity != null)
				GameRegistry.addSmelting(PolycraftMod.blocks.get(ore.gameName),
						ore.smeltingEntityIsItem ? new ItemStack(PolycraftMod.items.get(ore.smeltingEntity.gameName), ore.smeltingEntitiesPerBlock) : new ItemStack(PolycraftMod.blocks.get(ore.smeltingEntity.gameName),
								ore.smeltingEntitiesPerBlock), ore.smeltingExperience);

		for (final CompressedBlock compressedBlock : CompressedBlock.registry.values()) {
			final ItemStack blockCompressed = new ItemStack(PolycraftMod.blocks.get(compressedBlock.gameName));
			final Item compressedItem = PolycraftMod.items.get(compressedBlock.type.gameName);
			final Object[] compressedItems = new ItemStack[compressedBlock.itemsPerBlock];
			for (int i = 0; i < compressedItems.length; i++)
				compressedItems[i] = new ItemStack(compressedItem);
			GameRegistry.addShapelessRecipe(blockCompressed, compressedItems);
			GameRegistry.addShapelessRecipe(new ItemStack(compressedItem, compressedBlock.itemsPerBlock), blockCompressed);
		}

		for (final Alloy alloy : Alloy.registry.values()) {
			final Object[] inputs = new Object[alloy.craftingInputs.size()];
			int i = 0;
			for (Entry<Object, Integer> craftingInputEntry : alloy.craftingInputs.entrySet()) {
				final Object craftingInput = craftingInputEntry.getKey();
				inputs[i++] = new ItemStack((craftingInput instanceof Entity) ? PolycraftMod.items.get(((Entity) craftingInput).gameName) : (Item) craftingInput, craftingInputEntry.getValue());
			}
			// TODO it appears the forge has a bug here where the item stacks are only decremented by 1 each (not the required amount)
			GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.items.get("ingot_" + alloy.gameName), alloy.craftingAmount), inputs);
		}

		final Item fluidContainerNozzle = PolycraftMod.items.get("fluid_container_nozzle");
		GameRegistry.addRecipe(new ItemStack(fluidContainerNozzle), "xxx", " x ", " x ", 'x', new ItemStack(PolycraftMod.items.get("ingot_element_copper")));

		final Catalyst platinumCatalyst = Catalyst.registry.get("catalyst_element_platinum");
		GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.items.get(platinumCatalyst.gameName), platinumCatalyst.craftingAmount), new ItemStack(PolycraftMod.items.get("ingot_element_platinum")));

		// anything you put in here will be able to be crafted with all plastics of all colors
		for (final Plastic plastic : Plastic.registry.values()) {

			// there is plastic block corresponding to each type and each color of plastic
			final Block plasticBlock = PolycraftMod.blocks.get(plastic.gameName);

			// there are plastic pellets and fibers corresponding to each type and each color of plastic
			final Item plasticPellet = PolycraftMod.items.get(plastic.itemNamePellet);
			final Item plasticFiber = PolycraftMod.items.get(plastic.itemNameFiber);

			// there are plastic grips corresponding to each type and each color of plastic
			final Item plasticGrip = PolycraftMod.items.get(plastic.itemNameGrip);

			// three diagonal pellets give you a fiber
			GameRegistry.addRecipe(new ItemStack(plasticFiber), "x  ", " x ", "  x", 'x', new ItemStack(plasticPellet));

			// a fiber can be back converted into pellets
			GameRegistry.addShapelessRecipe(new ItemStack(plasticPellet), new ItemStack(plasticFiber));

			GameRegistry.addShapelessRecipe(new ItemStack(plasticPellet, plastic.craftingPelletsPerBlock), new ItemStack(plasticBlock));
			GameRegistry.addRecipe(new ItemStack(plasticGrip), "x x", "x x", "xxx", 'x', new ItemStack(plasticPellet));

			// this only builds new tools for each type of plastic, not each color...
			if (plastic.isDefaultColor())
				for (final String materialName : ItemGripped.allowedMaterials.keySet())
					for (final String type : ItemGripped.allowedTypes.keySet())
						GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.items.get(ItemGripped.getName(plastic, materialName, type))),
								new ItemStack((Item) Item.itemRegistry.getObject(ItemGripped.getNameBase(materialName, type))), new ItemStack(plasticGrip));
		}

		final Block fracker = PolycraftMod.blocks.get("fracker");
		GameRegistry.addRecipe(new ItemStack(fracker), "xxx", "x x", "xxx", 'x', new ItemStack(PolycraftMod.items.get("ingot_element_titanium")));
		FrackerRecipes.addRecipe(new ItemStack(PolycraftMod.blocks.get("ore_mineral_shale")), new ItemStack(PolycraftMod.items.get("compound_natural_gas_fluid_container")), .7f);

		final Item fluidContainer = PolycraftMod.items.get("fluid_container");
		GameRegistry.addRecipe(new ItemStack(fluidContainer), " y ", "x x", " x ", 'x', new ItemStack(PolycraftMod.items.get("ingot_alloy_steel")), 'y', new ItemStack(fluidContainerNozzle));
	}

	private void createCheatRecipes() {
		if (PolycraftMod.cheatRecipesEnabled) {
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.torch, 64), new ItemStack(Blocks.cobblestone));
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.furnace), new ItemStack(Blocks.cobblestone), new ItemStack(Blocks.cobblestone));
			GameRegistry.addShapelessRecipe(new ItemStack(Items.coal, 64), new ItemStack(Blocks.cobblestone), new ItemStack(Blocks.cobblestone), new ItemStack(Blocks.cobblestone));

			final ItemStack dirtStack = new ItemStack(Blocks.dirt);
			final Collection<ItemStack> dirtStacks = new ArrayList<ItemStack>();

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.blocks.get("fracker")), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.crafting_table), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(Items.diamond_pickaxe), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(Items.glass_bottle), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			final ItemStack oilBucketStack = new ItemStack(PolycraftMod.items.get("bucket_oil"));
			GameRegistry.addShapelessRecipe(oilBucketStack, dirtStacks.toArray());

			final Collection<ItemStack> oilBuckets = new ArrayList<ItemStack>();
			for (final Plastic plastic : Plastic.registry.values()) {
				oilBuckets.add(oilBucketStack);
				GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.blocks.get(plastic.gameName)), oilBuckets.toArray());
			}

			final Catalyst platinumCatalyst = Catalyst.registry.get("catalyst_element_platinum");
			// GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.blocks.get(Plastic.registry.get("plastic1").gameName)), oilBucketStack, new ItemStack(PolycraftMod.items.get(platinumCatalyst.gameName)));
		}
	}
}
