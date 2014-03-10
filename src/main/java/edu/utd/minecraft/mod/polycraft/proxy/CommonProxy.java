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
import edu.utd.minecraft.mod.polycraft.Element;
import edu.utd.minecraft.mod.polycraft.Plastic;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockCompressedElement;
import edu.utd.minecraft.mod.polycraft.block.BlockFluid;
import edu.utd.minecraft.mod.polycraft.block.BlockOreElement;
import edu.utd.minecraft.mod.polycraft.block.BlockPlastic;
import edu.utd.minecraft.mod.polycraft.handler.BucketHandler;
import edu.utd.minecraft.mod.polycraft.item.ItemGripped;
import edu.utd.minecraft.mod.polycraft.item.ItemIngotElement;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilDesert;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilOcean;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeInitializer;
import edu.utd.minecraft.mod.polycraft.worldgen.ElementOreWorldGenerator;
import edu.utd.minecraft.mod.polycraft.worldgen.OilPopulate;

public class CommonProxy
{
	public void preInit()
	{
		createBiomes();
		createElements();
		createFluids();
		createPlastics();
		createTools();
	}

	public void init()
	{
		createRecipes();
		GameRegistry.registerWorldGenerator(new ElementOreWorldGenerator(), PolycraftMod.elementOreWorldGeneratorWeight);
	}

	public void postInit()
	{
		MinecraftForge.EVENT_BUS.register(OilPopulate.INSTANCE);
		MinecraftForge.TERRAIN_GEN_BUS.register(new BiomeInitializer());
	}

	private void createBiomes()
	{
		int oilDesertBiomeId = 215;
		int oilOceanBiomeId = 216;
		class BiomeIdException extends RuntimeException {

			public BiomeIdException(String biome, int id) {
				super(String.format("You have a Biome Id conflict at %d for %s", id, biome));
			}
		}

		if (oilDesertBiomeId > 0) {
			if (BiomeGenBase.getBiomeGenArray()[oilDesertBiomeId] != null) {
				throw new BiomeIdException("oilDesert", oilDesertBiomeId);
			}
			PolycraftMod.biomeOilDesert = BiomeGenOilDesert.makeBiome(oilDesertBiomeId);
		}

		if (oilOceanBiomeId > 0) {
			if (BiomeGenBase.getBiomeGenArray()[oilOceanBiomeId] != null) {
				throw new BiomeIdException("oilOcean", oilOceanBiomeId);
			}
			PolycraftMod.biomeOilOcean = BiomeGenOilOcean.makeBiome(oilOceanBiomeId);
		}
	}

	private void createElements()
	{
		for (Element element : Element.elements)
		{
			PolycraftMod.registerBlock(element.blockNameOre, new BlockOreElement(element));
			PolycraftMod.registerItem(element.itemNameIngot, new ItemIngotElement(element));
			PolycraftMod.registerBlock(element.blockNameCompressed, new BlockCompressedElement(element).setHardness(element.oreHardness));
		}
	}

	private void createFluids()
	{
		Fluid fluidOil = new Fluid("oil").setDensity(PolycraftMod.oilFluidDensity).setViscosity(PolycraftMod.oilFluidViscosity);
		FluidRegistry.registerFluid(fluidOil);

		PolycraftMod.blockOil = PolycraftMod.registerBlock("oil",
				new BlockFluid(fluidOil, Material.water).setFlammable(true).setFlammability(PolycraftMod.oilBlockFlammability).setParticleColor(0.7F, 0.7F, 0.0F));
		fluidOil.setBlock(PolycraftMod.blockOil);

		PolycraftMod.registerItem("bucket_oil",
				new ItemBucket(PolycraftMod.blockOil).setContainerItem(Items.bucket).setTextureName(PolycraftMod.getTextureName("bucket_oil")));
		FluidContainerRegistry.registerFluidContainer(
				FluidRegistry.getFluidStack("oil", FluidContainerRegistry.BUCKET_VOLUME),
				new ItemStack(PolycraftMod.items.get("bucket_oil")),
				new ItemStack(Items.bucket));
		BucketHandler.INSTANCE.buckets.put(PolycraftMod.blockOil, PolycraftMod.items.get("bucket_oil"));
		MinecraftForge.EVENT_BUS.register(BucketHandler.INSTANCE);
	}

