package edu.utd.minecraft.mod.polycraft.inventory.hospitalgenerator;

import net.minecraft.block.material.Material;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;

public class HospitalGeneratorBlock extends PolycraftInventoryBlock {

//	@SideOnly(Side.CLIENT)
//	public IIcon iconOutside;
//	@SideOnly(Side.CLIENT)
//	public IIcon iconTop;
//	@SideOnly(Side.CLIENT)
//	public IIcon iconInside;

	public HospitalGeneratorBlock(final Inventory config, final Class tileEntityClass) {
		super(config, tileEntityClass, Material.iron, 7.5F);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

}
