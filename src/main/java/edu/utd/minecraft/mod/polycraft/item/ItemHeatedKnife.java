package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymer;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;

public class ItemHeatedKnife extends ItemTool implements PolycraftItem
{
//    private static final Set field_150915_c = Sets.newHashSet(
//    		new Block[] {Blocks.cobblestone, Blocks.double_stone_slab, Blocks.stone_slab, Blocks.stone, Blocks.sandstone, 
//    				Blocks.mossy_cobblestone, Blocks.iron_ore, Blocks.iron_block, Blocks.coal_ore, Blocks.gold_block, Blocks.gold_ore, 
//    				Blocks.diamond_ore, Blocks.diamond_block, Blocks.ice, Blocks.netherrack, Blocks.lapis_ore, Blocks.lapis_block, 
//    				Blocks.redstone_ore, Blocks.lit_redstone_ore, Blocks.rail, Blocks.detector_rail, Blocks.golden_rail, 
//    				Blocks.activator_rail});
    

	CustomObject config;
	
    public ItemHeatedKnife(final CustomObject config, String iconName)
    {
        //super(2.0F, p_i45347_1_, field_150915_c);
        super(2.0F, ToolMaterial.STONE, null);
        this.config = config;
		//this.setTextureName(PolycraftMod.getAssetName("heated_knife"));
		this.setCreativeTab(CreativeTabs.tabTools);
		//if (config.maxStackSize > 0)
			//this.setMaxStackSize(config.maxStackSize);
		//TODO: implement iconName...
    }

    @Override
    public boolean canHarvestBlock(Block p_150897_1_)
    {
        return p_150897_1_ instanceof BlockPolymer;
        
//        		== Blocks.obsidian ? this.toolMaterial.getHarvestLevel() == 3 : 
//        	(p_150897_1_ != Blocks.diamond_block && p_150897_1_ != Blocks.diamond_ore ? 
//        	(p_150897_1_ != Blocks.emerald_ore && p_150897_1_ != Blocks.emerald_block ? 
//        	(p_150897_1_ != Blocks.gold_block && p_150897_1_ != Blocks.gold_ore ? 
//        	(p_150897_1_ != Blocks.iron_block && p_150897_1_ != Blocks.iron_ore ? 
//        	(p_150897_1_ != Blocks.lapis_block && p_150897_1_ != Blocks.lapis_ore ? 
//        	(p_150897_1_ != Blocks.redstone_ore && p_150897_1_ != Blocks.lit_redstone_ore ? 
//        	(p_150897_1_.getMaterial() == Material.rock ? true :         		
//        	(p_150897_1_.getMaterial() == Material.iron ? true : 
//        	p_150897_1_.getMaterial() == Material.anvil)) : 
//        	this.toolMaterial.getHarvestLevel() >= 2) : 
//        	this.toolMaterial.getHarvestLevel() >= 1) : 
//        	this.toolMaterial.getHarvestLevel() >= 1) : 
//        	this.toolMaterial.getHarvestLevel() >= 2) : 
//        	this.toolMaterial.getHarvestLevel() >= 2) : 
//        	this.toolMaterial.getHarvestLevel() >= 2);
    }

    @Override
    public float getStrVsBlock(ItemStack p_150893_1_, Block p_150893_2_)
    {
        return config.params.getFloat(0);
        		//p_150893_2_.getMaterial() != Material.iron && p_150893_2_.getMaterial() != Material.anvil && p_150893_2_.getMaterial() != Material.rock ? super.func_150893_a(p_150893_1_, p_150893_2_) : this.efficiencyOnProperMaterial;
    }

	@Override
	public ItemCategory getCategory() {
		// TODO Auto-generated method stub
		return ItemCategory.TOOLS;
	}
}