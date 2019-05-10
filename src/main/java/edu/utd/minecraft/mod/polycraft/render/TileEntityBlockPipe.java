package edu.utd.minecraft.mod.polycraft.render;

import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.block.BlockPipe;
import edu.utd.minecraft.mod.polycraft.config.InternalObject;
import edu.utd.minecraft.mod.polycraft.inventory.pump.Flowable;
import edu.utd.minecraft.mod.polycraft.item.ItemBlockPipe;

public class TileEntityBlockPipe extends TileEntity{
	
    private byte directionIn;
    
    protected static InternalObject config;
    
	public static final void register(final InternalObject pipeConfig) {
		
		TileEntityBlockPipe.config = pipeConfig;
		final BlockPipe bp = new BlockPipe(pipeConfig);
		BlockPipeRenderingHandler bprh = new BlockPipeRenderingHandler(pipeConfig);
		
		PolycraftRegistry.registerBlockWithItem(pipeConfig.gameID, pipeConfig.name, bp, pipeConfig.itemID, pipeConfig.itemName, ItemBlockPipe.class, new Object[]{});
		
		GameRegistry.registerTileEntity(TileEntityBlockPipe.class, pipeConfig.tileEntityGameID);
		RenderingRegistry.registerBlockHandler(bprh.getRenderId(), bprh);
	}
	
	@Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readFromNBT(packet.func_148857_g());
    }
	
	@Override
    public void readFromNBT(NBTTagCompound p_145839_1_)
    {
        super.readFromNBT(p_145839_1_);
        this.directionIn = p_145839_1_.getByte("d");
    }

	@Override
    public void writeToNBT(NBTTagCompound p_145841_1_)
    {
        super.writeToNBT(p_145841_1_);
        p_145841_1_.setByte("d", this.directionIn);
    }
	
	public static void setDirectionIn(World worldObj, int xCoord, int yCoord, int zCoord) {
		TileEntityBlockPipe bte = (TileEntityBlockPipe) worldObj.getTileEntity(xCoord, yCoord, zCoord);
		bte.clearDirectionIn();
		for (ForgeDirection testdir: ForgeDirection.values())
			if (isDirectionIn(worldObj,testdir, xCoord, yCoord, zCoord))
				bte.addDirectionIn(testdir);
	}
	
	private static boolean isDirectionIn(World worldObj, ForgeDirection testdir, int xCoord, int yCoord, int zCoord)
	{
		if (PolycraftMod.getInventoryAt(worldObj, xCoord+testdir.offsetX, yCoord+testdir.offsetY, zCoord+testdir.offsetZ) != null)
			return true;
		if ((worldObj.getBlock(xCoord+testdir.offsetX, yCoord+testdir.offsetY, zCoord+testdir.offsetZ) instanceof Flowable) && (worldObj.getBlockMetadata(xCoord+testdir.offsetX, yCoord+testdir.offsetY, zCoord+testdir.offsetZ)==testdir.getOpposite().ordinal()))
			return true;
		return false;		
	}
	
	private void clearDirectionIn()
	{
		this.directionIn = 0;
	}
	
	private void addDirectionIn(final ForgeDirection dir)
	{
		this.directionIn |= 1 << dir.ordinal();
	}
	
	public boolean hasDirectionIn(final ForgeDirection dir)
	{
		return (directionIn & (1 << dir.ordinal())) > 0;
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
}