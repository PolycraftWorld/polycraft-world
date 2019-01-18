package edu.utd.minecraft.mod.polycraft.inventory.cannon;

import java.util.List;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.GuiContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftCraftingContainerGeneric;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import edu.utd.minecraft.mod.polycraft.inventory.territoryflag.TerritoryFlagBlock;
import edu.utd.minecraft.mod.polycraft.inventory.territoryflag.TerritoryFlagInventory;
import edu.utd.minecraft.mod.polycraft.inventory.tierchest.TierChestGui;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class CannonInventory extends PolycraftInventory {
	
	public double velocity;
	public double theta;
	public double mass;
	
	public static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	static {
		
	}

	
	private static Inventory config;


	public CannonInventory() {
		super(PolycraftContainerType.CANNON, config);
		this.velocity=0.1;
		this.theta=0.0;
		this.mass=1.0;

		
	}
	
	public static final void register(final Inventory config) {
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
		return new CannonGui(this, playerInventory,this.getWorldObj());
	}
	
	
//	@Override
//	public void readFromNBT(NBTTagCompound tag) {
//		super.readFromNBT(tag);
//		this.velocity = tag.getDouble("Velocity");
//		this.theta = tag.getDouble("Theta");
//		this.mass = tag.getDouble("Mass");
//	}
//
//	@Override
//	public void writeToNBT(NBTTagCompound tag) {
//		super.writeToNBT(tag);
//		tag.setDouble("Velocity", this.velocity);
//		tag.setDouble("Theta", this.theta);
//		tag.setDouble("Mass", this.mass);
//	}
}
