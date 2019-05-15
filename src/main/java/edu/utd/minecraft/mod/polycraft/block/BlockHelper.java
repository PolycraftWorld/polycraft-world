package edu.utd.minecraft.mod.polycraft.block;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.render.TileEntityBlockPipe;

public class BlockHelper {

	public static int determineOrientation(World p_150071_0_, BlockPos pos, EntityLivingBase p_150071_4_) {
		if (MathHelper.abs((float) p_150071_4_.posX - pos.getX()) < 2.0F && MathHelper.abs((float) p_150071_4_.posZ - pos.getZ()) < 2.0F) {
			double d0 = p_150071_4_.posY + 1.82D - p_150071_4_.getYOffset();
			if (d0 - pos.getY() > 2.0D)
				return 1;
			if (pos.getY() - d0 > 0.0D)
				return 0;
		}

		int l = MathHelper.floor_double(p_150071_4_.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		return l == 0 ? 2 : (l == 1 ? 5 : (l == 2 ? 3 : (l == 3 ? 4 : 0)));
	}

	public static EnumFacing setFacingMetadata6(Block thisBlock, World p_149689_1_, BlockPos pos, EntityLivingBase p_149689_5_, ItemStack p_149689_6_) {
		EnumFacing l = EnumFacing.getFront(determineOrientation(p_149689_1_, pos, p_149689_5_));
		p_149689_1_.setBlockState(pos, thisBlock.getStateFromMeta(l.getIndex()), 2);
		return l;
	}

	public static EnumFacing setFacingMetadata4(Block thisBlock, World p_149689_1_, BlockPos pos, EntityLivingBase p_149689_5_, ItemStack p_149689_6_) {
		EnumFacing l = EnumFacing.getFront(determineOrientation(p_149689_1_, pos, p_149689_5_));
		if (l == EnumFacing.DOWN || l == EnumFacing.UP)
			l = EnumFacing.NORTH;
		p_149689_1_.setBlockState(pos, thisBlock.getStateFromMeta(l.getIndex()), 2);
		return l;
	}

	public static int setFacingFlippableMetadata4(Block thisBlock, World worldObj, BlockPos pos, EntityLivingBase player, ItemStack itemStack, boolean shiftPressed) {
		int l = determineOrientation(worldObj, pos, player);
		if (l == EnumFacing.DOWN.ordinal() || l == EnumFacing.UP.ordinal())
			l = EnumFacing.NORTH.ordinal();
		if (shiftPressed)
			l |= 1 << 3;
		worldObj.setBlockState(pos, thisBlock.getStateFromMeta(l), 2);

		return l;
	}

	public static int setKnownFacingMetadata4(Block thisBlock, World worldObj, BlockPos pos, EnumFacing dir, boolean shiftPressed) {
		int l = dir.getOpposite().ordinal();
		if (shiftPressed)
			l |= 1 << 3;
		worldObj.setBlockState(pos, thisBlock.getStateFromMeta(l), 2);

		return l;
	}

	public static EnumFacing setFacingMetadataFlowable(Block thisBlock, World worldObj, BlockPos pos, EntityLivingBase player, ItemStack itemStack) {
		EnumFacing direction = setFacingMetadata6(thisBlock, worldObj, pos, player, itemStack);
		if (worldObj.getBlockState(pos).getBlock() instanceof BlockPipe)
			TileEntityBlockPipe.setDirectionIn(worldObj, pos);

		//update the neighbor direction in if it needs it
		//final Vec3 coords = PolycraftMod.getAdjacentCoords(pos), direction, false);
		if (worldObj.getBlockState(pos.offset(direction)) instanceof BlockPipe)
			TileEntityBlockPipe.setDirectionIn(worldObj, pos);

		return direction;
	}
}
