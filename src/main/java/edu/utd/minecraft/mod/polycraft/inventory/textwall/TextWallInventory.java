package edu.utd.minecraft.mod.polycraft.inventory.textwall;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiConsent;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.GuiContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.CraftingBehavior;
import edu.utd.minecraft.mod.polycraft.inventory.tradinghouse.TradingHouseInventory;
import net.minecraft.block.BlockSign;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;

public class TextWallInventory extends PolycraftInventory {
	
	private static Inventory config;
	public static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	
	public static final void register(final Inventory config) {
		TextWallInventory.config = config;
		config.containerType = PolycraftContainerType.TEXT_WALL;
		PolycraftInventory.register(new TextWallBlock(config, TextWallInventory.class));
	}

	//BlockSign sign = new BlockSign(null, isVanilla);
	public TextWallInventory() {
		super(PolycraftContainerType.TEXT_WALL, TextWallInventory.config);
		// TODO Auto-generated constructor stub
	}
	
	public TextWallInventory(PolycraftContainerType containerType, Inventory config) {
		super(PolycraftContainerType.TEXT_WALL, TextWallInventory.config);
		// TODO Auto-generated constructor stub
	}

}
