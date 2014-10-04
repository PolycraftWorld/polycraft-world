package edu.utd.minecraft.mod.polycraft.block;

import java.util.EnumSet;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Ore;
import edu.utd.minecraft.mod.polycraft.inventory.BlockFace;

//TODO replace getIcon and registerBlockIcons with custom rendering
public class BlockPipe extends Block {

	private final Map<BlockFace, IIcon> blockFaceIcons = Maps.newHashMap();
	
	public BlockPipe() {
		super(Material.iron);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setStepSound(Block.soundTypeMetal);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int front) {
		// If front face is explicitly defined, display that.
		if (side == front && blockFaceIcons.containsKey(BlockFace.FRONT)) {
			return blockFaceIcons.get(BlockFace.FRONT);
		}

		// If the block's side is explicitly defined, show that icon.
		BlockFace face = BlockFace.getBlockFace(side);
		if (blockFaceIcons.containsKey(face)) {
			return blockFaceIcons.get(face);
		}

		// Otherwise show the default icon.
		return blockFaceIcons.get(BlockFace.DEFAULT);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		for (BlockFace face : EnumSet.allOf(BlockFace.class))
			blockFaceIcons.put(face, register.registerIcon(PolycraftMod.getAssetName((face == BlockFace.FRONT) ? "spotlight_front" : "TODO")));
	}

	@Override
	public void onBlockPlacedBy(World p_149689_1_, int p_149689_2_, int p_149689_3_, int p_149689_4_, EntityLivingBase p_149689_5_, ItemStack p_149689_6_) {
		BlockHelper.setFacingMetadata6(this, p_149689_1_, p_149689_2_, p_149689_3_, p_149689_4_, p_149689_5_, p_149689_6_);
	}
}