package edu.utd.minecraft.mod.polycraft.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.experiment.old.ExperimentCTB;
import edu.utd.minecraft.mod.polycraft.experiment.old.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.experiment.old.ExperimentOld;
import edu.utd.minecraft.mod.polycraft.worldgen.ChallengeHouseDim;
import edu.utd.minecraft.mod.polycraft.worldgen.ChallengeTeleporter;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class BlockPolyPortal extends BlockBreakable {
	public final CustomObject config;
	private ArrayList<EntityPlayerMP> playersInteractedWith = new ArrayList<EntityPlayerMP>();
	private int interactTickCooldown = 0;
	
	public BlockPolyPortal(CustomObject config) {
		super( Material.portal, false);
		this.setCreativeTab(CreativeTabs.tabTools); //TODO: Take this out of CreativeTab and Make Command to access.
		this.config=config;
		this.setLightLevel(1.0F);
		this.setTickRandomly(true);
	}

	//@Override
	//public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		
	//	return new TileEntityEndPortal();
	//}
	

    public static int func_149999_b(int p_149999_0_)
    {
        return p_149999_0_ & 3;
    }
	 /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(IBlockAccess p_149719_1_, BlockPos blockPos)
    {
    	//TODO: update to 1.8
        /*
        int l = func_149999_b(p_149719_1_.getBlockMetadata(p_149719_2_, p_149719_3_, p_149719_4_));

        if (l == 0)
        {
            if (p_149719_1_.getBlock(p_149719_2_ - 1, p_149719_3_, p_149719_4_) != this && p_149719_1_.getBlock(p_149719_2_ + 1, p_149719_3_, p_149719_4_) != this)
            {
                l = 2;
            }
            else
            {
                l = 1;
            }

            if (p_149719_1_ instanceof World && !((World)p_149719_1_).isRemote)
            {
                ((World)p_149719_1_).setBlockMetadataWithNotify(p_149719_2_, p_149719_3_, p_149719_4_, l, 2);
            }
        }

        float f = 0.125F;
        float f1 = 0.125F;

        if (l == 1)
        {
            f = 0.5F;
        }

        if (l == 2)
        {
            f1 = 0.5F;
        }

        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f1, 0.5F + f, 1.0F, 0.5F + f1);*/
    }

    /**
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates.  Args: blockAccess, x, y, z, side
     */
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess p_149646_1_, BlockPos blockPos, EnumFacing facing)
    {
    	return super.shouldSideBeRendered(p_149646_1_, blockPos, facing);
    	
//        int i1 = 0;
//
//        if (p_149646_1_.getBlock(p_149646_2_, p_149646_3_, p_149646_4_) == this)
//        {
//            i1 = func_149999_b(p_149646_1_.getBlockMetadata(p_149646_2_, p_149646_3_, p_149646_4_));
//
//            if (i1 == 0)
//            {
//                return false;
//            }
//
//            if (i1 == 2 && p_149646_5_ != 5 && p_149646_5_ != 4)
//            {
//                return false;
//            }
//
//            if (i1 == 1 && p_149646_5_ != 3 && p_149646_5_ != 2)
//            {
//                return false;
//            }
//        }
//
//        boolean flag = p_149646_1_.getBlock(p_149646_2_ - 1, p_149646_3_, p_149646_4_) == this && p_149646_1_.getBlock(p_149646_2_ - 2, p_149646_3_, p_149646_4_) != this;
//        boolean flag1 = p_149646_1_.getBlock(p_149646_2_ + 1, p_149646_3_, p_149646_4_) == this && p_149646_1_.getBlock(p_149646_2_ + 2, p_149646_3_, p_149646_4_) != this;
//        boolean flag2 = p_149646_1_.getBlock(p_149646_2_, p_149646_3_, p_149646_4_ - 1) == this && p_149646_1_.getBlock(p_149646_2_, p_149646_3_, p_149646_4_ - 2) != this;
//        boolean flag3 = p_149646_1_.getBlock(p_149646_2_, p_149646_3_, p_149646_4_ + 1) == this && p_149646_1_.getBlock(p_149646_2_, p_149646_3_, p_149646_4_ + 2) != this;
//        boolean flag4 = flag || flag1 || i1 == 1;
//        boolean flag5 = flag2 || flag3 || i1 == 2;
//        return flag4 && p_149646_5_ == 4 ? true : (flag4 && p_149646_5_ == 5 ? true : (flag5 && p_149646_5_ == 2 ? true : flag5 && p_149646_5_ == 3));
//    
       // return p_149646_5_ != 0 ? false : super.shouldSideBeRendered(p_149646_1_, p_149646_2_, p_149646_3_, p_149646_4_, p_149646_5_);
    }

    /**
     * Adds all intersecting collision boxes to a list. (Be sure to only add boxes to the list if they intersect the
     * mask.) Parameters: World, X, Y, Z, mask, list, colliding entity
     */
    public void addCollisionBoxesToList(World p_149743_1_, int p_149743_2_, int p_149743_3_, int p_149743_4_, AxisAlignedBB p_149743_5_, List p_149743_6_, Entity p_149743_7_) {}

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass()
    {
        return 1;
    }
    
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
	    return null;
    }
   
    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random p_149745_1_)
    {
        return 0;
    }

    /**
     * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
     * This means the player wants to teleport! If the player is on dimension==0, they will be teleported to the spectator zone associated
     * with the block's experiment ID. In the future, the player may have a GUI that passes this desired experiment
     * id to the server; however, for now, this is hard-coded. 
     */
    public void onEntityCollidedWithBlock(World world, int xpos, int ypos, int zpos, Entity possiblePlayer)
    {    
    	if (possiblePlayer instanceof EntityPlayerMP && possiblePlayer.ridingEntity == null && possiblePlayer.riddenByEntity == null && !world.isRemote )
        {
            WorldServer worldserver = (WorldServer) ((EntityPlayerMP)possiblePlayer).getEntityWorld();
			EntityPlayerMP playerMP = (EntityPlayerMP) possiblePlayer;
			if(!playersInteractedWith.contains(playerMP)) {
				try {
					if(playerMP.dimension==8){
						playerMP.mcServer.getConfigurationManager().transferPlayerToDimension(playerMP, 0,	new PolycraftTeleporter(playerMP.mcServer.worldServerForDimension(0)));			
					} else if(playerMP.dimension==0) {
						int numChunks = 8;
						int nextID = ExperimentManager.INSTANCE.getNextID();
						
						int currentID = nextID - 1;
						if(nextID == 1) {
							//ExperimentManager.INSTANCE.registerExperiment(nextID, new ExperimentCTB(nextID, numChunks, nextID*16*numChunks + 16, nextID*16*numChunks + 144,DimensionManager.getWorld(8)));
							playerMP.addChatMessage(new ChatComponentText("Created a new Experiment: " + nextID));
						}
						
						try {
							switch(ExperimentManager.INSTANCE.getExperimentStatus(currentID)) {
							case PreInit:
								break;
							case Initializing:
								break;
							case WaitingToStart:
								ExperimentManager.INSTANCE.addPlayerToExperiment(currentID, playerMP);
								break;
							case GeneratingArea:
								playerMP.addChatMessage(new ChatComponentText("Expeirment is currently Generating"));
								break;
							case Running:
								break;
							case Done:
								System.out.println("currentID is actually Done!");
								//ExperimentManager.INSTANCE.registerExperiment(nextID, new ExperimentCTB(nextID, numChunks, nextID*16*numChunks + 16, nextID*16*numChunks + 144,DimensionManager.getWorld(8)));
								System.out.println("Created a new Experiment: " + nextID);
								break;
							default:
								playerMP.addChatComponentMessage(new ChatComponentText("Error! Stephen & Dhruv messed up." + ExperimentManager.INSTANCE.getExperimentStatus(currentID) + "::" + currentID));
								break;
							}
							//if(ExperimentManager.INSTANCE.getExperimentStatus(currentID).equals(Experiment.State.WaitingToStart){
								
							//}
						}catch(NullPointerException npe) {
							System.out.println("ERROR:");
							npe.printStackTrace();
						}
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
				playersInteractedWith.add(playerMP);
			}else {
				interactTickCooldown++;
				if(interactTickCooldown > 60) {
					playersInteractedWith.clear();
					interactTickCooldown = 0;
				}
			}
			
        }
    }

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, BlockPos blockPos, IBlockState state, Random p_149734_5_)
    {

        if (p_149734_5_.nextInt(100) == 0)
        {
            world.playSound(blockPos.getX() + 0.5D, blockPos.getY() + 0.5D, blockPos.getZ() + 0.5D, "portal.portal", 0.5F, p_149734_5_.nextFloat() * 0.4F + 0.8F, false);
        }

        for (int l = 0; l < 4; ++l)
        {
            double d0 = (double)((float)blockPos.getX() + p_149734_5_.nextFloat());
            double d1 = (double)((float)blockPos.getY() + p_149734_5_.nextFloat());
            double d2 = (double)((float)blockPos.getZ() + p_149734_5_.nextFloat());
            double d3 = 0.0D;
            double d4 = 0.0D;
            double d5 = 0.0D;
            int i1 = p_149734_5_.nextInt(2) * 2 - 1;
            d3 = ((double)p_149734_5_.nextFloat() - 0.5D) * 0.5D;
            d4 = ((double)p_149734_5_.nextFloat() - 0.5D) * 0.5D;
            d5 = ((double)p_149734_5_.nextFloat() - 0.5D) * 0.5D;

            if (world.getBlockState(blockPos.add(-1, 0, 0)).getBlock() != this && world.getBlockState(blockPos.add(1,0,0)).getBlock() != this)
            {
                d0 = (double)blockPos.getX() + 0.5D + 0.25D * (double)i1;
                d3 = (double)(p_149734_5_.nextFloat() * 2.0F * (float)i1);
            }
            else
            {
                d2 = (double)blockPos.getZ() + 0.5D + 0.25D * (double)i1;
                d5 = (double)(p_149734_5_.nextFloat() * 2.0F * (float)i1);
            }

            world.spawnParticle(EnumParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
        }
    }

    /**
     * The type of render function that is called for this block
     */
//    public int getRenderType()
//    {
//        return -1;
//    }
	
    
	
    @SideOnly(Side.CLIENT)
    public Item getItem(World world, int p_149694_2_, int p_149694_3_, int p_149694_4_)
    {
        return Item.getItemById(0);
    }
    
//    @Override
//	@SideOnly(Side.CLIENT)
//	public IIcon getIcon(int side, int colorIndex) {
//		return this.blockIcon;
//	}
//
//    @SideOnly(Side.CLIENT)
//    public void registerBlockIcons(IIconRegister p_149651_1_)
//    {
//    	this.blockIcon = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName("Poly_Portal")));
//
//
//    }

    public MapColor getMapColor(int p_149728_1_)
    {
        return MapColor.obsidianColor;
    }

}
