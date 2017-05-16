package edu.utd.minecraft.mod.polycraft.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.render.EntityDropParticleFX;

public class BlockFluid extends BlockFluidClassic {

	protected float particleRed;
	protected float particleGreen;
	protected float particleBlue;

	public BlockFluid(final Fluid fluid, final Material material) {
		super(fluid, material);
	}

	@SideOnly(Side.CLIENT)
	protected IIcon[] theIcon;
	protected boolean flammable;
	protected int flammability = 0;

	@Override
	public IIcon getIcon(int side, int meta) {
		return side != 0 && side != 1 ? this.theIcon[1] : this.theIcon[0];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		this.theIcon = new IIcon[] { iconRegister.registerIcon(PolycraftMod.getAssetName(fluidName + "_still")), iconRegister.registerIcon(PolycraftMod.getAssetName(fluidName + "_flow")) };
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		super.onNeighborBlockChange(world, x, y, z, block);
		if (flammable && world.provider.dimensionId == -1) {
			world.newExplosion(null, x, y, z, 4F, true, true);
			world.setBlockToAir(x, y, z);
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
	public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return flammable ? 300 : 0;
	}

	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return flammability;
	}

	@Override
	public boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return flammable;
	}

	@Override
	public boolean isFireSource(World world, int x, int y, int z, ForgeDirection side) {
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
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		super.randomDisplayTick(world, x, y, z, rand);

		if (rand.nextInt(10) == 0 && World.doesBlockHaveSolidTopSurface(world, x, y - 1, z) && !world.getBlock(x, y - 2, z).getMaterial().blocksMovement()) {

			double px = x + rand.nextFloat();
			double py = y - 1.05D;
			double pz = z + rand.nextFloat();

			EntityFX fx = new EntityDropParticleFX(world, px, py, pz, particleRed, particleGreen, particleBlue);
			FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public boolean canDisplace(IBlockAccess world, int x, int y, int z) {
		if (world.getBlock(x, y, z).getMaterial().isLiquid())
			return false;
		return super.canDisplace(world, x, y, z);
	}

	@Override
	public boolean displaceIfPossible(World world, int x, int y, int z) {
		if (world.getBlock(x, y, z).getMaterial().isLiquid())
			return false;
		return super.displaceIfPossible(world, x, y, z);
	}
}