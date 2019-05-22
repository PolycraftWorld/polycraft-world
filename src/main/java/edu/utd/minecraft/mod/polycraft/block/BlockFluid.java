package edu.utd.minecraft.mod.polycraft.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.render.EntityDropParticleFX;

public class BlockFluid extends BlockFluidClassic {

	protected float particleRed;
	protected float particleGreen;
	protected float particleBlue;

	public BlockFluid(final Fluid fluid, final Material material) {
		super(fluid, material);
	}

//	@SideOnly(Side.CLIENT)
//	protected IIcon[] theIcon;
	protected boolean flammable;
	protected int flammability = 0;

//	@Override
//	public IIcon getIcon(int side, int meta) {
//		return side != 0 && side != 1 ? this.theIcon[1] : this.theIcon[0];
//	}
//
//	@Override
//	@SideOnly(Side.CLIENT)
//	public void registerBlockIcons(IIconRegister iconRegister) {
//		this.theIcon = new IIcon[] { iconRegister.registerIcon(PolycraftMod.getAssetName(fluidName + "_still")), iconRegister.registerIcon(PolycraftMod.getAssetName(fluidName + "_flow")) };
//	}

	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block) {
		super.onNeighborBlockChange(world, pos, state, block);
		if (flammable && world.provider.getDimensionId() == -1) {
			world.newExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 4F, true, true);
			world.setBlockToAir(pos);
		}
	}

	public BlockFluid setFlammable(boolean flammable) {
		this.flammable = flammable;
		return this;
	}

	public BlockFluid setFlammability(int flammability) {
		this.flammability = flammability;
		return this;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing facing) {
		return flammable ? 300 : 0;
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing facing) {
		return flammability;
	}

	@Override
	public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing facing) {
		return flammable;
	}

	@Override
	public boolean isFireSource(World world,  BlockPos pos, EnumFacing facing) {
		return flammable && flammability == 0;
	}

	public BlockFluid setParticleColor(float particleRed, float particleGreen, float particleBlue) {
		this.particleRed = particleRed;
		this.particleGreen = particleGreen;
		this.particleBlue = particleBlue;
		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
		super.randomDisplayTick(world, pos, state, rand);

		if (rand.nextInt(10) == 0 && World.doesBlockHaveSolidTopSurface(world, pos.down()) && !world.getBlockState(pos.down(2)).getBlock().getMaterial().blocksMovement()) {

			double px = pos.getX() + rand.nextFloat();
			double py = pos.getY() - 1.05D;
			double pz = pos.getZ() + rand.nextFloat();

			EntityFX fx = new EntityDropParticleFX(world, px, py, pz, particleRed, particleGreen, particleBlue);
			FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public boolean canDisplace(IBlockAccess world, BlockPos pos) {
		if (world.getBlockState(pos).getBlock().getMaterial().isLiquid())
			return false;
		return super.canDisplace(world, pos);
	}

	@Override
	public boolean displaceIfPossible(World world, BlockPos pos) {
		if (world.getBlockState(pos).getBlock().getMaterial().isLiquid())
			return false;
		return super.displaceIfPossible(world, pos);
	}
}