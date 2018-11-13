package edu.utd.minecraft.mod.polycraft.inventory.textwall;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.IIcon;

public class TextWallBlock extends PolycraftInventoryBlock<TextWallInventory> {

	@SideOnly(Side.CLIENT)
	public IIcon iconOutside;
	@SideOnly(Side.CLIENT)
	public IIcon iconTop;
	@SideOnly(Side.CLIENT)
	public IIcon iconInside;
	
	
	public TextWallBlock(Inventory config, Class tileEntityClass) {
		super(config, tileEntityClass);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		// TODO Auto-generated constructor stub
	}
	



}
