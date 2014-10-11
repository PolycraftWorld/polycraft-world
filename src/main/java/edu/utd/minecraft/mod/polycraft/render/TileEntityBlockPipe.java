package edu.utd.minecraft.mod.polycraft.render;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;

import java.util.List;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.block.BlockPipe;
import edu.utd.minecraft.mod.polycraft.client.RenderIDs;
import edu.utd.minecraft.mod.polycraft.config.InternalObject;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.pump.Flowable;
import edu.utd.minecraft.mod.polycraft.item.ItemPipe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockHopper;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class TileEntityBlockPipe extends TileEntity{
	
	public int customField;
    private String customNameTag;
    
    public short directionIn;
    private static final String __OBFID = "CL_00010001";
    
    protected static InternalObject config;
    
    
	public static final void register(final InternalObject pipeConfig, final InternalObject pipeItem) {
		
		TileEntityBlockPipe.config = pipeConfig;
		BlockPipe bp = new BlockPipe(pipeConfig, TileEntityBlockPipe.class);
		BlockPipeRenderingHandler bprh = new BlockPipeRenderingHandler(pipeConfig);
		
		PolycraftRegistry.registerBlockWithItem(pipeConfig.gameID, pipeConfig.name, bp, pipeItem.gameID, pipeItem.name, ItemPipe.class, new Object[]{});
		
		GameRegistry.registerTileEntity(bp.tileEntityClass, pipeConfig.tileEntityGameID);
		RenderingRegistry.registerBlockHandler(bprh.getRenderId(), bprh);
		
		
	}
    
	
	@Override
    public void readFromNBT(NBTTagCompound p_145839_1_)
    {
        super.readFromNBT(p_145839_1_);
        NBTTagList nbttaglist = p_145839_1_.getTagList("Items", 10);
        //this.field_145900_a = new ItemStack[this.getSizeInventory()];

        if (p_145839_1_.hasKey("CustomName", 8))
        {
            this.customNameTag = p_145839_1_.getString("CustomName");
        }
        if (p_145839_1_.hasKey("DirectionIn", 8))
        {
            this.directionIn = p_145839_1_.getShort("DirectionIn");
        }

    }
	
	 public void setDirectionIn(short dir)
	 {
		 
		 	this.directionIn = dir;	 

	 }
	
	

	@Override
    public void writeToNBT(NBTTagCompound p_145841_1_)
    {
        super.writeToNBT(p_145841_1_);
        //NBTTagList nbttaglist = new NBTTagList();
        p_145841_1_.setShort("DirectionIn", this.directionIn);
        

    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    public void markDirty()
    {
        super.markDirty();
    }

    /**
     * Would be nice to know what this does
     */
    
    public void setCustomName(String customName)
    {
        this.customNameTag = customName;
    }

  
    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : player.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void updateEntity()
    {
       //nothing on update for now
    }


    public static EntityItem func_145897_a(World worldObj, double p_145897_1_, double p_145897_3_, double p_145897_5_)
    {
        List list = worldObj.selectEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getBoundingBox(p_145897_1_, p_145897_3_, p_145897_5_, p_145897_1_ + 1.0D, p_145897_3_ + 1.0D, p_145897_5_ + 1.0D), IEntitySelector.selectAnything);
        return list.size() > 0 ? (EntityItem)list.get(0) : null;
    }


    /**
     * Gets the world X position for this pipe entity.
     */
    public double getXPos()
    {
        return (double)this.xCoord;
    }

    /**
     * Gets the world Y position for this pipe entity.
     */
    public double getYPos()
    {
        return (double)this.yCoord;
    }

    /**
     * Gets the world Z position for this pipe entity.
     */
    public double getZPos()
    {
        return (double)this.zCoord;
    }
    
	public static int getDirectionFromMetadata(int metaData) {
		return metaData & 7;
	}
	

}