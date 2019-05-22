package edu.utd.minecraft.mod.polycraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;

import java.util.Random;

import com.google.common.base.Preconditions;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPolycraftDoor extends Block
{
//    @SideOnly(Side.CLIENT)
//    private IIcon[] field_150017_a;
//    @SideOnly(Side.CLIENT)
//    private IIcon[] field_150016_b;
    private static final String __OBFID = "CL_00000230";
    
	public final CustomObject config;

    protected BlockPolycraftDoor(CustomObject config, Material p_i45402_1_)
    {
        super(p_i45402_1_);
        float f = 0.5F;
        float f1 = 1.0F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
        
        Preconditions.checkNotNull(config);
		this.setCreativeTab(CreativeTabs.tabMaterials);
		//if (config.maxStackSize > 0)
		//	this.setMaxStackSize(config.maxStackSize);
		this.config = config;
		// TODO Auto-generated constructor stub
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
//    @SideOnly(Side.CLIENT)
//    public IIcon getIcon(int p_149691_1_, int p_149691_2_)
//    {
//        return this.field_150016_b[0];
//    }
//
//    @SideOnly(Side.CLIENT)
//    public IIcon getIcon(IBlockAccess p_149673_1_, int p_149673_2_, int p_149673_3_, int p_149673_4_, int p_149673_5_)
//    {
//        if (p_149673_5_ != 1 && p_149673_5_ != 0)
//        {
//            int i1 = this.func_150012_g(p_149673_1_, p_149673_2_, p_149673_3_, p_149673_4_);
//            int j1 = i1 & 3;
//            boolean flag = (i1 & 4) != 0;
//            boolean flag1 = false;
//            boolean flag2 = (i1 & 8) != 0;
//
//            if (flag)
//            {
//                if (j1 == 0 && p_149673_5_ == 2)
//                {
//                    flag1 = !flag1;
//                }
//                else if (j1 == 1 && p_149673_5_ == 5)
//                {
//                    flag1 = !flag1;
//                }
//                else if (j1 == 2 && p_149673_5_ == 3)
//                {
//                    flag1 = !flag1;
//                }
//                else if (j1 == 3 && p_149673_5_ == 4)
//                {
//                    flag1 = !flag1;
//                }
//            }
//            else
//            {
//                if (j1 == 0 && p_149673_5_ == 5)
//                {
//                    flag1 = !flag1;
//                }
//                else if (j1 == 1 && p_149673_5_ == 3)
//                {
//                    flag1 = !flag1;
//                }
//                else if (j1 == 2 && p_149673_5_ == 4)
//                {
//                    flag1 = !flag1;
//                }
//                else if (j1 == 3 && p_149673_5_ == 2)
//                {
//                    flag1 = !flag1;
//                }
//
//                if ((i1 & 16) != 0)
//                {
//                    flag1 = !flag1;
//                }
//            }
//
//            return flag2 ? this.field_150017_a[flag1?1:0] : this.field_150016_b[flag1?1:0];
//        }
//        else
//        {
//            return this.field_150016_b[0];
//        }
//    }
//
//    @SideOnly(Side.CLIENT)
//    public void registerBlockIcons(IIconRegister p_149651_1_)
//    {
//        this.field_150017_a = new IIcon[2];
//        this.field_150016_b = new IIcon[2];
//        this.field_150017_a[0] = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name + "_upper")));
//        this.field_150016_b[0] = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name + "_lower")));
//        this.field_150017_a[1] = new IconFlipped(this.field_150017_a[0], true, false);
//        this.field_150016_b[1] = new IconFlipped(this.field_150016_b[0], true, false);
//    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean getBlocksMovement(IBlockAccess p_149655_1_, BlockPos blockPos)
    {
        int l = this.func_150012_g(p_149655_1_, blockPos);
        return (l & 4) != 0;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 7;
    }

    /**
     * Returns the bounding box of the wired rectangular prism to render.
     */
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World p_149633_1_, BlockPos blockPos)
    {
        this.setBlockBoundsBasedOnState(p_149633_1_, blockPos);
        return super.getSelectedBoundingBox(p_149633_1_, blockPos);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, BlockPos blockPos)
    {
        this.setBlockBoundsBasedOnState(world, blockPos);
        return super.getCollisionBoundingBox(world, blockPos, world.getBlockState(blockPos));
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(IBlockAccess p_149719_1_, BlockPos blockPos)
    {
        this.func_150011_b(this.func_150012_g(p_149719_1_, blockPos));
    }

    public int func_150013_e(IBlockAccess p_150013_1_, BlockPos blockPos)
    {
        return this.func_150012_g(p_150013_1_, blockPos) & 3;
    }

    public boolean func_150015_f(IBlockAccess p_150015_1_, BlockPos blockPos)
    {
        return (this.func_150012_g(p_150015_1_, blockPos) & 4) != 0;
    }

    private void func_150011_b(int p_150011_1_)
    {
        float f = 0.1875F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
        int j = p_150011_1_ & 3;
        boolean flag = (p_150011_1_ & 4) != 0;
        boolean flag1 = (p_150011_1_ & 16) != 0;

        if (j == 0)
        {
            if (flag)
            {
                if (!flag1)
                {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
                }
                else
                {
                    this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
                }
            }
            else
            {
                this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
            }
        }
        else if (j == 1)
        {
            if (flag)
            {
                if (!flag1)
                {
                    this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                }
                else
                {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
                }
            }
            else
            {
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
            }
        }
        else if (j == 2)
        {
            if (flag)
            {
                if (!flag1)
                {
                    this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
                }
                else
                {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
                }
            }
            else
            {
                this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }
        }
        else if (j == 3)
        {
            if (flag)
            {
                if (!flag1)
                {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
                }
                else
                {
                    this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                }
            }
            else
            {
                this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World p_149727_1_, BlockPos blockPos, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
        if (this.blockMaterial == Material.iron)
        {
            return false; //Allow items to interact with the door
        }
        else
        {
            int i1 = this.func_150012_g(p_149727_1_, blockPos);
            int j1 = i1 & 7;
            j1 ^= 4;

            if ((i1 & 8) == 0)
            {
                p_149727_1_.setBlockState(blockPos, this.getStateFromMeta(j1), 2);
                p_149727_1_.markBlockRangeForRenderUpdate(blockPos, blockPos);
            }
            else
            {
                p_149727_1_.setBlockState(blockPos.down(), this.getStateFromMeta(j1), 2);
                p_149727_1_.markBlockRangeForRenderUpdate(blockPos.down(), blockPos);
            }

            p_149727_1_.playAuxSFXAtEntity(p_149727_5_, 1003, blockPos, 0);
            return true;
        }
    }
    
    public boolean open(World p_149727_1_, BlockPos blockPos, EntityPlayer p_149727_5_) {
		int i1 = this.func_150012_g(p_149727_1_, blockPos);
        int j1 = i1 & 7;
        j1 ^= 4;

        if ((i1 & 8) == 0)
        {
            p_149727_1_.setBlockState(blockPos, this.getStateFromMeta(j1), 2);
            p_149727_1_.markBlockRangeForRenderUpdate(blockPos, blockPos);
        }
        else
        {
            p_149727_1_.setBlockState(blockPos.down(), this.getStateFromMeta(j1), 2);
            p_149727_1_.markBlockRangeForRenderUpdate(blockPos.down(), blockPos);
        }

        p_149727_1_.playAuxSFXAtEntity(p_149727_5_, 1003, blockPos, 0);
        return true;
	}

    
    public void func_150014_a(World p_150014_1_, BlockPos blockPos, boolean p_150014_5_)
    {
        int l = this.func_150012_g(p_150014_1_, blockPos);
        boolean flag1 = (l & 4) != 0;

        if (flag1 != p_150014_5_)
        {
            int i1 = l & 7;
            i1 ^= 4;

            if ((l & 8) == 0)
            {
                p_150014_1_.setBlockState(blockPos, this.getStateFromMeta(i1), 2);
                p_150014_1_.markBlockRangeForRenderUpdate(blockPos, blockPos);
            }
            else
            {
                p_150014_1_.setBlockState(blockPos.down(), this.getStateFromMeta(i1), 2);
                p_150014_1_.markBlockRangeForRenderUpdate(blockPos.down(), blockPos);
            }

            p_150014_1_.playAuxSFXAtEntity((EntityPlayer)null, 1003, blockPos, 0);
        }
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor Block
     */
    public void onNeighborBlockChange(World p_149695_1_, BlockPos blockPos, IBlockState state, Block p_149695_5_)
    {
        int l = this.getMetaFromState(p_149695_1_.getBlockState(blockPos));

        if ((l & 8) == 0)
        {
            boolean flag = false;

            if (p_149695_1_.getBlockState(blockPos.up()).getBlock() != this)
            {
                p_149695_1_.setBlockToAir(blockPos);
                flag = true;
            }

            if (!World.doesBlockHaveSolidTopSurface(p_149695_1_, blockPos.down()))
            {
                p_149695_1_.setBlockToAir(blockPos);
                flag = true;

                if (p_149695_1_.getBlockState(blockPos.up()) == this)
                {
                    p_149695_1_.setBlockToAir(blockPos.up());
                }
            }

            if (flag)
            {
                if (!p_149695_1_.isRemote)
                {
                    this.dropBlockAsItem(p_149695_1_, blockPos, state, 0);
                }
            }
            else
            {
                boolean flag1 = p_149695_1_.isBlockIndirectlyGettingPowered(blockPos) > 0 || p_149695_1_.isBlockIndirectlyGettingPowered(blockPos.up()) > 0;

                if ((flag1 || p_149695_5_.canProvidePower()) && p_149695_5_ != this)
                {
                    this.func_150014_a(p_149695_1_, blockPos, flag1);
                }
            }
        }
        else
        {
            if (p_149695_1_.getBlockState(blockPos.down()).getBlock() != this)
            {
                p_149695_1_.setBlockToAir(blockPos);
            }

            if (p_149695_5_ != this)
            {
                this.onNeighborBlockChange(p_149695_1_, blockPos.down(), state, p_149695_5_);
            }
        }
    }

    public Item getItemDropped(IBlockState state, Random random, int forune)
    {
        return (this.getMetaFromState(state) & 8) != 0 ? null : (this.blockMaterial == Material.iron ? Items.iron_door : Items.oak_door);
    }

    /**
     * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit. Args: world,
     * x, y, z, startVec, endVec
     */
    public MovingObjectPosition collisionRayTrace(World p_149731_1_, BlockPos blockPos, Vec3 p_149731_5_, Vec3 p_149731_6_)
    {
        this.setBlockBoundsBasedOnState(p_149731_1_, blockPos);
        return super.collisionRayTrace(p_149731_1_, blockPos, p_149731_5_, p_149731_6_);
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(World p_149742_1_, BlockPos blockPos)
    {
        return blockPos.getY() >= p_149742_1_.getHeight() - 1 ? false : World.doesBlockHaveSolidTopSurface(p_149742_1_, blockPos.down()) && super.canPlaceBlockAt(p_149742_1_, blockPos) && super.canPlaceBlockAt(p_149742_1_, blockPos.up());
    }

    /**
     * Returns the mobility information of the block, 0 = free, 1 = can't push but can move over, 2 = total immobility
     * and stop pistons
     */
    public int getMobilityFlag()
    {
        return 1;
    }

    public int func_150012_g(IBlockAccess p_150012_1_, BlockPos blockPos)
    {
        //TODO: fix for 1.8
        /*
        int l = p_150012_1_.getBlockMetadata(blockPos);
        boolean flag = (l & 8) != 0;
        int i1;
        int j1;

        if (flag)
        {
            i1 = p_150012_1_.getBlockMetadata(p_150012_2_, p_150012_3_ - 1, p_150012_4_);
            j1 = l;
        }
        else
        {
            i1 = l;
            j1 = p_150012_1_.getBlockMetadata(p_150012_2_, p_150012_3_ + 1, p_150012_4_);
        }

        boolean flag1 = (j1 & 1) != 0;
        return i1 & 7 | (flag ? 8 : 0) | (flag1 ? 16 : 0);*/
        return 0;
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
    {
        return PolycraftRegistry.getItem(config.name);
    }

    /**
     * Called when the block is attempted to be harvested
     */
    public void onBlockHarvested(World p_149681_1_, BlockPos blockPos, IBlockState state, EntityPlayer p_149681_6_)
    {
        if (p_149681_6_.capabilities.isCreativeMode && (this.getMetaFromState(state) & 8) != 0 && p_149681_1_.getBlockState(blockPos.down()).getBlock() == this)
        {
            p_149681_1_.setBlockToAir(blockPos.down());
        }
    }
}