package edu.utd.minecraft.mod.polycraft.render;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
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
		//RenderingRegistry.registerBlockHandler(bprh.getRenderId(), bprh); TODO: Fix this?
	}
	
	@Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return new S35PacketUpdateTileEntity(pos, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
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
	
	public static void setDirectionIn(World worldObj, BlockPos pos) {
		TileEntityBlockPipe bte = (TileEntityBlockPipe) worldObj.getTileEntity(pos);
		bte.clearDirectionIn();
		for (EnumFacing testdir: EnumFacing.values())
			if (isDirectionIn(worldObj,testdir, pos))
				bte.addDirectionIn(testdir);
	}
	
	private static boolean isDirectionIn(World worldObj, EnumFacing testdir, BlockPos pos)
	{
		if (PolycraftMod.getInventoryAt(worldObj, pos.offset(testdir)) != null)
			return true;
		if ((worldObj.getBlockState(pos.offset(testdir)).getBlock() instanceof Flowable)
                && (worldObj.getBlockState(pos.offset(testdir)).getBlock().getMetaFromState(worldObj.getBlockState(pos.offset(testdir)))==testdir.getOpposite().ordinal()))
			return true;
		return false;		
	}
	
	private void clearDirectionIn()
	{
		this.directionIn = 0;
	}
	
	private void addDirectionIn(final EnumFacing dir)
	{
		this.directionIn |= 1 << dir.ordinal();
	}
	
	public boolean hasDirectionIn(final EnumFacing dir)
	{
		return (directionIn & (1 << dir.ordinal())) > 0;
	}

    public static EntityItem func_145897_a(World worldObj, double p_145897_1_, double p_145897_3_, double p_145897_5_)
    {
        List list = worldObj.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.fromBounds(p_145897_1_, p_145897_3_, p_145897_5_, p_145897_1_ + 1.0D, p_145897_3_ + 1.0D, p_145897_5_ + 1.0D), EntitySelectors.selectAnything);
        return list.size() > 0 ? (EntityItem)list.get(0) : null;
    }


    /**
     * Gets the world X position for this pipe entity.
     */
    public double getXPos()
    {
        return (double)this.pos.getX();
    }

    /**
     * Gets the world Y position for this pipe entity.
     */
    public double getYPos()
    {
        return (double)this.pos.getY();
    }

    /**
     * Gets the world Z position for this pipe entity.
     */
    public double getZPos()
    {
        return (double)this.pos.getZ();
    }
}