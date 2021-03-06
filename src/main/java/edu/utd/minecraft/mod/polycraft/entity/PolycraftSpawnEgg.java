package edu.utd.minecraft.mod.polycraft.entity;

import java.util.List;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class PolycraftSpawnEgg extends ItemMonsterPlacer
{
  //  @SideOnly(Side.CLIENT)
//    private IIcon theIcon;
    protected int colorBase = 0x000000;
    protected int colorSpots = 0xFFFFFF;
    protected String entityToSpawnName = "";
    protected String entityToSpawnNameFull = "";
    protected EntityLiving entityToSpawn = null;

    public PolycraftSpawnEgg()
    {
        super();
    }
    
    public PolycraftSpawnEgg(String parEntityToSpawnName, int parPrimaryColor, 
          int parSecondaryColor)
    {
        setHasSubtypes(false);
        maxStackSize = 64;
        setCreativeTab(CreativeTabs.tabMisc);
        setEntityToSpawnName(parEntityToSpawnName);
        colorBase = parPrimaryColor;
        colorSpots = parSecondaryColor;
        // DEBUG
        //System.out.println("Spawn egg constructor for "+entityToSpawnName);
    }
    /**
     * Callback for item usage. If the item does something special on right clicking, 
     * he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer,
                             World par3World, BlockPos blockPos, EnumFacing facing, float par8,
                             float par9, float par10)
    {
        if (par3World.isRemote)
        {
            return true;
        }
        else
        {
            Block block = par3World.getBlockState(blockPos).getBlock();
            blockPos = blockPos.add(facing.getFrontOffsetX(),
                                    facing.getFrontOffsetY(),
                                    facing.getFrontOffsetZ());
            double d0 = 0.0D;

            if (facing.getIndex() == 1 && block.getRenderType() == 11)
            {
                d0 = 0.5D;
            }

            Entity entity = spawnEntity(par3World, blockPos.getX() + 0.5D, blockPos.getY() + d0, blockPos.getZ() + 0.5D);

            if (entity != null)
            {
                if (entity instanceof EntityLivingBase && par1ItemStack.hasDisplayName())
                {
                    ((EntityLiving)entity).setCustomNameTag(par1ItemStack.getDisplayName());
                }

                if (!par2EntityPlayer.capabilities.isCreativeMode)
                {
                    --par1ItemStack.stackSize;
                }
            }

            return true;
        }
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. 
     *Args: itemStack, world, entityPlayer
     */
    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, 
          EntityPlayer par3EntityPlayer)
    {
        if (par2World.isRemote)
        {
            return par1ItemStack;
        }
        else
        {
            MovingObjectPosition movingobjectposition = 
                  getMovingObjectPositionFromPlayer(par2World, par3EntityPlayer, true);

            if (movingobjectposition == null)
            {
                return par1ItemStack;
            }
            else
            {
                if (movingobjectposition.typeOfHit == MovingObjectPosition
                      .MovingObjectType.BLOCK)
                {
                    if (!par2World.canMineBlockBody(par3EntityPlayer, movingobjectposition.getBlockPos()))
                    {
                        return par1ItemStack;
                    }

                    if (!par3EntityPlayer.canPlayerEdit(movingobjectposition.getBlockPos(), movingobjectposition
                          .sideHit, par1ItemStack))
                    {
                        return par1ItemStack;
                    }

                    if (par2World.getBlockState(movingobjectposition.getBlockPos()).getBlock() instanceof BlockLiquid)
                    {
                        Entity entity = spawnEntity(par2World, movingobjectposition.getBlockPos().getX(),
                                movingobjectposition.getBlockPos().getY(),
                                movingobjectposition.getBlockPos().getZ());

                        if (entity != null)
                        {
                            if (entity instanceof EntityLivingBase && par1ItemStack
                                  .hasDisplayName())
                            {
                                ((EntityLiving)entity).setCustomNameTag(par1ItemStack
                                      .getDisplayName());
                            }

                            if (!par3EntityPlayer.capabilities.isCreativeMode)
                            {
                                --par1ItemStack.stackSize;
                            }
                        }
                    }
                }

                return par1ItemStack;
            }
        }
    }

    /**
     * Spawns the creature specified by the egg's type in the location specified by 
     * the last three parameters.
     * Parameters: world, entityID, x, y, z.
     */
    public Entity spawnEntity(World parWorld, double parX, double parY, double parZ)
    {
     
       if (!parWorld.isRemote) // never spawn entity on client side
       {
            entityToSpawnNameFull = PolycraftMod.MODID+"."+entityToSpawnName;
            if (EntityList.stringToClassMapping.containsKey(entityToSpawnNameFull))
            {
                entityToSpawn = (EntityLiving) EntityList
                      .createEntityByName(entityToSpawnNameFull, parWorld);
                entityToSpawn.setLocationAndAngles(parX, parY, parZ, 
                      MathHelper.wrapAngleTo180_float(parWorld.rand.nextFloat()
                      * 360.0F), 0.0F);
                parWorld.spawnEntityInWorld(entityToSpawn);
                entityToSpawn.onInitialSpawn(null, (IEntityLivingData)null);
                entityToSpawn.playLivingSound();
            }
            else
            {
                //DEBUG
                System.out.println("Entity not found "+entityToSpawnName);
            }
        }
      
        return entityToSpawn;
    }


    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item parItem, CreativeTabs parTab, List parList)
    {
        parList.add(new ItemStack(parItem, 1, 0));     
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack par1ItemStack, int parColorType)
    {
        return (parColorType == 0) ? colorBase : colorSpots;
    }
/*  TODO: maybe we don't need this in 1.8?
    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }*/
    
    @Override
    // Doing this override means that there is no localization for language
    // unless you specifically check for localization here and convert
    public String getItemStackDisplayName(ItemStack par1ItemStack)
    {
        return "Spawn "+entityToSpawnName;
    }  


//    @Override
//    @SideOnly(Side.CLIENT)
//    public void registerIcons(IIconRegister par1IconRegister)
//    {
//        super.registerIcons(par1IconRegister);
//        this.itemIcon = par1IconRegister.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName("polyspawn_egg")));
//        theIcon = par1IconRegister.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName("polyspawn_egg_overlay")));
//        //theIcon = par1IconRegister.registerIcon(getIconString() + "_overlay");
//    }
//
//    /**
//     * Gets an icon index based on an item's damage value and the given render pass
//     */
//    @Override
//    @SideOnly(Side.CLIENT)
//    public IIcon getIconFromDamageForRenderPass(int parDamageVal, int parRenderPass)
//    {
//        return parRenderPass > 0 ? theIcon : super.getIconFromDamageForRenderPass(parDamageVal,
//              parRenderPass);
//    }
    
    public void setColors(int parColorBase, int parColorSpots)
    {
     colorBase = parColorBase;
     colorSpots = parColorSpots;
    }
    
    public int getColorBase()
    {
     return colorBase;
    }
    
    public int getColorSpots()
    {
     return colorSpots;
    }
    
    public void setEntityToSpawnName(String parEntityToSpawnName)
    {
        entityToSpawnName = parEntityToSpawnName;
        entityToSpawnNameFull = PolycraftMod.MODID+"."+entityToSpawnName; 
    }

}
