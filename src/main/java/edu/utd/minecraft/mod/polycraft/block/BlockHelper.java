package edu.utd.minecraft.mod.polycraft.block;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.render.TileEntityBlockPipe;

public class BlockHelper {

	public static int determineOrientation(World p_150071_0_, int p_150071_1_, int p_150071_2_, int p_150071_3_, EntityLivingBase p_150071_4_) {
		if (MathHelper.abs((float) p_150071_4_.posX - p_150071_1_) < 2.0F && MathHelper.abs((float) p_150071_4_.posZ - p_150071_3_) < 2.0F) {
			double d0 = p_150071_4_.posY + 1.82D - p_150071_4_.yOffset;
			if (d0 - p_150071_2_ > 2.0D)
				return 1;
			if (p_150071_2_ - d0 > 0.0D)
				return 0;
		}

		int l = MathHelper.floor_double(p_150071_4_.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		return l == 0 ? 2 : (l == 1 ? 5 : (l == 2 ? 3 : (l == 3 ? 4 : 0)));
	}

	public static int setFacingMetadata6(Block thisBlock, World p_149689_1_, int p_149689_2_, int p_149689_3_, int p_149689_4_, EntityLivingBase p_149689_5_, ItemStack p_149689_6_) {
		int l = determineOrientation(p_149689_1_, p_149689_2_, p_149689_3_, p_149689_4_, p_149689_5_);
		p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, l, 2);
		return l;
	}

	public static int setFacingMetadata4(Block thisBlock, World p_149689_1_, int p_149689_2_, int p_149689_3_, int p_149689_4_, EntityLivingBase p_149689_5_, ItemStack p_149689_6_) {
		int l = determineOrientation(p_149689_1_, p_149689_2_, p_149689_3_, p_149689_4_, p_149689_5_);
		if (l == ForgeDirection.DOWN.ordinal() || l == ForgeDirection.UP.ordinal())
			l = ForgeDirection.NORTH.ordinal();
		p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, l, 2);
		return l;
	}

	public static int setFacingFlippableMetadata4(Block thisBlock, World worldObj, int xCoord, int yCoord, int zCoord, EntityLivingBase player, ItemStack itemStack, boolean shiftPressed) {
		int l = determineOrientation(worldObj, xCoord, yCoord, zCoord, player);
		if (l == ForgeDirection.DOWN.ordinal() || l == ForgeDirection.UP.ordinal())
			l = ForgeDirection.NORTH.ordinal();
		if (shiftPressed)
			l |= 1 << 3;
		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, l, 2);

		return l;
	}

	public static int setKnownFacingMetadata4(Block thisBlock, World worldObj, int xCoord, int yCoord, int zCoord, ForgeDirection dir, boolean shiftPressed) {
		int l = dir.getOpposite().ordinal();
		if (shiftPressed)
			l |= 1 << 3;
		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, l, 2);

		return l;
	}

	public static int setFacingMetadataFlowable(Block thisBlock, World worldObj, int xCoord, int yCoord, int zCoord, EntityLivingBase player, ItemStack itemStack) {
		int direction = setFacingMetadata6(thisBlock, worldObj, xCoord, yCoord, zCoord, player, itemStack);
		if (worldObj.getBlock(xCoord, yCoord, zCoord) instanceof BlockPipe)
			TileEntityBlockPipe.setDirectionIn(worldObj, xCoord, yCoord, zCoord);

		//update the neighbor direction in if it needs it
		final Vec3 coords = PolycraftMod.getAdjacentCoords(Vec3.createVectorHelper(xCoord, yCoord, zCoord), direction, false);
		if (worldObj.getBlock((int) coords.xCoord, (int) coords.yCoord, (int) coords.zCoord) instanceof BlockPipe)
			TileEntityBlockPipe.setDirectionIn(worldObj, (int) coords.xCoord, (int) coords.yCoord, (int) coords.zCoord);

		return direction;
	}
}
