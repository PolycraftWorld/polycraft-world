package edu.utd.minecraft.mod.polycraft.proxy;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
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
import edu.utd.minecraft.mod.polycraft.handler.BucketHandler;
import edu.utd.minecraft.mod.polycraft.item.ItemPickaxePlasticHandle;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilDesert;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilOcean;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeInitializer;
import edu.utd.minecraft.mod.polycraft.worldgen.OilPopulate;

public class CommonProxy
{
	public void preInit()
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
		
		Fluid fluidOil = new Fluid("oil").setDensity(800).setViscosity(1500);
		FluidRegistry.registerFluid(fluidOil);

        PolycraftMod.blockOil = fluidOil.getBlock();
		if (PolycraftMod.blockOil == null)
		{
			PolycraftMod.blockOil = new BlockFluid(fluidOil, Material.water).setFlammable(true).setFlammability(5).setParticleColor(0.7F, 0.7F, 0.0F).setBlockName("blockOil");
			GameRegistry.registerBlock(PolycraftMod.blockOil, "blockOil");
			fluidOil.setBlock(PolycraftMod.blockOil);		
		}
		
		PolycraftMod.bucketOil = new ItemBucket(PolycraftMod.blockOil);
		PolycraftMod.bucketOil.setUnlocalizedName("bucketOil").setContainerItem(Items.bucket);
		PolycraftMod.bucketOil.setTextureName(PolycraftMod.MODID + ":bucket_oil");
		GameRegistry.registerItem(PolycraftMod.bucketOil, PolycraftMod.bucketOil.getUnlocalizedName());
		FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluidStack("oil", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(PolycraftMod.bucketOil), new ItemStack(Items.bucket));
		BucketHandler.INSTANCE.buckets.put(PolycraftMod.blockOil, PolycraftMod.bucketOil);
		MinecraftForge.EVENT_BUS.register(BucketHandler.INSTANCE);
		
		PolycraftMod.plasticHandle = new Item().setUnlocalizedName("plasticHandle").setCreativeTab(CreativeTabs.tabTools).setTextureName(PolycraftMod.MODID + ":plastic_handle");
		GameRegistry.registerItem(PolycraftMod.plasticHandle, "plasticHandle");

		Item plasticHandleWoodPickaxe = new ItemPickaxePlasticHandle(ToolMaterial.WOOD).setUnlocalizedName("plasticHandleWoodPickaxe").setCreativeTab(CreativeTabs.tabTools).setTextureName(PolycraftMod.MODID + ":plastic_handle_wood_pickaxe");
		GameRegistry.registerItem(plasticHandleWoodPickaxe, "plasticHandleWoodPickaxe");
	}
	
	public void init()
	{
        ItemStack dirtStack = new ItemStack(Blocks.dirt);
        ItemStack oilBucketStack = new ItemStack(PolycraftMod.bucketOil);
        ItemStack plasticHandleStack = new ItemStack(PolycraftMod.plasticHandle);
        ItemStack woodPickaxeStack = new ItemStack(Items.wooden_axe);
        ItemStack plasticHandleWoodPickaxeStack = new ItemStack(PolycraftMod.plasticHandleWoodPickaxe);
        
		GameRegistry.addShapelessRecipe(oilBucketStack, dirtStack);
		GameRegistry.addShapelessRecipe(plasticHandleStack, oilBucketStack);
		GameRegistry.addShapelessRecipe(plasticHandleWoodPickaxeStack, plasticHandleStack, woodPickaxeStack);
	}
	
	public void postInit()
	{
		MinecraftForge.EVENT_BUS.register(OilPopulate.INSTANCE);
		MinecraftForge.TERRAIN_GEN_BUS.register(new BiomeInitializer());
	}
}