	private void createPlastics()
	{
		for (Plastic plastic : Plastic.plastics)
		{
			PolycraftMod.registerBlock(plastic.blockName, new BlockPlastic(plastic));
			PolycraftMod.registerItem(plastic.itemNamePellet, new Item().setCreativeTab(CreativeTabs.tabMaterials).setTextureName(PolycraftMod.getTextureName(plastic.itemNamePellet)));
			PolycraftMod.registerItem(plastic.itemNameGrip, new Item().setCreativeTab(CreativeTabs.tabTools).setTextureName(PolycraftMod.getTextureName("plastic_grip")));
		}
	}

	private void createTools()
	{
		for (Entry<String, ToolMaterial> materialEntry : ItemGripped.allowedMaterials.entrySet())
		{
			String materialName = materialEntry.getKey();
			ToolMaterial material = materialEntry.getValue();
			for (Plastic plastic : Plastic.plastics)
				for (String type : ItemGripped.allowedTypes.keySet())
					PolycraftMod.registerItem(ItemGripped.getName(plastic, materialName, type), ItemGripped.create(type, materialName, material, plastic.itemDurabilityBonus));
		}
	}

	private void createRecipes()
	{
		for (Element element : Element.elements)
		{
			Item elementIngot = PolycraftMod.items.get(element.itemNameIngot);
			GameRegistry.addSmelting(
					PolycraftMod.blocks.get(element.blockNameOre),
					new ItemStack(elementIngot, element.oreSmeltingIngotsPerBlock),
					element.oreSmeltingExperience);

			ItemStack elementBlockCompressed = new ItemStack(PolycraftMod.blocks.get(element.blockNameCompressed));
			Object[] elementBlockCompressedIgnots = new ItemStack[element.compressedIngotsPerBlock];
			for (int i = 0; i < elementBlockCompressedIgnots.length; i++)
				elementBlockCompressedIgnots[i] = new ItemStack(elementIngot);
			GameRegistry.addShapelessRecipe(elementBlockCompressed, elementBlockCompressedIgnots);
			GameRegistry.addShapelessRecipe(new ItemStack(elementIngot, element.compressedIngotsPerBlock), elementBlockCompressed);
		}

		for (Plastic plastic : Plastic.plastics)
		{
			Block plasticBlock = PolycraftMod.blocks.get(plastic.blockName);
			Item plasticPellet = PolycraftMod.items.get(plastic.itemNamePellet);
			Item plasticGrip = PolycraftMod.items.get(plastic.itemNameGrip);

			GameRegistry.addShapelessRecipe(new ItemStack(plasticPellet, plastic.pelletsPerBlock), new ItemStack(plasticBlock));
			GameRegistry.addShapelessRecipe(new ItemStack(plasticGrip), new ItemStack(plasticPellet));

			for (String materialName : ItemGripped.allowedMaterials.keySet())
				for (String type : ItemGripped.allowedTypes.keySet())
					GameRegistry.addShapelessRecipe(
							new ItemStack(PolycraftMod.items.get(ItemGripped.getName(plastic, materialName, type))),
							new ItemStack((Item) Item.itemRegistry.getObject(ItemGripped.getNameBase(materialName, type))),
							new ItemStack(plasticGrip));
		}

		if (PolycraftMod.cheatRecipesEnabled)
		{
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
			for (Plastic plastic : Plastic.plastics)
			{
				oilBuckets.add(oilBucketStack);
				Block plasticBlock = PolycraftMod.blocks.get(plastic.blockName);
				GameRegistry.addShapelessRecipe(new ItemStack(plasticBlock), oilBuckets.toArray());
			}
		}
	}
}
