package edu.utd.minecraft.mod.polycraft.proxy;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
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

public class CommonProxy
{
	public void preInit()
	{
		Fluid fluidOil = new Fluid("oil").setDensity(800).setViscosity(1500);
		FluidRegistry.registerFluid(fluidOil);

        Block blockOil;
		if (fluidOil.getBlock() == null)
		{
			blockOil = new BlockFluid(fluidOil, Material.water).setFlammable(true).setFlammability(5).setParticleColor(0.7F, 0.7F, 0.0F);
			blockOil.setBlockName("blockOil");
			GameRegistry.registerBlock(blockOil, "oil");
			fluidOil.setBlock(blockOil);		
		}
		else
		{
			blockOil = fluidOil.getBlock();
		}
		
		if (blockOil != null)
		{
			Item bucketOil = new ItemBucket(blockOil);
			bucketOil.setTextureName(PolycraftMod.MODID + ":bucketOil");
			bucketOil.setUnlocalizedName("bucketOil").setContainerItem(Items.bucket);
			LanguageRegistry.addName(bucketOil, "bucketOil");
			GameRegistry.registerItem(bucketOil, "Oil Bucket");
			FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluidStack("oil", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(bucketOil), new ItemStack(Items.bucket));
			BucketHandler.INSTANCE.buckets.put(blockOil, bucketOil);
			MinecraftForge.EVENT_BUS.register(BucketHandler.INSTANCE);

	    	//Dirt to oil to diamonds!
	        ItemStack dirtStack = new ItemStack(Blocks.dirt);
	        ItemStack oilBucketStack = new ItemStack(bucketOil);
	        ItemStack diamondsStack = new ItemStack(Items.diamond, 64);
	        
			GameRegistry.addShapelessRecipe(oilBucketStack, dirtStack);
			GameRegistry.addShapelessRecipe(diamondsStack, oilBucketStack);
		}
	}
	
	public void init()
	{
	}
	
	public void postInit()
	{
		
	}
}
