package edu.utd.minecraft.mod.polycraft.inventory.tierchest;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.inventory.BlockFace;
import edu.utd.minecraft.mod.polycraft.inventory.InventoryBehavior;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class TierChestBlock extends PolycraftInventoryBlock  {
	
	 public static final String[] field_150157_a = new String[] {"wood", "copper", "iron","gold","diamond","emerald"};
	 private static final String[] field_150156_b = new String[] {"0", "1", "2","3","4","5"};
	 @SideOnly(Side.CLIENT)
	 private IIcon[] field_150158_M;
	 @SideOnly(Side.CLIENT)
	 private IIcon[] field_150159_N;
	 private int count=0;
	 

	public TierChestBlock(Inventory config, Class tileEntityClass) {
		super(config, tileEntityClass, Material.iron, 7.5F);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		this.setCreativeTab(CreativeTabs.tabInventory);
		this.setLightLevel(10F);
		this.setTickRandomly(true);
		
	}
	
	
	@Override
	@SideOnly(Side.CLIENT)
    public int getBlockColor()
    {
        return 16777215;
    }

    /**
     * Returns the color this block should be rendered. Used by leaves.
     */
	@Override
    @SideOnly(Side.CLIENT)
    public int getRenderColor(int p_149741_1_)
    {
        return 16777215;
    }

    /**
     * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
     * when first determining what to render.
     */
	@Override
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess p_149720_1_, int p_149720_2_, int p_149720_3_, int p_149720_4_)
    {
        return 16777215;
    }
	
	
	
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float what, float these, float are)
    {
		if (!world.isRemote)
        {
			
	        if(((TierChestInventory)world.getTileEntity(x, y, z)).getActive())
	        {
	        	((TierChestInventory)world.getTileEntity(x, y, z)).setActive(false);
	        	switch(world.getBlockMetadata(x, y, z)) {
	        		case 0:
	        			this.getInventory(world, x, y, z).setInventorySlotContents(0, new ItemStack(Item.getItemById(2)));
	        			break;
	        		case 1:
	        			this.getInventory(world, x, y, z).setInventorySlotContents(0, new ItemStack(Item.getItemById(3)));
	        			break;
	        		case 2:
	        			this.getInventory(world, x, y, z).setInventorySlotContents(0, new ItemStack(Item.getItemById(4)));
	        			break;
	        		case 3:
	        			this.getInventory(world, x, y, z).setInventorySlotContents(0, new ItemStack(Item.getItemById(5)));
	        			break;
	        		case 4:
	        			this.getInventory(world, x, y, z).setInventorySlotContents(0, new ItemStack(Item.getItemById(6)));
	        			break;
	        		case 5:
	        			this.getInventory(world, x, y, z).setInventorySlotContents(0, new ItemStack(Item.getItemById(7)));
	        			break;
	        		default:
	        			break;
	        	}
	        	((TierChestInventory)world.getTileEntity(x, y, z)).setActive(false);
	        }
        	
        	super.onBlockActivated(world,x, y, z,  player,  metadata,  what,  these,  are);
            return true;
            
        }
        else
        {
        	super.onBlockActivated(world,x, y, z,  player,  metadata,  what,  these,  are);
        	return true;
        }
		
		
        
    }
	
	@SideOnly(Side.CLIENT)
    public void randomDisplayTick(World p_149734_1_, int p_149734_2_, int p_149734_3_, int p_149734_4_, Random p_149734_5_)
    {
        if (p_149734_5_.nextInt(100) == 0)
        {
            p_149734_1_.playSound((double)p_149734_2_ + 0.5D, (double)p_149734_3_ + 0.5D, (double)p_149734_4_ + 0.5D, "portal.portal", 0.5F, p_149734_5_.nextFloat() * 0.4F + 0.8F, false);
        }
        //random.levelup 
        //random.orb 
    }
	
	@Override
	public void onBlockPlacedBy(World worldObj, int xPos, int yPos, int zPos, EntityLivingBase player, ItemStack itemToPlace) {
		if(!worldObj.isRemote) {
			worldObj.setBlockMetadataWithNotify(xPos, yPos, zPos, this.count, 2);
			this.count++;
			if(this.count>=6)
			{
				this.count=0;
			}
		}
	}
	
	//TODO FIX getIcon setup so it isn't set directionally.
	 	@Override
	 	@SideOnly(Side.CLIENT)
	    public IIcon getIcon(int p_149691_1_, int p_149691_2_)
	    {
	        if (p_149691_1_ != 1 && (p_149691_1_ != 0))
	        {
	        	if (p_149691_2_ < 0 || p_149691_2_ >= this.field_150158_M.length)
	        	{
	        		p_149691_2_ = 0;
	        	}
	        	return this.field_150158_M[p_149691_2_];
	        }
	        else
	        {
	        	if (p_149691_2_ < 0 || p_149691_2_ >= this.field_150158_M.length)
                {
                    p_149691_2_ = 0;
                }
	            return this.field_150159_N[p_149691_2_];
	        }
	    }
	
	 	
	 
	 	@Override
	 	@SideOnly(Side.CLIENT)
	    public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List p_149666_3_)
	    {
	        p_149666_3_.add(new ItemStack(p_149666_1_, 1, 0));
	    }
	 
	 	@Override
	 	@SideOnly(Side.CLIENT)
	    public void registerBlockIcons(IIconRegister p_149651_1_)
	    {
	        this.field_150158_M = new IIcon[field_150156_b.length];
	        this.field_150159_N = new IIcon[field_150156_b.length];

	        for (int i = 0; i < this.field_150158_M.length; ++i)
	        {
	        	String s = config.inventoryAsset+ "_" + field_150156_b[i];
	        	this.field_150158_M[i] = p_149651_1_.registerIcon(PolycraftMod.getAssetName(config.inventoryAsset+ "_" + field_150156_b[i]));
	        	this.field_150159_N[i] = p_149651_1_.registerIcon(PolycraftMod.getAssetName(config.inventoryAsset+ "_side_" + field_150156_b[i]));
	        }

	        
	    }

}
