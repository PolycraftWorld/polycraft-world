package edu.utd.minecraft.mod.polycraft.inventory.cannon;

import java.lang.reflect.Type;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.GuiContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;
import edu.utd.minecraft.mod.polycraft.inventory.InventoryBehavior;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftCraftingContainerGeneric;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import edu.utd.minecraft.mod.polycraft.inventory.territoryflag.TerritoryFlagBlock;
import edu.utd.minecraft.mod.polycraft.inventory.territoryflag.TerritoryFlagInventory;
import edu.utd.minecraft.mod.polycraft.inventory.tierchest.TierChestGui;
import edu.utd.minecraft.mod.polycraft.minigame.PolycraftMinigame;
import edu.utd.minecraft.mod.polycraft.minigame.PolycraftMinigameManager;
import edu.utd.minecraft.mod.polycraft.scoreboards.ClientScoreboard;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class CannonInventory extends PolycraftInventory {
	
	public double velocity;
	public double theta;
	public double phi;
	public double mass;
	
	
	
	public static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	static {
		guiSlots.add(GuiContainerSlot.createInput(0, 0, 0, 150, -10));
	}

	
	private static Inventory config;
	
	

	public CannonInventory() {
		super(PolycraftContainerType.CANNON, config);
		this.velocity=1.0;
		this.theta=0.0;
		this.phi=0;
		this.mass=1.0;
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
        this.getWorldObj().setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, this.blockMetadata, 3);
        this.getWorldObj().notifyBlockOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getWorldObj().getBlock(this.xCoord, this.yCoord, this.zCoord));
        this.getWorldObj().markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        this.markDirty();
	}
	
	public static void register(final Inventory config) {
		CannonInventory.config = config;
		config.containerType = PolycraftContainerType.CANNON;
		PolycraftInventory.register(new CannonBlock(config, CannonInventory.class));
	}
	
	@Override
	public PolycraftCraftingContainer getCraftingContainer(final InventoryPlayer playerInventory) {
		return new PolycraftCraftingContainerGeneric(this, playerInventory, 128); 
	}

	@Override
	@SideOnly(Side.CLIENT)
	public PolycraftInventoryGui getGui(final InventoryPlayer playerInventory) {
		// return new PolycraftInventoryGui(this, playerInventory, 133, false);
		return new CannonGui(this, playerInventory);
	}
	
//	@Override
//	public void readFromNBT(NBTTagCompound tag) {
//		super.readFromNBT(tag);
//		this.velocity=tag.getDouble("velocity");
//		this.theta=tag.getDouble("theta");
//		this.mass=tag.getDouble("mass");
//
//	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		CannonInventory cannon =(CannonInventory) this.getWorldObj().getTileEntity(this.xCoord, this.yCoord, this.zCoord);
		double velocity =cannon.velocity;
		double theta =cannon.theta;
		double mass =cannon.mass;
		double phi = cannon.phi;
		tag.setDouble("velocity", velocity);
		tag.setDouble("theta", theta);
		tag.setDouble("mass", mass);

	}
	
	   @Override
	    public Packet getDescriptionPacket() {
	        NBTTagCompound tag = new NBTTagCompound();
	        this.writeToNBT(tag);
	        return new S35PacketUpdateTileEntity(xCoord, yCoord, xCoord, 1, tag);
	    }

	    @Override
	    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
	        readFromNBT(packet.func_148857_g());
	    }

	

}
