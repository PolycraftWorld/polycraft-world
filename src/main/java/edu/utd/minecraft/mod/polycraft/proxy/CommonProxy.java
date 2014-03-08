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
import edu.utd.minecraft.mod.polycraft.block.BlockMoreOre;
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
import edu.utd.minecraft.mod.polycraft.item.ItemCatalyst;
import edu.utd.minecraft.mod.polycraft.item.ItemChemicalGas;
import edu.utd.minecraft.mod.polycraft.item.ItemChemicalLiquid;
import edu.utd.minecraft.mod.polycraft.item.ItemPickaxeGripped;


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
	
	private BlockMoreOre registerBlockMoreOre(String type)
	{
		BlockMoreOre blockMoreOre = new BlockMoreOre().setType(type);
		String name = "block_ore_" + type;
		blockMoreOre.setBlockName(name);
		GameRegistry.registerBlock(blockMoreOre, name);
		return blockMoreOre;
	}
	
	private void createBlocks()
	{
		Fluid fluidOil = new Fluid("oil").setDensity(PolycraftMod.oilFluidDensity).setViscosity(PolycraftMod.oilFluidViscosity);
		FluidRegistry.registerFluid(fluidOil);

		PolycraftMod.blockOil = PolycraftMod.registerBlock("oil", new BlockFluid(fluidOil, Material.water).setFlammable(true).setFlammability(PolycraftMod.oilBlockFlammability).setParticleColor(0.7F, 0.7F, 0.0F));
		fluidOil.setBlock(PolycraftMod.blockOil);
		
		for (Plastic plastic : Plastic.plastics)
			PolycraftMod.registerBlock(plastic.gameName, new BlockPlastic(plastic));
		
		PolycraftMod.platinumOre = registerBlockMoreOre("platinum");
		PolycraftMod.titaniumOre = registerBlockMoreOre("titanium");
		PolycraftMod.palladiumOre = registerBlockMoreOre("palladium");
		PolycraftMod.cobaltOre = registerBlockMoreOre("cobalt");
		PolycraftMod.manganeseOre = registerBlockMoreOre("manganese");
		PolycraftMod.magnesiumOre = registerBlockMoreOre("magnesium");
		PolycraftMod.antimonyOre = registerBlockMoreOre("antimony");
		PolycraftMod.bauxite = registerBlockMoreOre("bauxite");
		PolycraftMod.copperOre = registerBlockMoreOre("copper");
		PolycraftMod.bitumen = registerBlockMoreOre("bitumen");
		

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
		
		
		PolycraftMod.platinum = PolycraftMod.registerItem(new Item().setCreativeTab(CreativeTabs.tabMaterials), "platinum");
		PolycraftMod.titanium= PolycraftMod.registerItem(new Item().setCreativeTab(CreativeTabs.tabMaterials), "titanium");
		PolycraftMod.palladium = PolycraftMod.registerItem(new Item().setCreativeTab(CreativeTabs.tabMaterials), "palladium");
		PolycraftMod.cobalt = PolycraftMod.registerItem(new Item().setCreativeTab(CreativeTabs.tabMaterials), "cobalt");
		PolycraftMod.manganese = PolycraftMod.registerItem(new Item().setCreativeTab(CreativeTabs.tabMaterials), "manganese");
		PolycraftMod.magnesium = PolycraftMod.registerItem(new Item().setCreativeTab(CreativeTabs.tabMaterials), "magnesium");
		PolycraftMod.antimony = PolycraftMod.registerItem(new Item().setCreativeTab(CreativeTabs.tabMaterials), "antimony");
		PolycraftMod.aluminum = PolycraftMod.registerItem(new Item().setCreativeTab(CreativeTabs.tabMaterials), "aluminum");
		PolycraftMod.copper = PolycraftMod.registerItem(new Item().setCreativeTab(CreativeTabs.tabMaterials), "copper");
		
		
	    PolycraftMod.platinumCatalyst = (ItemCatalyst) PolycraftMod.registerItem(new ItemCatalyst(Material.rock).setCreativeTab(CreativeTabs.tabBrewing), "platinum_catalyst");
	    PolycraftMod.titaniumCatalyst = (ItemCatalyst) PolycraftMod.registerItem(new ItemCatalyst(Material.rock).setCreativeTab(CreativeTabs.tabBrewing), "titanium_catalyst");
	    PolycraftMod.cobaltCatalyst = (ItemCatalyst) PolycraftMod.registerItem(new ItemCatalyst(Material.rock).setCreativeTab(CreativeTabs.tabBrewing), "cobalt_catalyst");
	    PolycraftMod.manganeseCatalyst = (ItemCatalyst) PolycraftMod.registerItem(new ItemCatalyst(Material.rock).setCreativeTab(CreativeTabs.tabBrewing), "manganese_catalyst");
	    PolycraftMod.magnesiumOxideBaseCatalyst = (ItemCatalyst) PolycraftMod.registerItem(new ItemCatalyst(Material.rock).setCreativeTab(CreativeTabs.tabBrewing), "magnesium_oxide_base_catalyst");
	    PolycraftMod.antimonyTrioxideBaseCatalyst = (ItemCatalyst) PolycraftMod.registerItem(new ItemCatalyst(Material.rock).setCreativeTab(CreativeTabs.tabBrewing), "antimony_trioxide_base_catalyst");
	    PolycraftMod.copperIIChlorideCatalyst = (ItemCatalyst) PolycraftMod.registerItem(new ItemCatalyst(Material.rock).setCreativeTab(CreativeTabs.tabBrewing), "copperII_chloride_catalyst");
	    PolycraftMod.ironIIIChlorideCatalyst = (ItemCatalyst) PolycraftMod.registerItem(new ItemCatalyst(Material.rock).setCreativeTab(CreativeTabs.tabBrewing), "ironIII_chloride_catalyst");
	    PolycraftMod.ironIIIOxideCatalyst = (ItemCatalyst) PolycraftMod.registerItem(new ItemCatalyst(Material.rock).setCreativeTab(CreativeTabs.tabBrewing), "ironIII_oxide_catalyst");
	    PolycraftMod.zieglerNattaCatalyst = (ItemCatalyst) PolycraftMod.registerItem(new ItemCatalyst(Material.rock).setCreativeTab(CreativeTabs.tabBrewing), "Ziegler-Natta_catalyst");
	    PolycraftMod.cobaltManganeseBromideCatalyst = (ItemCatalyst) PolycraftMod.registerItem(new ItemCatalyst(Material.rock).setCreativeTab(CreativeTabs.tabBrewing), "cobalt-manganese-bromide_catalyst");
	    

	    PolycraftMod.emptyCylinder = (ItemChemicalGas) PolycraftMod.registerItem(new ItemChemicalGas(Material.rock).setCreativeTab(CreativeTabs.tabBrewing), "empty_cylinder");
	    PolycraftMod.HCLCylinder = (ItemChemicalGas) PolycraftMod.registerItem(new ItemChemicalGas(Material.rock).setCreativeTab(CreativeTabs.tabBrewing), "hcl_cylinder");
	    PolycraftMod.naturalGasCylinder = (ItemChemicalGas) PolycraftMod.registerItem(new ItemChemicalGas(Material.rock).setCreativeTab(CreativeTabs.tabBrewing), "natural_gas_cylinder");
	    
	    PolycraftMod.methaneCylinder = (ItemChemicalGas) PolycraftMod.registerItem(new ItemChemicalGas(Material.air).setCreativeTab(CreativeTabs.tabBrewing), "methane_cylinder");
	    PolycraftMod.ethaneCylinder = (ItemChemicalGas) PolycraftMod.registerItem(new ItemChemicalGas(Material.air).setCreativeTab(CreativeTabs.tabBrewing), "ethane_cylinder");
	    PolycraftMod.propaneCylinder = (ItemChemicalGas) PolycraftMod.registerItem(new ItemChemicalGas(Material.air).setCreativeTab(CreativeTabs.tabBrewing), "propane_cylinder");
	    PolycraftMod.butaneCylinder = (ItemChemicalGas) PolycraftMod.registerItem(new ItemChemicalGas(Material.air).setCreativeTab(CreativeTabs.tabBrewing), "butane_cylinder");
	    
	    PolycraftMod.emptyBottle = (ItemChemicalLiquid) PolycraftMod.registerItem(new ItemChemicalLiquid(Material.water).setCreativeTab(CreativeTabs.tabBrewing), "empty_bottle");
	    PolycraftMod.crudeOilBottle = (ItemChemicalLiquid) PolycraftMod.registerItem(new ItemChemicalLiquid(Material.water).setCreativeTab(CreativeTabs.tabBrewing), "crude_oil");
	    PolycraftMod.waterBottle = (ItemChemicalLiquid) PolycraftMod.registerItem(new ItemChemicalLiquid(Material.water).setCreativeTab(CreativeTabs.tabBrewing), "water_bottle");
	    PolycraftMod.bromineBottle = (ItemChemicalLiquid) PolycraftMod.registerItem(new ItemChemicalLiquid(Material.water).setCreativeTab(CreativeTabs.tabBrewing), "bromine_bottle");
	    PolycraftMod.chlorineBottle = (ItemChemicalLiquid) PolycraftMod.registerItem(new ItemChemicalLiquid(Material.water).setCreativeTab(CreativeTabs.tabBrewing), "chlorine_bottle");
	    PolycraftMod.naphthaBottle = (ItemChemicalLiquid) PolycraftMod.registerItem(new ItemChemicalLiquid(Material.water).setCreativeTab(CreativeTabs.tabBrewing), "naphtha_bottle");
	    PolycraftMod.dieselBottle = (ItemChemicalLiquid) PolycraftMod.registerItem(new ItemChemicalLiquid(Material.water).setCreativeTab(CreativeTabs.tabBrewing), "diesel_bottle");
	    PolycraftMod.kerosineBottle = (ItemChemicalLiquid) PolycraftMod.registerItem(new ItemChemicalLiquid(Material.water).setCreativeTab(CreativeTabs.tabBrewing), "kerosine_bottle");
	    PolycraftMod.ethyleneBottle = (ItemChemicalLiquid) PolycraftMod.registerItem(new ItemChemicalLiquid(Material.water).setCreativeTab(CreativeTabs.tabBrewing), "ethylene_bottle");
	    PolycraftMod.propyleneBottle = (ItemChemicalLiquid) PolycraftMod.registerItem(new ItemChemicalLiquid(Material.water).setCreativeTab(CreativeTabs.tabBrewing), "propylene_bottle");
	    PolycraftMod.butadieneBottle = (ItemChemicalLiquid) PolycraftMod.registerItem(new ItemChemicalLiquid(Material.water).setCreativeTab(CreativeTabs.tabBrewing), "butadiene_bottle");
	    PolycraftMod.olefinBottle = (ItemChemicalLiquid) PolycraftMod.registerItem(new ItemChemicalLiquid(Material.water).setCreativeTab(CreativeTabs.tabBrewing), "olefin_bottle");
	    PolycraftMod.paraffinBottle = (ItemChemicalLiquid) PolycraftMod.registerItem(new ItemChemicalLiquid(Material.water).setCreativeTab(CreativeTabs.tabBrewing), "paraffin_bottle");
	    PolycraftMod.ethyleneOxideBottle = (ItemChemicalLiquid) PolycraftMod.registerItem(new ItemChemicalLiquid(Material.water).setCreativeTab(CreativeTabs.tabBrewing), "ethylene_oxide_bottle");
	    PolycraftMod.ethyleneGlycolBottle = (ItemChemicalLiquid) PolycraftMod.registerItem(new ItemChemicalLiquid(Material.water).setCreativeTab(CreativeTabs.tabBrewing), "ethylene_glycol_bottle");
	    PolycraftMod.BTXBottle =(ItemChemicalLiquid)  PolycraftMod.registerItem(new ItemChemicalLiquid(Material.water).setCreativeTab(CreativeTabs.tabBrewing), "btx_bottle");
	    PolycraftMod.terephthalicAcidBottle = (ItemChemicalLiquid) PolycraftMod.registerItem(new ItemChemicalLiquid(Material.water).setCreativeTab(CreativeTabs.tabBrewing), "terephthalic_acid_bottle");
	    PolycraftMod.methanolBottle = (ItemChemicalLiquid) PolycraftMod.registerItem(new ItemChemicalLiquid(Material.water).setCreativeTab(CreativeTabs.tabBrewing), "methanolBottle");
	    PolycraftMod.dimethylTerephthalateBottle = (ItemChemicalLiquid) PolycraftMod.registerItem(new ItemChemicalLiquid(Material.water).setCreativeTab(CreativeTabs.tabBrewing), "dimethyl_terephthalate_bottle");
	    PolycraftMod.EDCBottle = (ItemChemicalLiquid) PolycraftMod.registerItem(new ItemChemicalLiquid(Material.water).setCreativeTab(CreativeTabs.tabBrewing), "edc_bottle");
	    PolycraftMod.vinylChlorideBottle = (ItemChemicalLiquid) PolycraftMod.registerItem(new ItemChemicalLiquid(Material.water).setCreativeTab(CreativeTabs.tabBrewing), "vinyl_chloride_bottle");
	    PolycraftMod.acetyleneBottle = (ItemChemicalLiquid) PolycraftMod.registerItem(new ItemChemicalLiquid(Material.water).setCreativeTab(CreativeTabs.tabBrewing), "acetylene_bottle");
	    PolycraftMod.sulfuricAcidBottle = (ItemChemicalLiquid) PolycraftMod.registerItem(new ItemChemicalLiquid(Material.water).setCreativeTab(CreativeTabs.tabBrewing), "sulfuric_acid_bottle");
	    PolycraftMod.benzeneBottle = (ItemChemicalLiquid) PolycraftMod.registerItem(new ItemChemicalLiquid(Material.water).setCreativeTab(CreativeTabs.tabBrewing), "benzene_bottle");
	    PolycraftMod.ethylBenzeneBottle = (ItemChemicalLiquid) PolycraftMod.registerItem(new ItemChemicalLiquid(Material.water).setCreativeTab(CreativeTabs.tabBrewing), "ethyl_benzene_bottle");
	    PolycraftMod.styreneBottle = (ItemChemicalLiquid) PolycraftMod.registerItem(new ItemChemicalLiquid(Material.water).setCreativeTab(CreativeTabs.tabBrewing), "styrene_bottle");  

		
		
		
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
