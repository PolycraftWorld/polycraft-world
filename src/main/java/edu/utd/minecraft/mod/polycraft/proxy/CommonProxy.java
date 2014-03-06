package edu.utd.minecraft.mod.polycraft.proxy;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockFluid;
import edu.utd.minecraft.mod.polycraft.block.BlockPlastic;
import edu.utd.minecraft.mod.polycraft.handler.BucketHandler;
import edu.utd.minecraft.mod.polycraft.item.ItemPickaxeGripped;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilDesert;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilOcean;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeInitializer;
import edu.utd.minecraft.mod.polycraft.worldgen.OilPopulate;

public class CommonProxy
{
	public void preInit()
	{
		createBiomes();
		createBlocks();
		createItems();
	}

	public void init()
	{
		createRecipes();
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
			if (BiomeGenBase.getBiomeGenArray () [oilDesertBiomeId] != null) {
				throw new BiomeIdException("oilDesert", oilDesertBiomeId);
			}
			PolycraftMod.biomeOilDesert = BiomeGenOilDesert.makeBiome(oilDesertBiomeId);
		}

		if (oilOceanBiomeId > 0) {
			if (BiomeGenBase.getBiomeGenArray () [oilOceanBiomeId] != null) {
				throw new BiomeIdException("oilOcean", oilOceanBiomeId);
			}
			PolycraftMod.biomeOilOcean = BiomeGenOilOcean.makeBiome(oilOceanBiomeId);
		}
	}
	
	private void createBlocks()
	{
		Fluid fluidOil = new Fluid("oil").setDensity(800).setViscosity(1500);
		FluidRegistry.registerFluid(fluidOil);

		PolycraftMod.blockOil = registerBlock(new BlockFluid(fluidOil, Material.water).setFlammable(true).setFlammability(5).setParticleColor(0.7F, 0.7F, 0.0F), "block_oil");
		fluidOil.setBlock(PolycraftMod.blockOil);		
		
		PolycraftMod.blockPlastic1PET = registerBlockPlastic("pet", .1);
		PolycraftMod.blockPlastic2PEHD = registerBlockPlastic("pehd", 1);
		PolycraftMod.blockPlastic3PVC = registerBlockPlastic("pvc", .2);
		PolycraftMod.blockPlastic4PELD = registerBlockPlastic("peld", .05);
		PolycraftMod.blockPlastic5PP = registerBlockPlastic("pp", .4);
		PolycraftMod.blockPlastic6PS = registerBlockPlastic("ps", .3);
		PolycraftMod.blockPlastic7O = registerBlockPlastic("o", 2);
	}
	
	private Block registerBlock(Block block, String name)
	{
		block.setBlockName(name);
		GameRegistry.registerBlock(block, name);
		return block;
	}
	
	private BlockPlastic registerBlockPlastic(String type, double durability)
	{
		BlockPlastic blockPlastic = new BlockPlastic().setDurability(durability).setType(type);
		String name = "block_plastic_" + type;
		blockPlastic.setBlockName(name);
		GameRegistry.registerBlock(blockPlastic, name);
		return blockPlastic;
	}
	
	private void createItems()
	{
		PolycraftMod.bucketOil = registerItem(new ItemBucket(PolycraftMod.blockOil).setContainerItem(Items.bucket), "bucket_oil");
		FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluidStack("oil", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(PolycraftMod.bucketOil), new ItemStack(Items.bucket));
		BucketHandler.INSTANCE.buckets.put(PolycraftMod.blockOil, PolycraftMod.bucketOil);
		MinecraftForge.EVENT_BUS.register(BucketHandler.INSTANCE);
		
		PolycraftMod.grip = registerItem(new Item().setCreativeTab(CreativeTabs.tabTools), "grip");
		PolycraftMod.grippedWoodPickaxe = registerItem(new ItemPickaxeGripped(ToolMaterial.WOOD).setCreativeTab(CreativeTabs.tabTools), "gripped_wood_pickaxe");
		PolycraftMod.grippedStonePickaxe = registerItem(new ItemPickaxeGripped(ToolMaterial.STONE).setCreativeTab(CreativeTabs.tabTools), "gripped_stone_pickaxe");
	}
	
	private Item registerItem(Item item, String name)
	{
		item.setUnlocalizedName(name);
		item.setTextureName(PolycraftMod.getTextureName(name));
		GameRegistry.registerItem(item, name);
		return item;
	}

	private void createRecipes()
	{
        ItemStack dirtStack = new ItemStack(Blocks.dirt);
        ItemStack oilBucketStack = new ItemStack(PolycraftMod.bucketOil);
        ItemStack plastic1PETStack = new ItemStack(PolycraftMod.blockPlastic1PET);
        ItemStack plastic2PEHDStack = new ItemStack(PolycraftMod.blockPlastic2PEHD);
        ItemStack plastic3PVCStack = new ItemStack(PolycraftMod.blockPlastic3PVC);
        ItemStack plastic4PELDStack = new ItemStack(PolycraftMod.blockPlastic4PELD);
        ItemStack plastic5PPStack = new ItemStack(PolycraftMod.blockPlastic5PP);
        ItemStack plastic6PSStack = new ItemStack(PolycraftMod.blockPlastic6PS);
        ItemStack plastic7OStack = new ItemStack(PolycraftMod.blockPlastic7O);
        ItemStack gripStack = new ItemStack(PolycraftMod.grip);
        ItemStack woodPickaxeStack = new ItemStack(Items.wooden_pickaxe);
        ItemStack stonePickaxeStack = new ItemStack(Items.stone_pickaxe);
        ItemStack grippedWoodPickaxeStack = new ItemStack(PolycraftMod.grippedWoodPickaxe);
        ItemStack grippedStonePickaxeStack = new ItemStack(PolycraftMod.grippedStonePickaxe);
        
        
		GameRegistry.addShapelessRecipe(oilBucketStack, dirtStack);
		GameRegistry.addShapelessRecipe(plastic1PETStack, oilBucketStack);
		GameRegistry.addShapelessRecipe(plastic2PEHDStack, oilBucketStack, oilBucketStack);
		GameRegistry.addShapelessRecipe(plastic3PVCStack, oilBucketStack, oilBucketStack, oilBucketStack);
		GameRegistry.addShapelessRecipe(plastic4PELDStack, oilBucketStack, oilBucketStack, oilBucketStack, oilBucketStack);
		GameRegistry.addShapelessRecipe(plastic5PPStack, oilBucketStack, oilBucketStack, oilBucketStack, oilBucketStack, oilBucketStack);
		GameRegistry.addShapelessRecipe(plastic6PSStack, oilBucketStack, oilBucketStack, oilBucketStack, oilBucketStack, oilBucketStack, oilBucketStack);
		GameRegistry.addShapelessRecipe(plastic7OStack, oilBucketStack, oilBucketStack, oilBucketStack, oilBucketStack, oilBucketStack, oilBucketStack, oilBucketStack);
		GameRegistry.addShapelessRecipe(gripStack, plastic1PETStack);
		GameRegistry.addShapelessRecipe(gripStack, plastic2PEHDStack);
		GameRegistry.addShapelessRecipe(gripStack, plastic3PVCStack);
		GameRegistry.addShapelessRecipe(gripStack, plastic4PELDStack);
		GameRegistry.addShapelessRecipe(gripStack, plastic5PPStack);
		GameRegistry.addShapelessRecipe(gripStack, plastic6PSStack);
		GameRegistry.addShapelessRecipe(gripStack, plastic7OStack);
		GameRegistry.addShapelessRecipe(grippedWoodPickaxeStack, gripStack, woodPickaxeStack);
		GameRegistry.addShapelessRecipe(grippedStonePickaxeStack, gripStack, stonePickaxeStack);
	}
}
