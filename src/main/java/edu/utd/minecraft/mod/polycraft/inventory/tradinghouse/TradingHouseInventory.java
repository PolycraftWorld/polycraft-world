package edu.utd.minecraft.mod.polycraft.inventory.tradinghouse;

import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.GuiContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;
import edu.utd.minecraft.mod.polycraft.inventory.CoinOperatedInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftCraftingContainerGeneric;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.CraftingBehavior;

public class TradingHouseInventory extends CoinOperatedInventory {

	public static final int slotIndexInputSmall;
	public static final int slotIndexInputMedium;
	public static final int slotIndexInputLarge;
	public static final int slotIndexInputGiant;
	public static final int slotIndexInputFee;
	public static final int slotIndexFirstOutput;
	public static final int slotIndexLastOutput;

	public static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	static {
		guiSlots.add(GuiContainerSlot.createInput(slotIndexInputSmall = guiSlots.size(), 0, 0, 8, 0));
		guiSlots.add(GuiContainerSlot.createInput(slotIndexInputMedium = guiSlots.size(), 1, 0, 8, 0));
		guiSlots.add(GuiContainerSlot.createInput(slotIndexInputLarge = guiSlots.size(), 2, 0, 8, 0));
		guiSlots.add(GuiContainerSlot.createInput(slotIndexInputGiant = guiSlots.size(), 3, 0, 8, 0));
		guiSlots.add(GuiContainerSlot.createInput(slotIndexInputFee = guiSlots.size(), 4, 0, 8, 36));
		slotIndexFirstOutput = guiSlots.size();
		for (int x = 0; x < 4; x++)
		{
			guiSlots.add(new GuiContainerSlot(guiSlots.size(), SlotType.OUTPUT, 0, x, 98 + x * 18, 18)); //output
		}
		slotIndexLastOutput = guiSlots.size() - 1;
	}

	private static Inventory config;

	public static final void register(final Inventory config) {
		TradingHouseInventory.config = config;
		config.containerType = PolycraftContainerType.TRADING_HOUSE;
		PolycraftInventory.register(new PolycraftInventoryBlock(config, TradingHouseInventory.class));
	}

	public TradingHouseInventory() {
		super(PolycraftContainerType.TRADING_HOUSE, config, 84, -1);
		this.addBehavior(new CraftingBehavior<TradingHouseInventory>());
	}

	@Override
	public PolycraftCraftingContainer getCraftingContainer(final InventoryPlayer playerInventory) {
		return new PolycraftCraftingContainerGeneric(this, playerInventory, playerInventoryOffset, true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public PolycraftInventoryGui getGui(final InventoryPlayer playerInventory) {
		return new PolycraftInventoryGui(this, playerInventory, 166);
	}
}
