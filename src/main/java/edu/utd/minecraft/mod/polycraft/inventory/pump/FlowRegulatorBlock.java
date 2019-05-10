package edu.utd.minecraft.mod.polycraft.inventory.pump;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.EnumSet;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockHelper;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.inventory.BlockFace;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;

public class FlowRegulatorBlock extends PolycraftInventoryBlock<FlowRegulatorInventory> implements Flowable {
	
	@SideOnly(Side.CLIENT)
	public IIcon iconUpper;
	@SideOnly(Side.CLIENT)
	public IIcon iconLower;
	@SideOnly(Side.CLIENT)
	public IIcon iconLeft;
	@SideOnly(Side.CLIENT)
	public IIcon iconRight;
	@SideOnly(Side.CLIENT)
	public IIcon iconBack;
	@SideOnly(Side.CLIENT)
	public IIcon iconFront;
    
	public FlowRegulatorBlock(final Inventory config, final Class tileEntityClass) {
		super(config, tileEntityClass, Material.iron, 7.5F);
	}

	@Override
	public void onBlockPlacedBy(World p_149689_1_, int p_149689_2_, int p_149689_3_, int p_149689_4_, EntityLivingBase p_149689_5_, ItemStack p_149689_6_) {
		BlockHelper.setFacingMetadataFlowable(this, p_149689_1_, p_149689_2_, p_149689_3_, p_149689_4_, p_149689_5_, p_149689_6_);
	}
	

	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int p_149691_1_, int p_149691_2_)
	{
		
		switch (p_149691_1_) {
		case 0:
			return this.iconLower;
		case 1:
			return this.iconUpper;
		case 2:
			return this.iconFront;
		case 3:
			return this.iconBack;
		case 4:
			return this.iconLeft;
		case 5:
			return this.iconRight;
		}
		return this.iconFront;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister p_149651_1_)
	{
		this.iconUpper = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name + "_upper")));
		this.iconLower = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name + "_lower")));
		this.iconFront = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name + "_front")));
		this.iconBack = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name + "_back")));
		this.iconLeft = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name + "_left")));
		this.iconRight = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name + "_right")));
	}
    
}
