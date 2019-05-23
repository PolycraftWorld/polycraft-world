package edu.utd.minecraft.mod.polycraft.inventory.textwall;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TextWallBlock extends PolycraftInventoryBlock {
	
	public TextWallBlock(Inventory config, Class tileEntityClass) {
		super(config, tileEntityClass, Material.iron, 7.5F);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos blockPos, IBlockState state,
									EntityPlayer player, EnumFacing facing, float p_149727_7_, float p_149727_8_, float p_149727_9_)
	{
		if(world.isRemote) {
			//open the consent GUI on right-click.
			PolycraftMod.proxy.openConsentGui(player, blockPos.getX(), blockPos.getY(), blockPos.getZ());
			
		}
		return false;
	}

	


}
