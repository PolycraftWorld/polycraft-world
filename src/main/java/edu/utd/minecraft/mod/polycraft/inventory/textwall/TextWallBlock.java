package edu.utd.minecraft.mod.polycraft.inventory.textwall;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class TextWallBlock extends PolycraftInventoryBlock {
	
	public TextWallBlock(Inventory config, Class tileEntityClass) {
		super(config, tileEntityClass, Material.iron, 7.5F);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		
		// TODO Auto-generated constructor stub
	}
	
//	@Override
//	public TileEntity createNewTileEntity(World world, int var1) {
//		return (TileEntity) new TextWallBlock(config, tileEntityClass);
//	}
	



}
