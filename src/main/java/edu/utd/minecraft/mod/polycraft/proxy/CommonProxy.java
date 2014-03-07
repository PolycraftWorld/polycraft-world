package edu.utd.minecraft.mod.polycraft.proxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

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
import edu.utd.minecraft.mod.polycraft.Plastic;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockFluid;
import edu.utd.minecraft.mod.polycraft.block.BlockPlastic;
import edu.utd.minecraft.mod.polycraft.handler.BucketHandler;
import edu.utd.minecraft.mod.polycraft.item.ItemAxeGripped;
import edu.utd.minecraft.mod.polycraft.item.ItemGripped;
import edu.utd.minecraft.mod.polycraft.item.ItemHoeGripped;
import edu.utd.minecraft.mod.polycraft.item.ItemPickaxeGripped;
import edu.utd.minecraft.mod.polycraft.item.ItemShovelGripped;
import edu.utd.minecraft.mod.polycraft.item.ItemSwordGripped;
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
		Fluid fluidOil = new Fluid("oil").setDensity(PolycraftMod.oilFluidDensity).setViscosity(PolycraftMod.oilFluidViscosity);
		FluidRegistry.registerFluid(fluidOil);

		PolycraftMod.blockOil = PolycraftMod.registerBlock("oil", new BlockFluid(fluidOil, Material.water).setFlammable(true).setFlammability(PolycraftMod.oilBlockFlammability).setParticleColor(0.7F, 0.7F, 0.0F));
		fluidOil.setBlock(PolycraftMod.blockOil);
		
		for (Plastic plastic : Plastic.plastics)
			PolycraftMod.registerBlock(plastic.gameName, new BlockPlastic(plastic));
	}
	
	private void createItems()
	{
		PolycraftMod.registerItem("bucket_oil", new ItemBucket(PolycraftMod.blockOil).setContainerItem(Items.bucket).setTextureName(PolycraftMod.getTextureName("bucket_oil")));
		FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluidStack("oil", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(PolycraftMod.items.get("bucket_oil")), new ItemStack(Items.bucket));
		BucketHandler.INSTANCE.buckets.put(PolycraftMod.blockOil, PolycraftMod.items.get("bucket_oil"));
		MinecraftForge.EVENT_BUS.register(BucketHandler.INSTANCE);

		for (Plastic plastic : Plastic.plastics)
		{
			PolycraftMod.registerItem(plastic.gameName + "_pellet", new Item().setCreativeTab(CreativeTabs.tabMaterials).setTextureName(PolycraftMod.getTextureName(plastic.gameName + "_pellet")));
			PolycraftMod.registerItem(plastic.gameName + "_grip", new Item().setCreativeTab(CreativeTabs.tabTools).setTextureName(PolycraftMod.getTextureName("plastic_grip")));
		}
		
		for (Entry<String, ToolMaterial> materialEntry : ItemGripped.allowedMaterials.entrySet())
		{
			String materialName = materialEntry.getKey();
			ToolMaterial material = materialEntry.getValue();
			for (Plastic plastic : Plastic.plastics)
			{
				String prefix = ItemGripped.getNamePrefix(plastic, materialName);
				PolycraftMod.registerItem(prefix + "_shovel", new ItemShovelGripped(materialName, material, plastic.itemDurabilityBonus));
				PolycraftMod.registerItem(prefix + "_axe", new ItemAxeGripped(materialName, material, plastic.itemDurabilityBonus));
				PolycraftMod.registerItem(prefix + "_pickaxe", new ItemPickaxeGripped(materialName, material, plastic.itemDurabilityBonus));
				PolycraftMod.registerItem(prefix + "_hoe", new ItemHoeGripped(materialName, material, plastic.itemDurabilityBonus));
				PolycraftMod.registerItem(prefix + "_sword", new ItemSwordGripped(materialName, material, plastic.itemDurabilityBonus));
			}
		}
	}

	private void createRecipes()
	{
        ItemStack dirtStack = new ItemStack(Blocks.dirt);
        ItemStack oilBucketStack = new ItemStack(PolycraftMod.items.get("bucket_oil"));
		GameRegistry.addShapelessRecipe(oilBucketStack, dirtStack);

		Collection<ItemStack> inputs = new ArrayList<ItemStack>();
		for (Plastic plastic : Plastic.plastics)
		{
			inputs.add(oilBucketStack);
			
			Block plasticBlock = PolycraftMod.blocks.get(plastic.gameName);
			Item plasticPellet = PolycraftMod.items.get(plastic.gameName + "_pellet");
			Item plasticGrip = PolycraftMod.items.get(plastic.gameName + "_grip");
			
			GameRegistry.addShapelessRecipe(new ItemStack(plasticBlock), inputs.toArray());
			GameRegistry.addShapelessRecipe(new ItemStack(plasticPellet, plastic.pelletsPerBlock), new ItemStack(plasticBlock));
			GameRegistry.addShapelessRecipe(new ItemStack(plasticGrip), new ItemStack(plasticPellet));
			
			for (String material : ItemGripped.allowedMaterials.keySet())
			{
				String prefix = ItemGripped.getNamePrefix(plastic, material);
				for (String itemName : ItemGripped.allowedItems)
					GameRegistry.addShapelessRecipe(
							new ItemStack(PolycraftMod.items.get(prefix + "_" + itemName)),
							new ItemStack((Item)Item.itemRegistry.getObject(material + "_" + itemName)),
							new ItemStack(plasticGrip));
			}
		}
	}
}
