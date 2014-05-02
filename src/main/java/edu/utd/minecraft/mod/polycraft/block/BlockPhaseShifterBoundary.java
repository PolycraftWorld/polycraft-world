package edu.utd.minecraft.mod.polycraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class BlockPhaseShifterBoundary extends Block {

	public BlockPhaseShifterBoundary() {
		super(Material.rock);
		this.setBlockTextureName(PolycraftMod.getAssetName("phase_shifter_boundary"));
		this.setBlockUnbreakable();
	}

	@Override
	public int getLightValue() {
		return 15;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {

		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_) {
		return false;
	}
}
