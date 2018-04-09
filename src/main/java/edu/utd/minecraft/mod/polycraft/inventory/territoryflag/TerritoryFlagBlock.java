package edu.utd.minecraft.mod.polycraft.inventory.territoryflag;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.item.PolycraftItemHelper;

public class TerritoryFlagBlock extends PolycraftInventoryBlock {

	@SideOnly(Side.CLIENT)
	public IIcon iconOutside;
	@SideOnly(Side.CLIENT)
	public IIcon iconTop;
	@SideOnly(Side.CLIENT)
	public IIcon iconInside;

	public TerritoryFlagBlock(final Inventory config, final Class tileEntityClass) {
		super(config, tileEntityClass, Material.iron, 7.5F);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}
	
	@Override
	protected void dropBlockAsItem(World world, int x, int y, int z, ItemStack itemstack)
	{
		PolycraftItemHelper.createTagCompound(itemstack);
		itemstack.stackTagCompound.setInteger("government_id", 1337);
		super.dropBlockAsItem(world, x, y, z, itemstack);
	}
	
	//@Override
	public void onBlockPlacedBy(World worldObj, int xPos, int yPos, int zPos, EntityLivingBase player, ItemStack itemToPlace) {
		if(itemToPlace.stackTagCompound.hasKey("government_id")) {
			System.out.println(itemToPlace.stackTagCompound.getInteger("government_id"));
		}
		super.onBlockPlacedBy(worldObj, xPos, yPos, zPos, player, itemToPlace);
	}

}
