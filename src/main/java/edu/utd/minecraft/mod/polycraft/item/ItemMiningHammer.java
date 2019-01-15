package edu.utd.minecraft.mod.polycraft.item;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ItemMiningHammer extends ItemCustom {

    private String toolClass;
    private float efficiencyOnProperMaterial;
    private Item.ToolMaterial toolMaterial;
	public ItemMiningHammer(CustomObject config) {
		super(config);
		this.setCreativeTab(CreativeTabs.tabTools);
		this.setTextureName(PolycraftMod.getAssetName("ItemMiningHammer"));
		this.toolMaterial = Item.ToolMaterial.EMERALD;
        this.efficiencyOnProperMaterial = Item.ToolMaterial.EMERALD.getEfficiencyOnProperMaterial();
		toolClass = "pickaxe";
        this.setMaxDamage(this.toolMaterial.getMaxUses());
		// TODO Auto-generated constructor stub
	}
int q;

@Override
public String getItemStackDisplayName(ItemStack parItemstack)
{
	return "Mining Hammer";
}
@Override
public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player)
{
	
	return false;
}
@Override
public boolean onBlockDestroyed(ItemStack itemstack, World world, Block block, int x, int y, int z, EntityLivingBase p_150894_7_)
{
    for(int i=-1;i<=1;i++)
	{
		for(int j=-1;j<=1;j++)
		{
			for(int k=-1;k<=1;k++)
			{
				int a=i+x;
				int b=j+y;
				int c=k+z;
				if(!world.getBlock(a, b, c).isAir(world, a, b, c) && !(world.getBlock(a, b, c)== Blocks.leaves)&& !(world.getBlock(a, b, c)== Blocks.leaves2)&& !(world.getBlock(a, b, c)== Blocks.grass)&& !(world.getBlock(a, b, c)== Blocks.tallgrass)&& !(world.getBlock(a, b, c)== Blocks.gravel) && !(world.getBlock(a, b, c)== Blocks.dirt) && !(world.getBlock(a, b, c)== Blocks.clay) && !(world.getBlock(a, b, c)== Blocks.sand) && !(world.getBlock(a, b, c)== Blocks.planks) && !(world.getBlock(a, b, c)== Blocks.log) && !(world.getBlock(a, b, c)== Blocks.wool))
				{
					if (world.getBlock(a, b, c).getBlockHardness(world, a, b, c)!=-1.0F )
					{
						
						world.getBlock(a, b, c).dropBlockAsItem(world, a, b, c, 1, 0);
						world.setBlockToAir(a, b, c);
						q++;
					}
				}
			}
		}
	}
    if ((double)block.getBlockHardness(world, x, y, z) != 0.0D)
    {
        itemstack.damageItem(q, p_150894_7_);
    }
    q = 0;
    return true;
}

//@Override
//public float getDigSpeed(ItemStack itemstack, Block block, int metadata)
//{
//    return 1.0F;
//}
@Override
public boolean func_150897_b(Block p_150897_1_)
{
	 return p_150897_1_ == Blocks.obsidian ? this.toolMaterial.getHarvestLevel() == 3 : (p_150897_1_ != Blocks.diamond_block && p_150897_1_ != Blocks.diamond_ore ? (p_150897_1_ != Blocks.emerald_ore && p_150897_1_ != Blocks.emerald_block ? (p_150897_1_ != Blocks.gold_block && p_150897_1_ != Blocks.gold_ore ? (p_150897_1_ != Blocks.iron_block && p_150897_1_ != Blocks.iron_ore ? (p_150897_1_ != Blocks.lapis_block && p_150897_1_ != Blocks.lapis_ore ? (p_150897_1_ != Blocks.redstone_ore && p_150897_1_ != Blocks.lit_redstone_ore ? (p_150897_1_.getMaterial() == Material.rock ? true : (p_150897_1_.getMaterial() == Material.iron ? true : p_150897_1_.getMaterial() == Material.anvil)) : this.toolMaterial.getHarvestLevel() >= 2) : this.toolMaterial.getHarvestLevel() >= 1) : this.toolMaterial.getHarvestLevel() >= 1) : this.toolMaterial.getHarvestLevel() >= 2) : this.toolMaterial.getHarvestLevel() >= 2) : this.toolMaterial.getHarvestLevel() >= 2);
}

@Override
public Set<String> getToolClasses(ItemStack stack)
{
    return toolClass != null ? ImmutableSet.of(toolClass) : super.getToolClasses(stack);
}

@Override
public boolean onItemUse(ItemStack p_77648_1_, EntityPlayer p_77648_2_, World p_77648_3_, int p_77648_4_, int p_77648_5_, int p_77648_6_, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_)
{
    return false;
}
@Override
public float func_150893_a(ItemStack p_150893_1_, Block p_150893_2_)
{
    return p_150893_2_.getMaterial() != Material.iron && p_150893_2_.getMaterial() != Material.anvil && p_150893_2_.getMaterial() != Material.rock ? super.func_150893_a(p_150893_1_, p_150893_2_) : this.efficiencyOnProperMaterial;
}


}
 